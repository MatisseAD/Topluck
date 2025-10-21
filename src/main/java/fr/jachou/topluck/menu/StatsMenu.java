package fr.jachou.topluck.menu;

import fr.jachou.topluck.Topluck;
import fr.jachou.topluck.data.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simple GUI to display mining statistics for online players.
 */
public class StatsMenu {

    private final Topluck plugin;

    public StatsMenu(Topluck plugin) {
        this.plugin = plugin;
    }

    public void open(Player viewer) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        int size = Math.max(9, ((players.size() - 1) / 9 + 1) * 9);
        Inventory inv = Bukkit.createInventory(null, size, ChatColor.DARK_GREEN + "TopLuck Stats");
        int index = 0;
        for (Player p : players) {
            PlayerStats stats = plugin.getStats(p.getUniqueId());
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(p);
                meta.setDisplayName(ChatColor.YELLOW + p.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Total mined: " + stats.getTotalBlocksMined());
                lore.add(ChatColor.AQUA + "Diamonds: " + stats.getDiamondOresMined() + formatPercent(stats.getDiamondOresMined(), stats));
                lore.add(ChatColor.GREEN + "Emeralds: " + stats.getEmeraldOresMined() + formatPercent(stats.getEmeraldOresMined(), stats));
                lore.add(ChatColor.GOLD + "Gold: " + stats.getGoldOresMined() + formatPercent(stats.getGoldOresMined(), stats));
                lore.add(ChatColor.WHITE + "Iron: " + stats.getIronOresMined() + formatPercent(stats.getIronOresMined(), stats));
                lore.add(ChatColor.DARK_PURPLE + "Ancient Debris: " + stats.getAncientDebrisMined() + formatPercent(stats.getAncientDebrisMined(), stats));
                lore.add("");
                double suspicion = stats.getSuspicionScore();
                ChatColor suspicionColor = suspicion > 50 ? ChatColor.RED : (suspicion > 20 ? ChatColor.YELLOW : ChatColor.GREEN);
                lore.add(suspicionColor + "Suspicion: " + String.format("%.1f/100", suspicion));
                
                if (stats.getSurveillanceStatus() != PlayerStats.SurveillanceStatus.NONE) {
                    lore.add(ChatColor.RED + "Status: " + stats.getSurveillanceStatus());
                }
                
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
            inv.setItem(index++, head);
        }
        viewer.openInventory(inv);
    }

    private String formatPercent(int count, PlayerStats stats) {
        double pct = stats.getPercentage(count);
        ChatColor color = ChatColor.WHITE;
        
        // Color code based on suspicion
        if (pct > 5.0) {
            color = ChatColor.RED;
        } else if (pct > 2.0) {
            color = ChatColor.YELLOW;
        }
        
        return color + String.format(" (%.2f%%)", pct);
    }
}
