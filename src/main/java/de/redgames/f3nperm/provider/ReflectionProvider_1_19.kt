package de.redgames.f3nperm.provider

import de.redgames.f3nperm.F3NFixPlugin
import de.redgames.f3nperm.reflection.ReflectionException
import de.redgames.f3nperm.reflection.Reflections

class ReflectionProvider_1_19(plugin: F3NFixPlugin) : ReflectionProvider_1_18_2(plugin) {

    @Throws(ReflectionException::class)
    override fun getNetworkManager(playerConnection: Any): Any = Reflections.getPrivate(playerConnection, "b")
}