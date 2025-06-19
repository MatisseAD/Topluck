package fr.jachou.topluck.data;

import java.util.UUID;

/**
 * Simple in-memory statistics for a player.
 */
public class PlayerStats {
    private final UUID uuid;
    private int rareOresMined;

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getRareOresMined() {
        return rareOresMined;
    }

    public void incrementRareOre() {
        rareOresMined++;
    }
}
