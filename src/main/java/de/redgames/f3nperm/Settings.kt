package de.redgames.f3nperm

import org.bukkit.configuration.file.FileConfiguration

class Settings private constructor(config: FileConfiguration) {
    val useProtocolLib = config.getBoolean("useProtocolLib", true)
    val opPermissionLevel: OpPermissionLevel
    val enablePermissionCheck = config.getBoolean("enablePermissionCheck", false)

    init {
        val opPermLvlInt = config.getInt("opPermissionLevel", OpPermissionLevel.PLAYER_COMMANDS.level)
        opPermissionLevel =
            OpPermissionLevel.fromLevel(opPermLvlInt) ?: error("OpPermissionLevel $opPermLvlInt is not recognized!")
    }

    companion object {
        @JvmStatic
        fun loadSettings(plugin: F3NFixPlugin): Settings {
            plugin.saveDefaultConfig() // Doesn't overwrite if it's already present
            plugin.reloadConfig()
            return Settings(plugin.config)
        }
    }
}