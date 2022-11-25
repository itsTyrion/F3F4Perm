package de.redgames.f3nperm.hooks

import de.redgames.f3nperm.F3NFixPlugin
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.EventSubscription
import net.luckperms.api.event.user.UserDataRecalculateEvent
import org.bukkit.Bukkit

class LuckPermsHook : Hook {
    private var subscription: EventSubscription<UserDataRecalculateEvent>? = null

    override fun getName(): String = "LuckPerms"

    override fun register(plugin: F3NFixPlugin) {
        if (plugin.server.pluginManager.getPlugin("LuckPerms") == null) {
            return
        }
        subscription = LuckPermsProvider.get().eventBus.subscribe(UserDataRecalculateEvent::class.java) { e ->

            Bukkit.getPlayer(e.user.uniqueId)?.let(plugin.provider::update)
        }
        plugin.logger.info("Successfully hooked into LuckPerms!")
    }

    override fun unregister(plugin: F3NFixPlugin) {
        subscription?.close()
    }
}