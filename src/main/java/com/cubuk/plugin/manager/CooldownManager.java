package com.cubuk.plugin.manager;

import com.cubuk.plugin.CubukPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final CubukPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_MS;

    public CooldownManager(CubukPlugin plugin) {
        this.plugin = plugin;
        this.COOLDOWN_MS = plugin.getConfig().getInt("cooldown-seconds", 3) * 1000L;
        startTicker();
    }

    public boolean isOnCooldown(Player player) {
        Long end = cooldowns.get(player.getUniqueId());
        return end != null && end > System.currentTimeMillis();
    }

    public long getRemaining(Player player) {
        Long end = cooldowns.get(player.getUniqueId());
        if (end == null) return 0;
        return Math.max(0, end - System.currentTimeMillis());
    }

    public void start(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + COOLDOWN_MS);
        player.setLevel((int) Math.ceil(COOLDOWN_MS / 1000.0));
        player.setExp(0f);
    }

    public void clearAll() {
        for (UUID uuid : cooldowns.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) { p.setLevel(0); p.setExp(0f); }
        }
        cooldowns.clear();
    }

    private void startTicker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : new HashMap<>(cooldowns).keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null || !player.isOnline()) {
                        cooldowns.remove(uuid);
                        continue;
                    }

                    long remaining = cooldowns.get(uuid) - System.currentTimeMillis();
                    if (remaining <= 0) {
                        cooldowns.remove(uuid);
                        player.setLevel(0);
                        player.setExp(0f);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.8f, 1.5f);
                        player.sendMessage("§8§l» §a§lAteş Çubuğu Hazır! §8§l«");
                    } else {
                        float progress = 1.0f - (remaining / (float) COOLDOWN_MS);
                        player.setExp(Math.max(0f, Math.min(1f, progress)));
                        player.setLevel((int) Math.ceil(remaining / 1000.0));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
