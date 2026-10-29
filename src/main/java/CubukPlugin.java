package com.example.cubuk;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CubukPlugin extends JavaPlugin implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_MS = 3000L; // 3 saniye
    private final String MENU_TITLE = "§8Çubuk Menüsü";

    @Override
    public void onEnable() {
        // Komutları kaydet
        getCommand("cubuk").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) {
                openWandMenu((Player) sender);
            }
            return true;
        });

        getCommand("copeat").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) {
                removeWands((Player) sender);
            }
            return true;
        });

        // Event listener'ı kaydet
        Bukkit.getPluginManager().registerEvents(this, this);

        // Hotbar'daki cooldown sayacını güncelleyen görev
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : cooldowns.keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null || !player.isOnline()) continue;

                    long remaining = cooldowns.get(uuid) - System.currentTimeMillis();
                    if (remaining <= 0) {
                        cooldowns.remove(uuid);
                        player.setLevel(0);
                        player.setExp(0f);
                    } else {
                        int seconds = (int) Math.ceil(remaining / 1000.0);
                        player.setLevel(seconds);
                        player.setExp(remaining / (float) COOLDOWN_MS);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 5L); // Her 5 tick'te bir (çeyrek saniye) güncelle
    }

    // /cubuk komutu: menüyü açar
    private void openWandMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, MENU_TITLE);
        inv.setItem(4, createWand());
        player.openInventory(inv);
    }

    // Özel çubuğu oluşturur
    private ItemStack createWand() {
        ItemStack wand = new ItemStack(Material.STICK);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName("§eAteş Çubuğu");
        meta.setLore(Arrays.asList("§7Sağ tıkla: §cAteş Topu"));
        wand.setItemMeta(meta);
        return wand;
    }

    // Bir eşyanın özel çubuk olup olmadığını kontrol eder
    private boolean isWand(ItemStack item) {
        if (item == null || item.getType() != Material.STICK) return false;
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals("§eAteş Çubuğu");
    }

    // Menüye tıklanınca çubuğu verir
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(MENU_TITLE)) return;
        event.setCancelled(true); // Menüden eşya alınmasını engelle

        if (event.getCurrentItem() != null && isWand(event.getCurrentItem())) {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            player.getInventory().addItem(createWand());
            player.sendMessage("§aAteş Çubuğu envanterine eklendi!");
        }
    }

    // Çubukla sağ tıklanınca ateş topu fırlatır
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (!isWand(item)) return;

        // Cooldown kontrolü
        if (cooldowns.containsKey(player.getUniqueId())) {
            long remaining = cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
            if (remaining > 0) {
                player.sendMessage("§cAteş Çubuğu cooldown'da! Kalan: " + (int) Math.ceil(remaining / 1000.0) + "s");
                return;
            }
        }

        // Ateş topunu fırlat
        player.launchProjectile(Fireball.class);
        player.sendMessage("§cAteş Topu fırlatıldı!");

        // Cooldown'ı başlat
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + COOLDOWN_MS);

        // Hotbar'ı hemen güncelle
        int seconds = (int) Math.ceil(COOLDOWN_MS / 1000.0);
        player.setLevel(seconds);
        player.setExp(1f);
    }

    // /copeat komutu: envanterdeki özel çubukları siler
    private void removeWands(Player player) {
        int removed = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (isWand(item)) {
                player.getInventory().setItem(i, null);
                removed++;
            }
        }

        if (removed > 0) {
            player.sendMessage("§a" + removed + " adet Ateş Çubuğu envanterden silindi.");
        } else {
            player.sendMessage("§cEnvanterinde Ateş Çubuğu bulunamadı.");
        }
    }
}
