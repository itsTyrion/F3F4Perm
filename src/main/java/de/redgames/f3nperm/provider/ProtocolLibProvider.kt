package de.redgames.f3nperm.provider

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import de.redgames.f3nperm.F3NFixPlugin
import de.redgames.f3nperm.OpPermissionLevel.Companion.fromStatusByte
import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException

class ProtocolLibProvider(private val permPlugin: F3NFixPlugin) : Provider {
    private var manager: ProtocolManager? = null

    override fun register() {
        manager = ProtocolLibrary.getProtocolManager()

        manager!!.addPacketListener(object : PacketAdapter(permPlugin, PacketType.Play.Server.ENTITY_STATUS) {
            override fun onPacketSending(event: PacketEvent) {
                if (event.packetType !== PacketType.Play.Server.ENTITY_STATUS || event.isPlayerTemporary) {
                    return
                }
                val packet = event.packet

                if (packet.integers.read(0) == event.player.entityId) {
                    if (fromStatusByte(permPlugin.serverVersion, packet.bytes.read(0)) != null) {
                        val targetLevel = permPlugin.getF3NFixPermissionLevel(event.player)
                        packet.bytes.write(0, targetLevel.toStatusByte(permPlugin.serverVersion))
                    }
                }
            }
        })
    }

    override fun unregister(plugin: F3NFixPlugin) {
        manager?.removePacketListeners(plugin)
    }

    override fun update(player: Player) {
        val level = permPlugin.getF3NFixPermissionLevel(player)
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_STATUS)
        packet.integers.write(0, player.entityId)
        packet.bytes.write(0, level.toStatusByte(permPlugin.serverVersion))
        try {
            manager?.sendServerPacket(player, packet, false)
        } catch (e: InvocationTargetException) {
            throw ProviderException("Could not send status packet!", e)
        }
    }
}