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
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
            inv.setItem(index++, head);
        }
        viewer.openInventory(inv);
    }

    private String formatPercent(int count, PlayerStats stats) {
        double pct = stats.getPercentage(count);
        return String.format(" (%.2f%%)", pct);
    }
}
