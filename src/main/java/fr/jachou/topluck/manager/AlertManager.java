package fr.jachou.topluck.manager;

import fr.jachou.topluck.Topluck;
import fr.jachou.topluck.data.Alert;
import fr.jachou.topluck.data.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages alerts for suspicious mining behavior.
 */
public class AlertManager {
    private final Topluck plugin;
    private final List<Alert> alerts;
    private final File alertLogFile;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AlertManager(Topluck plugin) {
        this.plugin = plugin;
        this.alerts = new ArrayList<>();
        
        // Setup alert log file
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File alertDir = new File(dataFolder, "alerts");
        if (!alertDir.exists()) {
            alertDir.mkdirs();
        }
        this.alertLogFile = new File(alertDir, "alerts.log");
    }

    public void checkAndCreateAlerts(Player player, PlayerStats stats, Location location) {
        if (!plugin.getConfig().getBoolean("alerts.enabled", true)) {
            return;
        }

        int minBlocks = plugin.getConfig().getInt("minimum-blocks", 100);
        if (stats.getTotalBlocksMined() < minBlocks) {
            return;
        }

        double diamondThreshold = plugin.getConfig().getDouble("thresholds.diamond", 5.0);
        double emeraldThreshold = plugin.getConfig().getDouble("thresholds.emerald", 3.0);
        double debrisThreshold = plugin.getConfig().getDouble("thresholds.ancient_debris", 2.0);
        double rareCombinedThreshold = plugin.getConfig().getDouble("thresholds.rare_combined", 8.0);

        double diamondPct = stats.getPercentage(stats.getDiamondOresMined());
        double emeraldPct = stats.getPercentage(stats.getEmeraldOresMined());
        double debrisPct = stats.getPercentage(stats.getAncientDebrisMined());
        double rarePct = stats.getPercentage(stats.getRareOresMined());

        if (diamondPct > diamondThreshold) {
            createAlert(player.getUniqueId(), player.getName(), Alert.AlertType.DIAMOND_THRESHOLD,
                    "Diamond mining percentage (" + String.format("%.2f", diamondPct) + "%) exceeds threshold", 
                    diamondPct, location);
        }

        if (emeraldPct > emeraldThreshold) {
            createAlert(player.getUniqueId(), player.getName(), Alert.AlertType.EMERALD_THRESHOLD,
                    "Emerald mining percentage (" + String.format("%.2f", emeraldPct) + "%) exceeds threshold", 
                    emeraldPct, location);
        }

        if (debrisPct > debrisThreshold) {
            createAlert(player.getUniqueId(), player.getName(), Alert.AlertType.ANCIENT_DEBRIS_THRESHOLD,
                    "Ancient Debris mining percentage (" + String.format("%.2f", debrisPct) + "%) exceeds threshold", 
                    debrisPct, location);
        }

        if (rarePct > rareCombinedThreshold) {
            createAlert(player.getUniqueId(), player.getName(), Alert.AlertType.RARE_COMBINED_THRESHOLD,
                    "Combined rare ore percentage (" + String.format("%.2f", rarePct) + "%) exceeds threshold", 
                    rarePct, location);
        }
    }

    public void createAlert(UUID playerUuid, String playerName, Alert.AlertType type, String reason, double percentage, Location location) {
        Alert alert = new Alert(playerUuid, playerName, type, reason, percentage, location);
        alerts.add(alert);
        
        // Log to file
        if (plugin.getConfig().getBoolean("alerts.log-to-file", true)) {
            logAlertToFile(alert);
        }

        // Notify admins
        if (plugin.getConfig().getBoolean("alerts.notify-admins", true)) {
            notifyAdmins(alert);
        }
    }

    private void logAlertToFile(Alert alert) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(alertLogFile, true))) {
            String logEntry = String.format("[%s] ALERT - Player: %s, Type: %s, Reason: %s, Percentage: %.2f%%, Location: %s%n",
                    alert.getTimestamp().format(DATE_FORMAT),
                    alert.getPlayerName(),
                    alert.getType(),
                    alert.getReason(),
                    alert.getPercentage(),
                    formatLocation(alert.getLocation()));
            writer.write(logEntry);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write alert to log file: " + e.getMessage());
        }
    }

    private void notifyAdmins(Alert alert) {
        String message = ChatColor.RED + "[ALERT] " + ChatColor.YELLOW + alert.getPlayerName() + 
                         ChatColor.WHITE + " - " + alert.getReason();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("topluck.alerts")) {
                player.sendMessage(message);
            }
        }
    }

    private String formatLocation(Location loc) {
        if (loc == null) return "Unknown";
        return String.format("%s (%.1f, %.1f, %.1f)", 
                loc.getWorld() != null ? loc.getWorld().getName() : "Unknown",
                loc.getX(), loc.getY(), loc.getZ());
    }

    public List<Alert> getAlerts() {
        return new ArrayList<>(alerts);
    }

    public List<Alert> getAlertsForPlayer(UUID playerUuid) {
        return alerts.stream()
                .filter(a -> a.getPlayerUuid().equals(playerUuid))
                .collect(Collectors.toList());
    }

    public List<Alert> getPendingAlerts() {
        return alerts.stream()
                .filter(a -> a.getStatus() == Alert.AlertStatus.PENDING)
                .collect(Collectors.toList());
    }

    public void handleAlert(UUID alertId, String adminName) {
        alerts.stream()
                .filter(a -> a.getAlertId().equals(alertId))
                .findFirst()
                .ifPresent(alert -> alert.markAsHandled(adminName));
    }

    public void dismissAlert(UUID alertId) {
        alerts.stream()
                .filter(a -> a.getAlertId().equals(alertId))
                .findFirst()
                .ifPresent(Alert::dismiss);
    }
}
