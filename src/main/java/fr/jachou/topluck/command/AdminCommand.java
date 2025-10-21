package fr.jachou.topluck.command;

import fr.jachou.topluck.Topluck;
import fr.jachou.topluck.data.Alert;
import fr.jachou.topluck.data.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Admin command executor for anti-cheat features.
 */
public class AdminCommand implements CommandExecutor {
    private final Topluck plugin;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AdminCommand(Topluck plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("topluck.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "audit" -> handleAudit(sender, args);
            case "surveillance" -> handleSurveillance(sender, args);
            case "alerts" -> handleAlerts(sender, args);
            case "freeze" -> handleFreeze(sender, args);
            case "export" -> handleExport(sender, args);
            default -> showHelp(sender);
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Topluck Admin Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/tlad audit <player>" + ChatColor.WHITE + " - Show detailed audit report");
        sender.sendMessage(ChatColor.YELLOW + "/tlad surveillance <player> <watch|clear> [reason]" + ChatColor.WHITE + " - Manage surveillance status");
        sender.sendMessage(ChatColor.YELLOW + "/tlad alerts [player]" + ChatColor.WHITE + " - View alerts");
        sender.sendMessage(ChatColor.YELLOW + "/tlad freeze <player>" + ChatColor.WHITE + " - Freeze/unfreeze a player");
        sender.sendMessage(ChatColor.YELLOW + "/tlad export <player> <csv|json>" + ChatColor.WHITE + " - Export player stats");
    }

    private void handleAudit(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /tlad audit <player>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        PlayerStats stats = plugin.getStats(target.getUniqueId());

        sender.sendMessage(ChatColor.AQUA + "=== Audit Report for " + target.getName() + " ===");
        sender.sendMessage(ChatColor.YELLOW + "Total blocks mined: " + ChatColor.WHITE + stats.getTotalBlocksMined());
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "Ore Statistics:");
        sender.sendMessage(ChatColor.AQUA + "  Diamonds: " + stats.getDiamondOresMined() + 
                          String.format(" (%.2f%%)", stats.getPercentage(stats.getDiamondOresMined())));
        sender.sendMessage(ChatColor.GREEN + "  Emeralds: " + stats.getEmeraldOresMined() + 
                          String.format(" (%.2f%%)", stats.getPercentage(stats.getEmeraldOresMined())));
        sender.sendMessage(ChatColor.GOLD + "  Gold: " + stats.getGoldOresMined() + 
                          String.format(" (%.2f%%)", stats.getPercentage(stats.getGoldOresMined())));
        sender.sendMessage(ChatColor.WHITE + "  Iron: " + stats.getIronOresMined() + 
                          String.format(" (%.2f%%)", stats.getPercentage(stats.getIronOresMined())));
        sender.sendMessage(ChatColor.DARK_PURPLE + "  Ancient Debris: " + stats.getAncientDebrisMined() + 
                          String.format(" (%.2f%%)", stats.getPercentage(stats.getAncientDebrisMined())));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.RED + "Suspicion Score: " + String.format("%.1f/100", stats.getSuspicionScore()));
        sender.sendMessage(ChatColor.YELLOW + "Surveillance Status: " + ChatColor.WHITE + stats.getSurveillanceStatus());
        
        if (stats.getSurveillanceStatus() != PlayerStats.SurveillanceStatus.NONE) {
            sender.sendMessage(ChatColor.GRAY + "  Reason: " + stats.getSurveillanceReason());
            if (stats.getSurveillanceStartDate() != null) {
                sender.sendMessage(ChatColor.GRAY + "  Since: " + stats.getSurveillanceStartDate().format(DATE_FORMAT));
            }
        }

        List<Alert> alerts = plugin.getAlertManager().getAlertsForPlayer(target.getUniqueId());
        sender.sendMessage("");
        sender.sendMessage(ChatColor.RED + "Total Alerts: " + alerts.size());
        
        // Compare with server average
        double avgDiamond = plugin.getAllStats().stream()
                .mapToDouble(s -> s.getPercentage(s.getDiamondOresMined()))
                .average().orElse(0.0);
        double avgEmerald = plugin.getAllStats().stream()
                .mapToDouble(s -> s.getPercentage(s.getEmeraldOresMined()))
                .average().orElse(0.0);
        
