package de.redgames.f3nperm

import de.redgames.f3nperm.ServerVersion.Companion.fromBukkitVersion
import de.redgames.f3nperm.hooks.LuckPermsHook
import de.redgames.f3nperm.provider.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.util.logging.Level

class F3NFixPlugin : JavaPlugin(), Listener {
    private val hooks = LuckPermsHook()
    lateinit var provider: Provider
    private var serverVersion: ServerVersion? = null
    private lateinit var settings: Settings

    override fun onLoad() {
        serverVersion = fromBukkitVersion()
        if (serverVersion == null) {
            logger.warning("Could not read server version, proceed with caution!")
        } else {
            logger.info("Server version $serverVersion detected")
        }
        loadSettings()
        provider = findProvider()
        logger.info("Provider " + provider.javaClass.simpleName + " loaded!")
        logger.info("Plugin loaded!")
    }

    override fun onEnable() {
        val f3nPermCommand = F3NFixCommand(this)
        getCommand("f3f4fix")!!.apply { setExecutor(f3nPermCommand); tabCompleter = f3nPermCommand }

        server.pluginManager.registerEvents(F3NFixListener(this), this)
        try {
            provider.register()
        } catch (e: ProviderException) {
            logger.log(Level.SEVERE, "Could not register provider " + provider.javaClass.simpleName + "!", e)
        }
        hooks.register(this)
        logger.info("Plugin enabled!")
    }

    override fun onDisable() {
        hooks.unregister(this)
        try {
            provider.unregister(this)
        } catch (e: ProviderException) {
            logger.log(Level.SEVERE, "Could not unregister provider " + provider.javaClass.simpleName + "!", e)
        }
        logger.info("Plugin disabled!")
    }

    fun reloadPlugin() {
        hooks.unregister(this)
        loadSettings()
        hooks.register(this)
        Bukkit.getOnlinePlayers().forEach(provider::update)
    }

    private fun loadSettings() {
        settings = try {
            Settings.loadSettings(this)
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Error loading configuration!", e)
            throw RuntimeException()
        }
    }

    private fun findProvider(): Provider {
        if (server.pluginManager.getPlugin("ProtocolLib") != null && settings.useProtocolLib) {
            return ProtocolLibProvider(this)
        }
        val serverVersion = this.serverVersion
            ?: throw ProviderException("Server version cannot be detected and ProtocolLib is disabled/missing!")
        return when {
            serverVersion.isLowerThan(ServerVersion.v_1_19) -> ReflectionProvider_1_18_R2(this)
            serverVersion.isLowerThan(ServerVersion.v_1_19_4) -> ReflectionProvider_1_19_R1(this)
            serverVersion.isLowerThan(ServerVersion.v_1_20_2) -> ReflectionProvider_1_19_R3(this)
            else -> ReflectionProvider_1_20_R2(this)
        }
    }

    fun getF3NFixPermissionLevel(player: Player): OpPermissionLevel {
        return if (!settings.enablePermissionCheck || player.hasPermission("f3f4fix.use") || player.isOp) {
            settings.opPermissionLevel
        } else
            OpPermissionLevel.NO_PERMISSIONS
    }
}