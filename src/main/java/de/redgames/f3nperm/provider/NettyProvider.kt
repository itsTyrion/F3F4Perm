package de.redgames.f3nperm.provider

import de.redgames.f3nperm.F3NFixPlugin
import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

abstract class NettyProvider(protected val plugin: F3NFixPlugin) : Provider, Listener {
    private val playerHandlers = HashMap<Player, F3NFixChannelHandler>()

    abstract fun sendPacket(player: Player)
    abstract fun adjustPacket(player: Player, packet: Any)
    abstract fun getChannel(player: Player): Channel
    override fun update(player: Player) {
        sendPacket(player)
    }

    override fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun unregister(plugin: F3NFixPlugin) {
        HandlerList.unregisterAll(this)
        for (handler in playerHandlers.values) {
            handler.unregister()
        }
        playerHandlers.clear()
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val handler = playerHandlers[player]
        if (handler == null) {
            val channel = getChannel(player)
            val newHandler = F3NFixChannelHandler(player, channel)
            newHandler.register()
            playerHandlers[player] = newHandler
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val handler = playerHandlers.remove(event.player)
        handler?.unregister()
    }

    inner class F3NFixChannelHandler(private val player: Player, private val channel: Channel) : ChannelDuplexHandler() {
        fun register() {
            channel.pipeline().addBefore("packet_handler", NAME, this)
        }

        fun unregister() {
            if (channel.pipeline().get(NAME) != null)
                channel.pipeline().remove(NAME)
        }

        @Throws(Exception::class)
        override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
            adjustPacket(player, msg)
            super.write(ctx, msg, promise)
        }

    }

    companion object {
        private const val NAME = "f3nperm_handler"
    }
}