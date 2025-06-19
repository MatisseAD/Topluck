package fr.jachou.topluck.command;

import fr.jachou.topluck.Topluck;
import fr.jachou.topluck.data.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Displays simple statistics about rare ore mining.
 */
public class TopLuckCommand implements CommandExecutor {
    private final Topluck plugin;

    public TopLuckCommand(Topluck plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showTop(sender);
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
        sender.sendMessage(ChatColor.YELLOW + "Rare ores mined: " + stats.getRareOresMined());
    }
}
