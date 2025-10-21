package fr.jachou.topluck.command;

import fr.jachou.topluck.Topluck;
import fr.jachou.topluck.data.PlayerStats;
import fr.jachou.topluck.menu.StatsMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class TopLuckCommand implements CommandExecutor {
    private final Topluck plugin;

    public TopLuckCommand(Topluck plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                new StatsMenu(plugin).open(player);
            } else {
                showTop(sender);
            }
        } else {
            showPlayer(sender, args[0]);
        }
        return true;
    }

    private void showTop(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "-- Top Luck --");
        plugin.getAllStats().stream()
                .sorted(Comparator.comparingInt(PlayerStats::getRareOresMined).reversed())
                .limit(10)
                .forEach(stats -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(stats.getUuid());
                    sender.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.WHITE + ": " + stats.getRareOresMined());
                });
    }

    private void showPlayer(CommandSender sender, String name) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(name);
        PlayerStats stats = plugin.getStats(target.getUniqueId());
        sender.sendMessage(ChatColor.AQUA + "Stats for " + target.getName());

        sender.sendMessage(ChatColor.YELLOW + "Total blocks mined: " + stats.getTotalBlocksMined());
        sender.sendMessage(ChatColor.AQUA + "Diamonds: " + stats.getDiamondOresMined() + format(stats.getDiamondOresMined(), stats));
        sender.sendMessage(ChatColor.GREEN + "Emeralds: " + stats.getEmeraldOresMined() + format(stats.getEmeraldOresMined(), stats));
        sender.sendMessage(ChatColor.GOLD + "Gold: " + stats.getGoldOresMined() + format(stats.getGoldOresMined(), stats));
        sender.sendMessage(ChatColor.WHITE + "Iron: " + stats.getIronOresMined() + format(stats.getIronOresMined(), stats));
        sender.sendMessage(ChatColor.DARK_PURPLE + "Ancient Debris: " + stats.getAncientDebrisMined() + format(stats.getAncientDebrisMined(), stats));
        sender.sendMessage(ChatColor.YELLOW + "Rare ores mined: " + stats.getRareOresMined());
        sender.sendMessage("");
        
        double suspicion = stats.getSuspicionScore();
        ChatColor suspicionColor = suspicion > 50 ? ChatColor.RED : (suspicion > 20 ? ChatColor.YELLOW : ChatColor.GREEN);
        sender.sendMessage(suspicionColor + "Suspicion Score: " + String.format("%.1f/100", suspicion));
        
        if (stats.getSurveillanceStatus() != PlayerStats.SurveillanceStatus.NONE) {
            sender.sendMessage(ChatColor.RED + "Surveillance: " + stats.getSurveillanceStatus());
        }
    }

    private String format(int count, PlayerStats stats) {
        return String.format(" (%.2f%%)", stats.getPercentage(count));
    }
}
