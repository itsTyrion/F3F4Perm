package de.redgames.f3nperm

enum class OpPermissionLevel(val level: Int) {
    NO_PERMISSIONS(0), ACCESS_SPAWN(1), WORLD_COMMANDS(2), PLAYER_COMMANDS(3), ADMIN_COMMANDS(4);

    fun toStatusByte(version: ServerVersion?): Byte {
        val baseOffset = 24
        return (level + baseOffset).toByte()
    }

    companion object {
        private val values = values()
        @JvmStatic
        fun fromLevel(level: Int): OpPermissionLevel? =
            values.firstOrNull { it.level == level }

        @JvmStatic
        fun fromStatusByte(version: ServerVersion?, statusByte: Byte): OpPermissionLevel? =
            values.firstOrNull { it.toStatusByte(version) == statusByte }
    }
}