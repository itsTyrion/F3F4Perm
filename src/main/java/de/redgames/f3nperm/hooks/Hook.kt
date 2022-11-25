package de.redgames.f3nperm.hooks

import de.redgames.f3nperm.F3NFixPlugin

interface Hook {
    fun getName(): String
    fun register(plugin: F3NFixPlugin)
    fun unregister(plugin: F3NFixPlugin)
}