        sender.sendMessage("");
        sender.sendMessage(ChatColor.AQUA + "Server Average Comparison:");
        sender.sendMessage(ChatColor.WHITE + "  Diamond: " + String.format("%.2f%% (avg: %.2f%%)", 
                stats.getPercentage(stats.getDiamondOresMined()), avgDiamond));
        sender.sendMessage(ChatColor.WHITE + "  Emerald: " + String.format("%.2f%% (avg: %.2f%%)", 
                stats.getPercentage(stats.getEmeraldOresMined()), avgEmerald));
    }

    private void handleSurveillance(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /tlad surveillance <player> <watch|clear> [reason]");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        PlayerStats stats = plugin.getStats(target.getUniqueId());
        String action = args[2].toLowerCase();

        if (action.equals("watch")) {
            String reason = args.length > 3 ? String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length)) 
                                            : "Manual surveillance by admin";
            stats.setSurveillanceStatus(PlayerStats.SurveillanceStatus.UNDER_SURVEILLANCE, reason);
            sender.sendMessage(ChatColor.GREEN + target.getName() + " is now under surveillance.");
        } else if (action.equals("clear")) {
            stats.setSurveillanceStatus(PlayerStats.SurveillanceStatus.CLEARED, "Cleared by admin");
            sender.sendMessage(ChatColor.GREEN + target.getName() + " has been cleared.");
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid action. Use 'watch' or 'clear'.");
        }
    }

    private void handleAlerts(CommandSender sender, String[] args) {
        List<Alert> alertsToShow;
        
        if (args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            alertsToShow = plugin.getAlertManager().getAlertsForPlayer(target.getUniqueId());
            sender.sendMessage(ChatColor.AQUA + "=== Alerts for " + target.getName() + " ===");
        } else {
            alertsToShow = plugin.getAlertManager().getPendingAlerts();
            sender.sendMessage(ChatColor.AQUA + "=== Pending Alerts ===");
        }

        if (alertsToShow.isEmpty()) {
            sender.sendMessage(ChatColor.GREEN + "No alerts found.");
            return;
        }

        int count = 0;
        for (Alert alert : alertsToShow) {
            if (count++ >= 10) {
                sender.sendMessage(ChatColor.GRAY + "... and " + (alertsToShow.size() - 10) + " more");
                break;
            }
            String status = alert.getStatus() == Alert.AlertStatus.PENDING ? 
                    ChatColor.RED + "[PENDING]" : ChatColor.GREEN + "[" + alert.getStatus() + "]";
            sender.sendMessage(status + ChatColor.YELLOW + " " + alert.getPlayerName() + 
                              ChatColor.WHITE + " - " + alert.getReason());
            sender.sendMessage(ChatColor.GRAY + "  Time: " + alert.getTimestamp().format(DATE_FORMAT));
        }
    }

    private void handleFreeze(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /tlad freeze <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or not online.");
            return;
        }

        // Toggle freeze status using walking speed
        if (target.getWalkSpeed() == 0.0f) {
            target.setWalkSpeed(0.2f); // Normal speed
            target.setFlySpeed(0.1f);
            sender.sendMessage(ChatColor.GREEN + target.getName() + " has been unfrozen.");
            target.sendMessage(ChatColor.GREEN + "You have been unfrozen.");
        } else {
            target.setWalkSpeed(0.0f); // Frozen
            target.setFlySpeed(0.0f);
            sender.sendMessage(ChatColor.GREEN + target.getName() + " has been frozen.");
            target.sendMessage(ChatColor.RED + "You have been frozen by an administrator for investigation.");
        }
    }

    private void handleExport(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /tlad export <player> <csv|json>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        String format = args[2].toLowerCase();
        
        if (!format.equals("csv") && !format.equals("json")) {
            sender.sendMessage(ChatColor.RED + "Invalid format. Use 'csv' or 'json'.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Export functionality is configured in config.yml");
        sender.sendMessage(ChatColor.GRAY + "Player: " + target.getName() + ", Format: " + format);
        sender.sendMessage(ChatColor.GRAY + "Stats would be exported to: plugins/Topluck/exports/");
    }
}
