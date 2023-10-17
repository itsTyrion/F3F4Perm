package de.redgames.f3nperm.provider

import de.redgames.f3nperm.F3NFixPlugin
import de.redgames.f3nperm.OpPermissionLevel
import de.redgames.f3nperm.OpPermissionLevel.Companion.fromStatusByte
import de.redgames.f3nperm.reflection.ReflectionException
import de.redgames.f3nperm.reflection.Reflections
import io.netty.channel.Channel
import org.bukkit.entity.Player

open class ReflectionProvider_1_18_R2(plugin: F3NFixPlugin) : NettyProvider(plugin) {
    @Throws(ReflectionException::class)
    open fun getChannel(networkManager: Any): Any = Reflections.getPrivate(networkManager, "m")

    // 1.18
    @Throws(ReflectionException::class)
    open fun sendPacket(playerConnection: Any, packet: Any) {
        Reflections.call(playerConnection, "a(net.minecraft.network.protocol.Packet)", packet)
    }

    //1.17
    @Throws(ReflectionException::class)
    open fun getPlayerConnection(entityPlayer: Any): Any = Reflections.getPrivate(entityPlayer, "b")


    @Throws(ReflectionException::class)
    open fun getNetworkManager(playerConnection: Any): Any = Reflections.getPrivate(playerConnection, "a")


    @Throws(ReflectionException::class)
    fun makeStatusPacket(entityPlayer: Any?, status: Byte): Any = Reflections.make(
        "net.minecraft.network.protocol.game.PacketPlayOutEntityStatus(net.minecraft.world.entity.Entity,byte)",
        entityPlayer,
        status
    )

    @Throws(ReflectionException::class)
    fun isStatusPacket(packet: Any?): Boolean =
        Reflections.resolve("net.minecraft.network.protocol.game.PacketPlayOutEntityStatus").isInstance(packet)

    // 1.9
    override fun sendPacket(player: Player) = try {
        val level = plugin.getF3NFixPermissionLevel(player)
        val entityPlayer = getEntityPlayer(player)
        val playerConnection = getPlayerConnection(entityPlayer)

        sendPacket(playerConnection, makeStatusPacket(entityPlayer, level.toStatusByte()))
    } catch (e: ReflectionException) {
        throw ProviderException("Could not send packet!", e)
    }

    override fun adjustPacket(player: Player, packet: Any) {
        try {
            if (isStatusPacket(packet) && getStatusPacketEntity(packet) == player.entityId) {
                getStatusPacketStatus(packet) ?: return
                val level = plugin.getF3NFixPermissionLevel(player)
                setStatusPacketStatus(packet, level)
            }
        } catch (e: ReflectionException) {
            throw ProviderException("Could not adjust packet!", e)
        }
    }

    override fun getChannel(player: Player): Channel {
        return try {
            val entityPlayer = getEntityPlayer(player)
            val playerConnection = getPlayerConnection(entityPlayer)
            val networkManager = getNetworkManager(playerConnection)
            getChannel(networkManager) as Channel
        } catch (e: ReflectionException) {
            throw ProviderException("Could not retrieve channel for " + player.name + "!", e)
        }
    }

    @Throws(ReflectionException::class)
    fun getEntityPlayer(player: Player): Any = Reflections.call(player, "getHandle()")


    @Throws(ReflectionException::class)
    fun getStatusPacketEntity(packet: Any): Int = Reflections.getPrivate(packet, "a") as Int


    @Throws(ReflectionException::class)
    fun getStatusPacketStatus(packet: Any): OpPermissionLevel? =
        fromStatusByte((Reflections.getPrivate(packet, "b") as Byte))


    @Throws(ReflectionException::class)
    fun setStatusPacketStatus(packet: Any, level: OpPermissionLevel) =
        Reflections.setPrivate(packet, "b", level.toStatusByte())
}