package de.redgames.f3nperm

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class F3NFixCommand(private val plugin: F3NFixPlugin) : TabExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) return false

        return when (args[0].lowercase()) {
            "reload" -> {
                plugin.reloadPlugin()
                sender.sendRichMessage("<green>Plugin successfully reloaded!")
                true
            }
            "forceupdate" -> {
                val provider = plugin.provider
                if (args.size < 2) {
                    plugin.server.onlinePlayers.forEach(provider::update)
                    sender.sendRichMessage("<green>Updated all online players")
                    return true
                }
                val target = Bukkit.getPlayer(args[1])
                if (target == null) {
                    sender.sendRichMessage("<red>A player named <c:yellow>${args[1]}</c> was not found!")
                    return true
                }
                provider.update(target)
                sender.sendRichMessage("<green>Updated player <yellow>${args[1]}")
                true
            }
            else -> false
        }
    }

    override fun onTabComplete(cs: CommandSender, command: Command, alias: String, args: Array<String>): List<String> =
        if (args.size == 1) {
            arrayOf("reload", "forceupdate").filter { it == args[0].lowercase() }

        } else if (args.size == 2 && args[0].equals("forceupdate", ignoreCase = true)) {
            Bukkit.matchPlayer(args[1]).map { it.name }
        } else
            emptyList()
}