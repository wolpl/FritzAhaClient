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