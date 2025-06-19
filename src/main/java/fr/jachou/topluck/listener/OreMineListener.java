package fr.jachou.topluck.listener;

import fr.jachou.topluck.Topluck;
import fr.jachou.topluck.data.PlayerStats;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/**
 * Listens for mining of rare ores and updates statistics.
 */
public class OreMineListener implements Listener {
    private static final Set<Material> RARE_ORES = EnumSet.of(
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.ANCIENT_DEBRIS,
            Material.EMERALD_ORE
    );

    private final Topluck plugin;

    public OreMineListener(Topluck plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (RARE_ORES.contains(block.getType())) {
            PlayerStats stats = plugin.getStats(player.getUniqueId());
            stats.incrementRareOre();
        }
    }
}
