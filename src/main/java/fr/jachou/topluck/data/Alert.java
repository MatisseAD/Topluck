package fr.jachou.topluck.data;

import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an alert for suspicious mining behavior.
 */
public class Alert {
    private final UUID alertId;
    private final UUID playerUuid;
    private final String playerName;
    private final AlertType type;
    private final String reason;
    private final double percentage;
    private final LocalDateTime timestamp;
    private final Location location;
    private String handledBy;
    private LocalDateTime handledAt;
    private AlertStatus status;

    public Alert(UUID playerUuid, String playerName, AlertType type, String reason, double percentage, Location location) {
        this.alertId = UUID.randomUUID();
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.type = type;
        this.reason = reason;
        this.percentage = percentage;
        this.timestamp = LocalDateTime.now();
        this.location = location;
        this.status = AlertStatus.PENDING;
    }

    public UUID getAlertId() {
        return alertId;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public AlertType getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public double getPercentage() {
        return percentage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Location getLocation() {
        return location;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public LocalDateTime getHandledAt() {
        return handledAt;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void markAsHandled(String adminName) {
        this.handledBy = adminName;
        this.handledAt = LocalDateTime.now();
        this.status = AlertStatus.HANDLED;
    }

    public void dismiss() {
        this.status = AlertStatus.DISMISSED;
    }

    public enum AlertType {
        DIAMOND_THRESHOLD,
        EMERALD_THRESHOLD,
        ANCIENT_DEBRIS_THRESHOLD,
        RARE_COMBINED_THRESHOLD,
        SUSPICIOUS_LOCATION,
        SPIKE_DETECTION
    }

    public enum AlertStatus {
        PENDING,
        HANDLED,
        DISMISSED
    }
}
