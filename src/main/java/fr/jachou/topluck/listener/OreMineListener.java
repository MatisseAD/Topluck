package fr.jachou.topluck.listener;

import fr.jachou.topluck.Topluck;
import fr.jachou.topluck.data.PlayerStats;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

/**
 * Listens for block breaking events and updates statistics.
 */
public class OreMineListener implements Listener {
    private final Topluck plugin;

    public OreMineListener(Topluck plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        PlayerStats stats = plugin.getStats(player.getUniqueId());
        stats.incrementBlock(block.getType());
    }
}
