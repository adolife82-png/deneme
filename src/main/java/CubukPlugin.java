package com.example.cubuk;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
    private final long COOLDOWN_MS = 3000L;
    private final String MENU_TITLE = "§8§l» §e§lAteş Çubuğu Menüsü §8§l«";

    @Override
    public void onEnable() {
        getCommand("cubuk").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) openWandMenu((Player) sender);
            return true;
        });

        getCommand("copeat").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) removeWands((Player) sender);
            return true;
        });

        Bukkit.getPluginManager().registerEvents(this, this);

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
                        // Hazır olduğunda ses çal
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 2f);
                    } else {
                        int seconds = (int) Math.ceil(remaining / 1000.0);
                        player.setLevel(seconds);
                        player.setExp(remaining / (float) COOLDOWN_MS);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 5L);
    }

    // ================== MODERN GUI ==================
    private void openWandMenu(Player player) {
        // 5x9 (45 slot) modern GUI
        Inventory inv = Bukkit.createInventory(null, 45, MENU_TITLE);

        // Cam panel ile çerçeve
        ItemStack border = createGlass((short) 7); // gri cam
        ItemStack inner = createGlass((short) 15); // siyah cam (boşluklar için)

        // Üst ve alt sıraları doldur
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(36 + i, border);
        }
        // Sol ve sağ sütunları doldur
        for (int i = 1; i < 4; i++) {
            inv.setItem(i * 9, border);
            inv.setItem(i * 9 + 8, border);
        }
        // Orta boşlukları siyah cam ile doldur
        for (int i = 10; i < 35; i++) {
            if (i % 9 != 0 && i % 9 != 8) inv.setItem(i, inner);
        }

        // Dekoratif eşyalar
        inv.setItem(13, createDeco(Material.BLAZE_POWDER, "§6§lAteş Gücü",
                "§7Bu çubukla ateş topu fırlatabilirsin.",
                "§7Cooldown: §c3 saniye"));

        inv.setItem(29, createDeco(Material.PAPER, "§a§lBilgi",
                "§7Çubuğu almak için ortadaki",
                "§7çubuğa tıklaman yeterli."));

        inv.setItem(33, createDeco(Material.REDSTONE, "§c§lDikkat",
                "§7Çubuk 3 saniyede bir ateş topu",
                "§7fırlatabilir."));

        // Ortada ana çubuk (tıklanabilir)
        inv.setItem(22, createWand());

        // Açılma sesi
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1.5f);

        player.openInventory(inv);
    }

    private ItemStack createGlass(short data) {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createDeco(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createWand() {
        ItemStack wand = new ItemStack(Material.STICK);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName("§e§lAteş Çubuğu");
        meta.setLore(Arrays.asList(
                "§8§m----------------",
                "§7Sağ tıkla: §cAteş Topu",
                "§7Cooldown: §c3s",
                "§8§m----------------",
                "§a» §fAlmak için tıkla"
        ));
        wand.setItemMeta(meta);
        return wand;
    }

    private boolean isWand(ItemStack item) {
        if (item == null || item.getType() != Material.STICK) return false;
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals("§e§lAteş Çubuğu");
    }

    // ================== GUI TIKLAMA ==================
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(MENU_TITLE)) return;
        event.setCancelled(true);

        if (event.getCurrentItem() != null && isWand(event.getCurrentItem())) {
            Player player = (Player) event.getWhoClicked();

            // Tıklama sesi
            player.playSound(player.getLocation(), Sound.CLICK, 1f, 1.2f);

            player.closeInventory();
            player.getInventory().addItem(createWand());

            // Alım onay sesi
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.8f, 1.4f);
            player.sendMessage("§8§l» §aAteş Çubuğu envanterine eklendi! §8§l«");
        }
    }

    // ================== GUI KAPANMA SESİ ==================
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(MENU_TITLE)) return;

        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            // Kapanma sesi
            player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 0.7f, 1.3f);
        }
    }

    // ================== ÇUBUK KULLANIMI ==================
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (!isWand(item)) return;

        if (cooldowns.containsKey(player.getUniqueId())) {
            long remaining = cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
            if (remaining > 0) {
                player.sendMessage("§8§l» §cCooldown! Kalan: §e" + (int) Math.ceil(remaining / 1000.0) + "s");
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0.8f, 1f);
                return;
            }
        }

        player.launchProjectile(Fireball.class);
        player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1f, 1f);
        player.sendMessage("§8§l» §cAteş Topu fırlatıldı! §8§l«");

        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + COOLDOWN_MS);
        player.setLevel(3);
        player.setExp(1f);
    }

    // ================== /copeat ==================
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
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1f, 1f);
            player.sendMessage("§8§l» §a" + removed + " adet Ateş Çubuğu silindi. §8§l«");
        } else {
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0.8f, 1f);
            player.sendMessage("§8§l» §cEnvanterinde Ateş Çubuğu yok! §8§l«");
        }
    }
}
