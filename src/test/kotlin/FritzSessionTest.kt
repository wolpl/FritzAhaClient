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
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FritzSessionTest {
    private val username = File(FritzSessionTest::class.java.getResource("FritzboxCredentials").file).readLines()[0]
    private val password = File(FritzSessionTest::class.java.getResource("FritzboxCredentials").file).readLines()[1]

    @Test
    fun `getDeviceListInfosRaw runs`() {
        val s = FritzSession(username, password)
        println(s.getDeviceListInfosRaw())
    }

    @Test
    fun `getSwitchList runs`() {
        val s = FritzSession(username, password)
        println(s.getSwitchList())
    }

    @Test
    fun `getTemplateListInfosRaw runs`() {
        val s = FritzSession(username, password)
        println(s.getTemplateListInfosRaw())
    }

    @Test
    fun `wrong credentials fail`() {
        val s = FritzSession(username, password + "WRONG")
        val exception = assertThrows<IllegalStateException> {
            println(s.getDeviceListInfosRaw())
        }
        assert(exception.message!!.startsWith("Client does not have a valid sid. Request is aborted!"))
        Thread.sleep(5000)
    }
}