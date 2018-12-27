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

open class HttpConnector : IFritzboxConnector {

    companion object {
        private val logger = LoggerFactory.getLogger(HttpConnector::class.java)
        const val EmptySid = "0000000000000000"

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

    private fun httpGet(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return when {
            response.isSuccessful -> response.body()!!.string()
            else -> throw RuntimeException("HTTP Request was not successful! Got response code ${response.code()} ${response.message()}")
        }
    }

    override fun request(sid: String, command: String, ain: String?, param: String?): String {
        if (sid == EmptySid) throw IllegalStateException("Client does not have a valid sid. Request is aborted! Check the used credentials!")
        var url = "http://fritz.box/webservices/homeautoswitch.lua?switchcmd=$command&sid=$sid"
        if (ain != null) url += "&ain=$ain"
        if (param != null) url += "&param=$param"
        return httpGet(url)
    }

    override fun getSessionId(username: String, password: String): String {
        fun getXmlValue(document: String, tag: String): String = document.split("<$tag>")[1].split("</$tag>")[0]

        fun createResponse(challenge: String, password: String): String {
            val text = "$challenge-$password".toByteArray(Charset.forName("utf-16le"))
            val digest = MessageDigest.getInstance("md5").digest(text)
            return challenge + "-" + digest.toHexString().toLowerCase()
        }

        val url = "http://fritz.box/login_sid.lua"
        var doc = httpGet(url)
        var sid = getXmlValue(doc, "SID")
        if (sid == "0000000000000000") {
            val challenge = getXmlValue(doc, "Challenge")
            val response = createResponse(challenge, password)
            doc = httpGet("http://fritz.box/login_sid.lua?username=$username&response=$response")
            sid = getXmlValue(doc, "SID")
        }
        logger.debug("Created new SID: $sid")
        return sid
    }
}