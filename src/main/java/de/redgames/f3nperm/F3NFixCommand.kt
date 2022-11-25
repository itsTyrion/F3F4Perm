package de.redgames.f3nperm

import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class F3NFixCommand(private val plugin: F3NFixPlugin) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) return false

        return when (args[0].lowercase()) {
            "reload" -> {
                plugin.reloadPlugin()
                sender.sendMessage("$GREEN Plugin successfully reloaded!")
                true
            }
            "forceupdate" -> {
                val provider = plugin.provider
                if (args.size < 2) {
                    plugin.server.onlinePlayers.forEach(provider::update)
                    sender.sendMessage("$GREEN Updated all online players")
                    return true
                }
                val target = Bukkit.getPlayer(args[1])
                if (target == null) {
                    sender.sendMessage("$RED A player named $YELLOW${args[1]} was not found!")
                    return true
                }
                provider.update(target)
                sender.sendMessage("$GREEN Updated player ${args[1]}")
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