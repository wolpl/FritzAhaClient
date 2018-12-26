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

import kotlin.math.roundToInt

/**
 * Represents a temperature setting for a HKR device.
 * @since 0.2.0
 */
data class HkrTemperature(val type: Type, val temperature: Float?) {
    enum class Type { On, Off, Temperature }

    init {
        require(type != Type.Temperature || (temperature != null)) { "If type is Type.Temperature temperature cannot be null!" }
    }

    /**
     * Creates a new [HkrTemperature] with [type] = [Type.Temperature] and the specified temperature.
     */
    constructor(temperature: Float) : this(Type.Temperature, temperature)

    /**
     * The technical code representing the [HkrTemperature].
     * It is
     * - `254` if [type] is [Type.On]
     * - `253` if [type] is [Type.Off]
     * - `[temperature] * 2` if [type] is [Type.Temperature]. The result will be rounded to the next [Int] in this case.
     */
    val code: Int by lazy {
        when (type) {
            Type.On -> 254
            Type.Off -> 253
            Type.Temperature -> (temperature!! * 2).roundToInt()
        }
    }

    companion object {
        /**
         * Creates a [HkrTemperature] from the specified [code].
         */
        fun fromCode(code: Int) = when (code) {
            254 -> HkrTemperature(Type.On, null)
            253 -> HkrTemperature(Type.Off, null)
            in 16..56 -> HkrTemperature((code / 2).toFloat())
            else -> throw IllegalArgumentException("code must be in 16..56 or 253 or 254 but was $code!")
        }
    }
}