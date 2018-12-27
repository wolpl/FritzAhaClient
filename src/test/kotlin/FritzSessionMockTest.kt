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
import com.github.wolpl.fritzahaclient.FritzSession
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FritzSessionMockTest {

    private val session = FritzSession("username", "password", TestFritzboxConnector())

    @Test
    fun `getDeviceListInfosRaw runs`() {
        println(session.getDeviceListInfosRaw())
    }

    @Test
    fun getSwitchList() {
        val result = session.getSwitchList()
        assert(result == listOf(TestFritzboxConnector.validAinSwitch))
    }

    @Test
    fun `getTemplateListInfosRaw runs`() {
        println(session.getTemplateListInfosRaw())
    }

    @Test
    fun getTemperature() {
        val result = session.getTemperature(TestFritzboxConnector.validAinHkr)
        assert(result == 24.5f)
    }

    @Test
    fun `wrong credentials fail`() {
        val s = FritzSession("username", "password" + "WRONG", TestFritzboxConnector())
        val exception = assertThrows<Exception> {
            println(s.getDeviceListInfosRaw())
        }
        assert(exception.message!!.startsWith("Client does not have a valid sid. Request is aborted!"))
    }
}