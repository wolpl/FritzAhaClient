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

import com.github.wolpl.fritzahaclient.IFritzboxConnector

internal class TestFritzboxConnector : IFritzboxConnector {
    companion object {
        private const val validSid = "1234567891234567"
        const val validAinHkr = "ainHkr1"
        const val validAinSwitch = "ainSwitch1"
        const val invalidAin = "invalAin1"
        private val deviceListInfos = """
<devicelist version="1">
  <device identifier="08761 0000434" id="17" functionbitmask="896" fwversion="03.33" manufacturer="AVM"
productname="FRITZ!DECT 200">
    <present>1</present>
    <name>Steckdose</name>
<switch>
<state>1</state><mode>auto</mode>
<lock>0</lock><devicelock>0</devicelock>
</switch>
    <powermeter>
<power>0</power><energy>707</energy><voltage>230252</voltage>
   </powermeter>
    <temperature><celsius>285</celsius><ofset>0</ofset></temperature>
  </device>
  <device identifier="08761 1048079" id="16" functionbitmask="1280" fwversion="03.33" manufacturer="AVM"
productname="FRITZ!DECT Repeater 100">
    <present>1</present>
    <name>FRITZ!DECT Rep 100 #1</name>
    <temperature><celsius>288</celsius><ofset>0</ofset></temperature>
      </device>
  <group identifier="65:3A:18-900" id="900" functionbitmask="512" fwversion="1.0" manufacturer="AVM"
productname="">
    <present>1</present>
    <name>Gruppe</name>
    <switch><state>1</state><mode>auto</mode><lock/><devicelock/></switch>
    <groupinfo><masterdeviceid>0</masterdeviceid><members>17</members></groupinfo>
  </group>
</devicelist>
        """.trimIndent()
        private val deviceStats = """
            <devicestats>
<temperature>
<stats count="96" grid="900">
245,255,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,-,
-,-,-,-,-,-,-,-,-,-,-,-,-
</stats>
</temperature>
<voltage>
<stats count="360" grid="10">
228615,228615,228615,228615,228615,228615,228615,228615,228615,228615,228615,228615,229040,229040,22
9040,229040,229040,229040,229040,229040,229040,229040,229040,229040,229882,229882,229882,229882,2298
82,229882,229882,229882,229882,229882,229882,229882,229796,229796,229796,229796,229796,229796,229796
,229796,229796,229796,229796,229796,228864,228864,228864,228864,228864,228864,228864,228864,228864,2
28864,228864,228864,229059,229507,228667,228507,228919,229180,229245,229067,229245,228832,229352,229
041,229041,229041,229041,229041,229041,229041,229041,226194,226194,226194,226194,226194,226194,22619
4,226194,226194,226194,226194,226194,226451,226451,226451,226451,226451,226451,226451,226451,226451,
226451,226451,226451,226894,226894,226894,226894,226894,226894,226894,226894,226894,226894,226894,22
6894,226894,227326,227541,227843,228241,227120,227171,227265,227836,227406,227598,228280,228152,2276
26,227820,227364,226775,227162,227003,228258,227902,228142,227950,227103,227003,227360,227456,227957
,227929,227274,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
</stats>
</voltage>
<power>
<stats count="360" grid="10">
1067,1067,1067,1067,1067,1067,1067,1067,1067,1067,1067,1067,1066,1066,1066,1066,1066,1066,1066,1066,106
6,1066,1066,1066,1082,1082,1082,1082,1082,1082,1082,1082,1082,1082,1082,1082,1086,1086,1086,1086,1086,1
086,1086,1086,1086,1086,1086,1086,1085,1085,1085,1085,1085,1085,1085,1085,1085,1085,1085,1085,1097,1096
,1093,1095,1096,1099,1100,1100,1098,1100,1102,1102,1102,1102,1102,1102,1102,1102,1102,1095,1095,1095,10
95,1095,1095,1095,1095,1095,1095,1095,1095,1106,1106,1106,1106,1106,1106,1106,1106,1106,1106,1106,1106,
1129,1129,1129,1129,1129,1129,1129,1129,1129,1129,1129,1129,1129,1126,1129,1134,1132,1127,1129,1133,113
8,1137,1141,1144,1146,1146,1153,1157,1165,1174,1195,1219,1246,1287,1329,1354,1341,1285,588,91,132,133,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
</stats>
</power>
</devicestats>
        """.trimIndent()
        private val templateListInfos = """
<templatelist version="1">
<template identifier="tmp653A18-38AE7FDE9" id="60001" functionbitmask="320" applymask="10">
<name>Wohnzimmer Wochenende</name>
<devices>
  <device identifier="09995 0001012"/>
  <device identifier="09995 0000645"/>
</devices>
<applymask>
  <hkr_temperature/>
  <hkr_time_table/>
</applymask>
</templatelist>
        """.trimIndent()
    }

    private var switch1State = false
    private val switch1StateString
        get() = if (switch1State) "1" else "0"

    override fun request(sid: String, command: String, ain: String?, param: String?): String {
        fun error(): String = throw RuntimeException("Error while execution request!")
        if (sid != validSid) throw Exception("Client does not have a valid sid. Request is aborted!")
        return when (command) {
            "gettemperature" -> if (ain == validAinHkr) "245" else error()
            "getdevicelistinfos" -> deviceListInfos
            "getswitchlist" -> validAinSwitch
            "setswitchon" -> if (ain == validAinSwitch) "1" else error()
            "setswitchoff" -> if (ain == validAinSwitch) "0" else error()
            "setswitchtoggle" -> {
                if (ain == validAinSwitch) {
                    switch1State = !switch1State
                    switch1StateString
                } else error()
            }
            "getswitchstate" -> if (ain == validAinSwitch) switch1StateString else error()
            "getswitchpresent" -> if (ain == validAinSwitch) "1" else "0"
            "getswitchpower" -> if (ain == validAinSwitch) "132" else error()
            "getswitchenergy" -> if (ain == validAinSwitch) "10" else error()
            "getswitchname" -> if (ain == validAinSwitch) "Switch 1" else error()
            "gethkrsoll" -> if (ain == validAinHkr) "30" else error()
            "gethkrkomfort" -> if (ain == validAinHkr) "44" else error()
            "gethkrabsenk" -> if (ain == validAinHkr) "20" else error()
            "sethkrsoll" -> if (ain == validAinHkr) "" else error()
            "getbasicdevicestats" -> if (ain == validAinHkr) deviceStats else if (ain == validAinSwitch) deviceStats else error()
            "gettemplatelistinfos" -> templateListInfos
            else -> throw Exception("Invalid command!")
        }
    }

    override fun getSessionId(username: String, password: String): String =
        if (username == "username" && password == "password") validSid else IFritzboxConnector.EmptySid

}