package fr.jachou.topluck;

import fr.jachou.topluck.command.TopLuckCommand;
import fr.jachou.topluck.data.PlayerStats;
import fr.jachou.topluck.listener.OreMineListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Basic implementation of the TopLuck plugin tracking rare ore mining.
 */
public final class Topluck extends JavaPlugin {

    private final Map<UUID, PlayerStats> statsMap = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new OreMineListener(this), this);
        PluginCommand cmd = getCommand("topluck");
        if (cmd != null) {
            cmd.setExecutor(new TopLuckCommand(this));
        } else {
            getLogger().warning("Command topluck not defined in plugin.yml");
        }
    }

    @Override
    public void onDisable() {
        statsMap.clear();
    }

    public PlayerStats getStats(UUID uuid) {
        return statsMap.computeIfAbsent(uuid, PlayerStats::new);
    }

    public Collection<PlayerStats> getAllStats() {
        return statsMap.values();
    }
}
