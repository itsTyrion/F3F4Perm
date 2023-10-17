package de.redgames.f3nperm

enum class OpPermissionLevel(val level: Int) {
    NO_PERMISSIONS(0), ACCESS_SPAWN(1), WORLD_COMMANDS(2), PLAYER_COMMANDS(3), ADMIN_COMMANDS(4);

    fun toStatusByte(): Byte {
        return (level + BASE_OFFSET).toByte()
    }

    companion object {
        const val BASE_OFFSET = 24
        @JvmStatic
        fun fromLevel(level: Int): OpPermissionLevel? =
            entries.firstOrNull { it.level == level }

        @JvmStatic
        fun fromStatusByte(statusByte: Byte): OpPermissionLevel? =
            entries.firstOrNull { it.toStatusByte() == statusByte }
    }
}