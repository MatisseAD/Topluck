package fr.jachou.topluck.data;

import java.util.UUID;

/**
 * Simple in-memory statistics for a player.
 */
public class PlayerStats {
    private final UUID uuid;

    private int totalBlocksMined;
    private int diamondOresMined;
    private int emeraldOresMined;
    private int goldOresMined;
    private int ironOresMined;

    private int rareOresMined;


    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }


    public int getTotalBlocksMined() {
        return totalBlocksMined;
    }

    public int getDiamondOresMined() {
        return diamondOresMined;
    }

    public int getEmeraldOresMined() {
        return emeraldOresMined;
    }

    public int getGoldOresMined() {
        return goldOresMined;
    }

    public int getIronOresMined() {
        return ironOresMined;
    }

    public void incrementBlock(org.bukkit.Material type) {
        totalBlocksMined++;
        switch (type) {
            case DIAMOND_ORE,
                    DEEPSLATE_DIAMOND_ORE -> diamondOresMined++;
            case EMERALD_ORE -> emeraldOresMined++;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE, NETHER_GOLD_ORE -> goldOresMined++;
            case IRON_ORE, DEEPSLATE_IRON_ORE -> ironOresMined++;
            default -> {
            }
        }
    }

    public double getPercentage(int count) {
        if (totalBlocksMined == 0) return 0.0D;
        return (double) count * 100.0D / totalBlocksMined;
    }

    public int getRareOresMined() {
        return rareOresMined;
    }

    public void incrementRareOre() {
        rareOresMined++;
    }
}
