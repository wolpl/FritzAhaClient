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