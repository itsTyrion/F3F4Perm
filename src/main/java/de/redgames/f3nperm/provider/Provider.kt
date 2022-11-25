package de.redgames.f3nperm.provider

import de.redgames.f3nperm.F3NFixPlugin
import org.bukkit.entity.Player

interface Provider {
    fun register()
    fun unregister(plugin: F3NFixPlugin)
    fun update(player: Player)
}