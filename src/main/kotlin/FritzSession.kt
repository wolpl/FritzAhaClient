/*
 * FritzAhaClient is a Kotlin/JVM client for accessing the AVM Fritzbox AHA-HTTP Interface.
 * Copyright (C) 2018  wolpl (github.com/wolpl)
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.wolpl.fritzahaclient

import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * A client for accessing the FritzBox AHA-HTTP interface using a username and a password.
 * @since 0.1.0
 */
class FritzSession(private val username: String, private val password: String) {
    companion object {
        private val logger = LoggerFactory.getLogger(FritzSession::class.java)
        private const val EmptySid = "0000000000000000"

        private val hexArray = "0123456789ABCDEF".toCharArray()
        private fun ByteArray.toHexString(): String {
            val hexChars = CharArray(this.size * 2)
            for (j in this.indices) {
                val v = this[j].toInt() and 0xFF
                hexChars[j * 2] = hexArray[v.ushr(4)]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }
    }

    private var sid: String

    init {
        sid = getSessionId()
        if (sid == EmptySid) logger.warn("Could not get valid SID")
        logger.debug("New SID: $sid")
    }

    private fun getSessionId(username: String = this.username, password: String = this.password): String {
        fun getXmlValue(document: String, tag: String): String = document.split("<$tag>")[1].split("</$tag>")[0]

        val url = "http://fritz.box/login_sid.lua"
        var doc = httpGet(url)
        sid = getXmlValue(doc, "SID")
        if (sid == "0000000000000000") {
            val challenge = getXmlValue(doc, "Challenge")
            val response = createResponse(challenge, password)
            doc = httpGet("http://fritz.box/login_sid.lua?username=$username&response=$response")
            sid = getXmlValue(doc, "SID")
        }
        return sid
    }

    private fun createResponse(challenge: String, password: String): String {
        val text = "$challenge-$password".toByteArray(Charset.forName("utf-16le"))
        val digest = MessageDigest.getInstance("md5").digest(text)
        return challenge + "-" + digest.toHexString().toLowerCase()
    }

    private fun httpGet(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return when {
            response.isSuccessful -> response.body()!!.string()
            else -> throw RuntimeException("HTTP Request was not successful! Got response code ${response.code()} ${response.message()}")
        }
    }

    private fun request(command: String, ain: String? = null, param: String? = null): String {
        if (sid == EmptySid) getSessionId()
        if (sid == EmptySid) throw IllegalStateException("Client does not have a valid sid. Request is aborted! Check the used credentials!")
        var url = "http://fritz.box/webservices/homeautoswitch.lua?switchcmd=$command&sid=$sid"
        if (ain != null) url += "&ain=$ain"
        if (param != null) url += "&param=$param"
        return httpGet(url)
    }


    /**
     * @return The latest temperature value for the actor specified by [ain].
     * @since 0.1.0
     */
    fun getTemperature(ain: String): Float = request("gettemperature", ain).toFloat() / 10

    /**
     * @return Basic information about all smart home devices as xml.
     * @since 0.1.0
     */
    fun getDeviceListInfosRaw(): String = request("getdevicelistinfos")

    /**
     * @return A list containing the ain/mac of all known switches.
     * @since 0.2.0
     */
    fun getSwitchList(): List<String> = request("getswitchlist").split(",")

    /**
     * Turns on the switch with the given [ain].
     * @return The new state of the switch. Should always be On.
     * @since 0.2.0
     */
    fun setSwitchOn(ain: String): SwitchState = SwitchState.parse(request("setswitchon", ain))

    /**
     * Turns off the switch with the given [ain].
     * @return The new state of the switch. Should always be Off.
     * @since 0.2.0
     */
    fun setSwitchOff(ain: String): SwitchState = SwitchState.parse(request("setswitchoff", ain))

    /**
     * Toggles the switch specified by [ain] on or off.
     * @return The new state of the switch.
     * @since 0.2.0
     */
    fun setSwitchToggle(ain: String): SwitchState = SwitchState.parse(request("setswitchtoggle", ain))

    /**
     * @return The current state of the switch specified by [ain].
     * @since 0.2.0
     */
    fun getSwitchState(ain: String): SwitchState = SwitchState.parse(request("getswitchstate", ain))

    /**
     * @return true if the switch specified by [ain] is connected, false otherwise.
     * @since 0.2.0
     */
    fun getSwitchPresent(ain: String): Boolean = request("getswitchpresent", ain) == "1"

    /**
     * @return The power in mW that is currently taken from the switch specified by [ain].
     * @since 0.2.0
     */
    fun getSwitchPower(ain: String): Float? = request("getswitchpower", ain).toFloatOrNull()

    /**
     * @return The energy in Wh taken from the switch specified by [ain] since its last reset.
     * @since 0.2.0
     */
    fun getSwitchEnergy(ain: String): Float? = request("getswitchenergy", ain).toFloatOrNull()

    /**
     * @return The name of the actor specified by [ain].
     * @since 0.2.0
     */
    fun getSwitchName(ain: String): String = request("getswitchname", ain)

    /**
     * @return The current target temperature of the HKR device specified by [ain].
     * @since 0.2.0
     */
    fun getHkrTargetTemperature(ain: String): HkrTemperature =
        HkrTemperature.fromCode(request("gethkrtsoll", ain).toInt())

    /**
     * Sets the target temperature for the HKR device specified by [ain].
     * @since 0.2.0
     */
    fun setHkrTargetTemperature(ain: String, temperature: HkrTemperature) {
        request("sethkrtsoll", ain, temperature.code.toString())
    }

    /**
     * @return The comfort temperature that is currently set for the HKR device specified by [ain].
     * @since 0.2.0
     */
    fun getHkrComfortTemperature(ain: String): HkrTemperature =
        HkrTemperature.fromCode(request("gethkrkomfort", ain).toInt())

    /**
     * @return The eco temperature that is currently set for the HKR device specified by [ain].
     * @since 0.2.0
     */
    fun getHkrEcoTemperature(ain: String): HkrTemperature =
        HkrTemperature.fromCode(request("gethkrabsenk", ain).toInt())

    /**
     * @return Statistics about the device specified by [ain] in an xml string.
     * @since 0.2.0
     */
    fun getBasicDeviceStatsRaw(ain: String): String = request("getbasicdevicestats", ain)

    /**
     * @return Basic information about all templates as an xml string.
     * @since 0.2.0
     */
    fun getTemplateListInfosRaw(): String = request("gettemplatelistinfos")
}
