package de.redgames.f3nperm

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class F3NFixListener(private val plugin: F3NFixPlugin) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        // Since the op package is sent so early in the login process
        // (before the permission plugin is initialized). After everything
        // is initialized, we check again and update the player.
        Bukkit.getScheduler().runTaskLater(plugin, { ->
            if (player.isOnline) {
                plugin.provider.update(player)
            }
        }, 10L)
    }
}