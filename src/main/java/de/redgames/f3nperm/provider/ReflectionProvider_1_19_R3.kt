package de.redgames.f3nperm.provider

import de.redgames.f3nperm.F3NFixPlugin
import de.redgames.f3nperm.reflection.ReflectionException
import de.redgames.f3nperm.reflection.Reflections

open class ReflectionProvider_1_19_R3(plugin: F3NFixPlugin) : ReflectionProvider_1_19_R1(plugin) {

    @Throws(ReflectionException::class)
    override fun getNetworkManager(playerConnection: Any): Any = Reflections.getPrivate(playerConnection, "h")

    @Throws(ReflectionException::class)
    override fun getPlayerConnection(entityPlayer: Any): Any = Reflections.get(entityPlayer, "b")
}