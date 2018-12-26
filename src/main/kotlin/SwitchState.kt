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

/**
 * An enum representing the state of a switch.
 * @since 0.2.0
 */
enum class SwitchState {
    On, Off, Invalid;

    companion object {
        fun parse(s: String) = when (s) {
            "0" -> Off
            "1" -> On
            "inval" -> Invalid
            else -> throw IllegalArgumentException("Could not find suitable SwitchState for string \"$s\"!")
        }
    }
}