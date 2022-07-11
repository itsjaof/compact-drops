package dev.twoz.compactdrops;

import dev.twoz.compactdrops.commands.ComandoCompactar;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("compactardrops").setExecutor(new ComandoCompactar());
    }

    @Override
    public void onDisable() {

    }
}
