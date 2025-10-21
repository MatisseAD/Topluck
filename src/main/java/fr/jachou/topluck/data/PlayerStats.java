package fr.jachou.topluck.data;

import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private int ancientDebrisMined;

    private SurveillanceStatus surveillanceStatus;
    private String surveillanceReason;
    private LocalDateTime surveillanceStartDate;
    
    private final List<MiningEvent> miningHistory;

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
        this.surveillanceStatus = SurveillanceStatus.NONE;
        this.miningHistory = new ArrayList<>();
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

    public int getAncientDebrisMined() {
        return ancientDebrisMined;
    }

    public void incrementBlock(org.bukkit.Material type, Location location) {
        totalBlocksMined++;
        switch (type) {
            case DIAMOND_ORE,
                    DEEPSLATE_DIAMOND_ORE -> {
                diamondOresMined++;
                rareOresMined++;
                miningHistory.add(new MiningEvent(type, location, LocalDateTime.now()));
            }
            case EMERALD_ORE -> {
                emeraldOresMined++;
                rareOresMined++;
                miningHistory.add(new MiningEvent(type, location, LocalDateTime.now()));
            }
            case GOLD_ORE, DEEPSLATE_GOLD_ORE, NETHER_GOLD_ORE -> goldOresMined++;
            case IRON_ORE, DEEPSLATE_IRON_ORE -> ironOresMined++;
            case ANCIENT_DEBRIS -> {
                ancientDebrisMined++;
                rareOresMined++;
                miningHistory.add(new MiningEvent(type, location, LocalDateTime.now()));
            }
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

    public SurveillanceStatus getSurveillanceStatus() {
        return surveillanceStatus;
    }

    public void setSurveillanceStatus(SurveillanceStatus status, String reason) {
        this.surveillanceStatus = status;
        this.surveillanceReason = reason;
        if (status != SurveillanceStatus.NONE) {
            this.surveillanceStartDate = LocalDateTime.now();
        } else {
            this.surveillanceStartDate = null;
        }
    }

    public String getSurveillanceReason() {
        return surveillanceReason;
    }

    public LocalDateTime getSurveillanceStartDate() {
        return surveillanceStartDate;
    }

    public List<MiningEvent> getMiningHistory() {
        return new ArrayList<>(miningHistory);
    }

    public double getSuspicionScore() {
        if (totalBlocksMined < 100) return 0.0;
        
        double score = 0.0;
        double diamondPct = getPercentage(diamondOresMined);
        double emeraldPct = getPercentage(emeraldOresMined);
        double debrisPct = getPercentage(ancientDebrisMined);
        
        // Normal ranges: diamond ~0.3%, emerald ~0.1%, debris ~0.2%
        if (diamondPct > 1.0) score += (diamondPct - 1.0) * 10;
        if (emeraldPct > 0.5) score += (emeraldPct - 0.5) * 15;
        if (debrisPct > 0.5) score += (debrisPct - 0.5) * 12;
        
        return Math.min(100.0, score);
    }

    public enum SurveillanceStatus {
        NONE,
        UNDER_SURVEILLANCE,
        CLEARED
    }

    public static class MiningEvent {
        private final org.bukkit.Material material;
        private final Location location;
        private final LocalDateTime timestamp;

        public MiningEvent(org.bukkit.Material material, Location location, LocalDateTime timestamp) {
            this.material = material;
            this.location = location;
            this.timestamp = timestamp;
        }

        public org.bukkit.Material getMaterial() {
            return material;
        }

        public Location getLocation() {
            return location;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}

