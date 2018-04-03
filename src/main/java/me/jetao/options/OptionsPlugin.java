package me.jetao.options;

import me.jetao.options.commands.HologramCommand;
import me.jetao.options.manager.HologramManager;
import me.jetao.options.listeners.HologramListeners;
import org.bukkit.plugin.java.JavaPlugin;

public class OptionsPlugin extends JavaPlugin {

    private static OptionsPlugin instance;

    private static HologramManager hologramManager;

    @Override
    public void onEnable() {
        instance = this;

        hologramManager = new HologramManager();

        loadListeners();
        loadCommands();
    }

    private void loadCommands() {
        new HologramCommand();
    }

    private void loadListeners() {
        new HologramListeners();
    }

    public static OptionsPlugin getInstance() {
        return instance;
    }

    public static HologramManager getHologramManager() {
        return hologramManager;
    }
}
