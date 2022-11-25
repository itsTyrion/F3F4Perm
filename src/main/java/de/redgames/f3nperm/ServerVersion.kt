package de.redgames.f3nperm

import org.bukkit.Bukkit
import java.util.regex.Pattern

class ServerVersion(private val major: Int, private val minor: Int, private val patch: Int) {

    fun isLowerThan(version: ServerVersion) = when {
        major < version.major -> true
        major > version.major -> false
        minor < version.minor -> true
        minor > version.minor -> false
        else -> patch < version.patch
    }

    override fun toString() = "v" + major + "_" + minor + "_R" + patch

    companion object {
        @JvmField
        val v_1_19 = ServerVersion(1, 19, 1)

        private val PACKAGE_PATTERN = Pattern.compile("v(\\d+)_(\\d+)_R(\\d+)")
        @JvmStatic
        fun fromBukkitVersion(): ServerVersion? {
            val packageName = try {
                Bukkit.getServer().javaClass.getPackage().name.split('.').dropLastWhile { it.isEmpty() }[3]
            } catch (e: Exception) {
                return null
            }
            val matcher = PACKAGE_PATTERN.matcher(packageName)
            if (!matcher.find()) {
                return null
            }
            val major = matcher.group(1).toInt()
            val minor = matcher.group(2).toInt()
            val patch = matcher.group(3).toInt()
            return ServerVersion(major, minor, patch)
        }
    }
}