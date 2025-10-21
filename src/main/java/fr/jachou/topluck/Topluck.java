package fr.jachou.topluck;

import fr.jachou.topluck.command.AdminCommand;
import fr.jachou.topluck.command.TopLuckCommand;
import fr.jachou.topluck.data.PlayerStats;
import fr.jachou.topluck.listener.OreMineListener;
import fr.jachou.topluck.manager.AlertManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Topluck extends JavaPlugin {

    private final Map<UUID, PlayerStats> statsMap = new HashMap<>();
    private AlertManager alertManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        alertManager = new AlertManager(this);
        
        // Register events
        Bukkit.getPluginManager().registerEvents(new OreMineListener(this), this);
        
        // Register commands
        PluginCommand cmd = getCommand("topluck");
        if (cmd != null) {
            cmd.setExecutor(new TopLuckCommand(this));
        } else {
            getLogger().warning("Command topluck not defined in plugin.yml");
        }

        PluginCommand adminCmd = getCommand("tlad");
        if (adminCmd != null) {
            adminCmd.setExecutor(new AdminCommand(this));
        } else {
            getLogger().warning("Command tlad not defined in plugin.yml");
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

    public AlertManager getAlertManager() {
        return alertManager;
    }
}
