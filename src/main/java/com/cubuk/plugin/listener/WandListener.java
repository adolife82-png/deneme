package com.cubuk.plugin.listener;

import com.cubuk.plugin.CubukPlugin;
import com.cubuk.plugin.menu.DeleteConfirmMenu;
import com.cubuk.plugin.menu.WandMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class WandListener implements Listener {

    private final CubukPlugin plugin;

    public WandListener(CubukPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player p = e.getPlayer();
        ItemStack item = p.getItemInHand();
        if (!plugin.getWandManager().isWand(item)) return;

        if (plugin.getCooldownManager().isOnCooldown(p)) {
            long rem = plugin.getCooldownManager().getRemaining(p);
            p.sendMessage("§8§l» §cCooldown! Kalan: §e" + (int) Math.ceil(rem / 1000.0) + "s");
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 0.8f, 1f);
            return;
        }

        Fireball fb = p.launchProjectile(Fireball.class);
        fb.setIsIncendiary(plugin.getConfig().getBoolean("fireball-incendiary", true));
        fb.setYield((float) plugin.getConfig().getDouble("fireball-yield", 1.5));
        p.playSound(p.getLocation(), Sound.GHAST_FIREBALL, 1f, 1f);
        p.sendMessage("§8§l» §cAteş Topu fırlatıldı! §8§l«");
        plugin.getCooldownManager().start(p);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().toLowerCase();
        Player p = e.getPlayer();
        if (cmd.equals("/c") || cmd.equals("/cubuk")) {
            e.setCancelled(true);
            WandMenu.open(p, plugin);
        } else if (cmd.equals("/sil") || cmd.equals("/copeat")) {
            e.setCancelled(true);
            if (!plugin.getWandManager().hasWand(p)) {
                p.sendMessage("§8§l» §cEnvanterinde çubuk yok!");
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 0.8f, 1f);
                return;
            }
            DeleteConfirmMenu.open(p, plugin);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // Cooldown XP barını sıfırla (ticker zaten temizliyor)
    }
}
