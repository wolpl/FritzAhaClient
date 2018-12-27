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

import org.slf4j.LoggerFactory

/**
 * A client for accessing the FritzBox AHA-HTTP interface using a username and a password.
 * @since 0.1.0
 */
class FritzSession(
    private val username: String,
    private val password: String,
    private val connector: IFritzboxConnector = HttpConnector()
) {
    companion object {
        private val logger = LoggerFactory.getLogger(FritzSession::class.java)
    }

    private var sid: String

    init {
        sid = connector.getSessionId(username, password)
        if (sid == IFritzboxConnector.EmptySid) logger.warn("Could not get valid SID")
    }

    private fun request(command: String, ain: String? = null, param: String? = null): String {
        fun r() = connector.request(sid, command, ain, param)

        return try {
            r()
        } catch (e: IllegalStateException) {
            sid = connector.getSessionId(username, password)
            r()
        }
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
