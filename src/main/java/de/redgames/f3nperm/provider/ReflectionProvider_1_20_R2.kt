package de.redgames.f3nperm.provider

import de.redgames.f3nperm.F3NFixPlugin
import de.redgames.f3nperm.reflection.ReflectionException
import de.redgames.f3nperm.reflection.Reflections

open class ReflectionProvider_1_20_R2(plugin: F3NFixPlugin) : ReflectionProvider_1_19_R3(plugin) {

    @Throws(ReflectionException::class)
    override fun getNetworkManager(playerConnection: Any): Any = Reflections.get(playerConnection, "c")

    @Throws(ReflectionException::class)
    override fun getChannel(networkManager: Any): Any = Reflections.getPrivate(networkManager, "n")

    @Throws(ReflectionException::class)
    override fun sendPacket(playerConnection: Any, packet: Any) {
        Reflections.call(playerConnection, "b(net.minecraft.network.protocol.Packet)", packet)
    }
}