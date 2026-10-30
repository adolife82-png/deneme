package com.cubuk.plugin;

import com.cubuk.plugin.listener.MenuListener;
import com.cubuk.plugin.listener.WandListener;
import com.cubuk.plugin.manager.CooldownManager;
import com.cubuk.plugin.manager.WandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CubukPlugin extends JavaPlugin {

    private static CubukPlugin instance;
    private WandManager wandManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Manager'ları başlat
        this.wandManager = new WandManager(this);
        this.cooldownManager = new CooldownManager(this);

        // Listener'ları kaydet
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new WandListener(this), this);

        getLogger().info("§a[CubukPlugin] Aktif! Surum: " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        if (cooldownManager != null) cooldownManager.clearAll();
        getLogger().info("§c[CubukPlugin] Kapatildi.");
    }

    public static CubukPlugin getInstance() { return instance; }
    public WandManager getWandManager() { return wandManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
}
