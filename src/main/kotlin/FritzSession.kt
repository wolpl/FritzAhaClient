package com.github.wolpl.fritzahaclient

import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * A client for accessing the FritzBox AHA-HTTP interface using a username and a password.
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
        println("New SID: $sid")
    }

    private fun getSessionId(username: String = this.username, password: String = this.password): String {
        fun getXmlValue(document: String, tag: String): String = document.split("<$tag>")[1].split("</$tag>")[0]


        val url = "http://fritz.box/login_sid.lua"
        with(URL(url).openConnection() as HttpURLConnection) {
            requestMethod = "GET"
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
    }

    private fun createResponse(challenge: String, password: String): String {
        val text = "$challenge-$password".toByteArray(Charset.forName("utf-16le"))
        val digest = MessageDigest.getInstance("md5").digest(text)
        return challenge + "-" + digest.toHexString().toLowerCase()
    }

    private fun httpGet(url: String, isFirstTry: Boolean = true): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return when {
            response.isSuccessful -> response.body()!!.string()
            isFirstTry -> {
                getSessionId()
                httpGet(url, false)
            }
            else -> throw RuntimeException("Cannot perform HTTP request!")
        }
    }

    private fun request(command: String, ain: String? = null, param: String? = null): String {
        var url = "http://fritz.box/webservices/homeautoswitch.lua?switchcmd=$command&sid=$sid"
        if (ain != null) url += "&ain=$ain"
        if (param != null) url += "&param=$param"
        return httpGet(url)
    }


    /**
     * @return The latest temperature value for the actor specified by [ain].
     */
    fun getTemperature(ain: String): Float = request("gettemperature", ain).toFloat() / 10

    /**
     * @return Basic information about all smart home devices as xml.
     */
    fun getDeviceListInfos(): String = request("getdevicelistinfos")

    /**
     * @return A comma separated list containing the ain/mac of all known switches.
     */
    fun getSwitchList(): String = request("getswitchlist")

    /**
     * Turns on the switch with the given [ain].
     * @return The new state of the switch. Should always be On.
     */
    fun setSwitchOn(ain: String): SwitchState = SwitchState.parse(request("setswitchon", ain))

    /**
     * Turns off the switch with the given [ain].
     * @return The new state of the switch. Should always be Off.
     */
    fun setSwitchOff(ain: String): SwitchState = SwitchState.parse(request("setswitchoff", ain))

    /**
     * Toggles the switch specified by [ain] on or off.
     * @return The new state of the switch.
     */
    fun setSwitchToggle(ain: String): SwitchState = SwitchState.parse(request("setswitchtoggle", ain))

    /**
     * @return The current state of the switch specified by [ain].
     */
    fun getSwitchState(ain: String): SwitchState = SwitchState.parse(request("getswitchstate", ain))

    /**
     * @return true if the switch specified by [ain] is connected, false otherwise.
     */
    fun getSwitchPresent(ain: String): Boolean = request("getswitchpresent", ain) == "1"

    /**
     * @return The power in mW that is currently taken from the switch specified by [ain].
     */
    fun getSwitchPower(ain: String): Float? = request("getswitchpower", ain).toFloatOrNull()

    /**
     * @return The energy in Wh taken from the switch specified by [ain] since its last reset.
     */
    fun getSwitchEnergy(ain: String): Float? = request("getswitchenergy", ain).toFloatOrNull()

    /**
     * @return The name of the actor specified by [ain].
     */
    fun getSwitchName(ain: String): String = request("getswitchname", ain)
}
