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
import org.bukkit.event.player.PlayerQuitEvent;
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

        // Cooldown Güncelleyici (Hotbar / XP Bar)
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : new HashMap<>(cooldowns).keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    
                    // Oyuncu çıktıysa veya yoksa temizle
                    if (player == null || !player.isOnline()) {
                        cooldowns.remove(uuid);
                        continue;
                    }

                    long remaining = cooldowns.get(uuid) - System.currentTimeMillis();

                    if (remaining <= 0) {
                        // Cooldown bitti
                        cooldowns.remove(uuid);
                        player.setLevel(0);
                        player.setExp(0f);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.8f, 1.5f);
                        player.sendMessage("§8§l» §a§lAteş Çubuğu Hazır! §8§l«");
                    } else {
                        // Cooldown devam ediyor
                        float progress = 1.0f - (remaining / (float) COOLDOWN_MS);
                        if (progress < 0f) progress = 0f;
                        if (progress > 1f) progress = 1f;
                        
                        // XP barını doldur
                        player.setExp(progress);
                        
                        // Seviye olarak kalan saniyeyi göster (yukarı ok / sayı)
                        int seconds = (int) Math.ceil(remaining / 1000.0);
                        player.setLevel(seconds);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 1L); // Her tick (saniyede 20 kez)
    }

    @Override
    public void onDisable() {
        // Sunucu kapanırken XP barlarını sıfırla
        for (UUID uuid : cooldowns.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.setLevel(0);
                player.setExp(0f);
            }
        }
        cooldowns.clear();
    }

    // ================== MODERN GUI ==================
    private void openWandMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, MENU_TITLE);

        ItemStack border = createGlass((short) 7);
        ItemStack inner = createGlass((short) 15);

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(36 + i, border);
        }
        for (int i = 1; i < 4; i++) {
            inv.setItem(i * 9, border);
            inv.setItem(i * 9 + 8, border);
        }
        for (int i = 10; i < 35; i++) {
            if (i % 9 != 0 && i % 9 != 8) inv.setItem(i, inner);
        }

        boolean hasWand = playerHasWand(player);

        inv.setItem(13, createDeco(Material.BLAZE_POWDER, "§6§lAteş Gücü",
                "§7Bu çubukla ateş topu fırlatabilirsin.",
                "§7Cooldown: §c3 saniye",
                "§7Gösterge: §aHotbar"));

        inv.setItem(29, createDeco(Material.PAPER, "§a§lBilgi",
                "§7Çubuğu almak için ortadaki",
                "§7çubuğa tıklaman yeterli."));

        inv.setItem(33, createDeco(Material.REDSTONE, "§c§lDikkat",
                "§7Çubuk 3 saniyede bir ateş topu",
                "§7fırlatabilir."));

        if (hasWand) {
            inv.setItem(22, createAlreadyHaveItem());
        } else {
            inv.setItem(22, createWand());
        }

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1.5f);
        player.openInventory(inv);
    }

    private ItemStack createAlreadyHaveItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lZaten Bir Çubuğun Var!");
        meta.setLore(Arrays.asList(
                "§8§m----------------",
                "§7Envanterinde zaten Ateş Çubuğu var.",
                "§7Tekrar almak için önce",
                "§7§f/copeat §7yazmalısın.",
                "§8§m----------------"
        ));
        item.setItemMeta(meta);
        return item;
    }

    private boolean playerHasWand(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isWand(item)) return true;
        }
        return false;
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

        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem().getType() == Material.BARRIER) {
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0.8f, 1f);
            player.sendMessage("§8§l» §cZaten bir Ateş Çubuğun var! §7Sil: §f/copeat");
            return;
        }

        if (!isWand(event.getCurrentItem())) return;

        if (playerHasWand(player)) {
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0.8f, 1f);
            player.sendMessage("§8§l» §cZaten bir Ateş Çubuğun var! §7Sil: §f/copeat");
            player.closeInventory();
            return;
        }

        player.playSound(player.getLocation(), Sound.CLICK, 1f, 1.2f);
        player.closeInventory();
        player.getInventory().addItem(createWand());
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.8f, 1.4f);
        player.sendMessage("§8§l» §aAteş Çubuğu envanterine eklendi! §8§l«");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(MENU_TITLE)) return;
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
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

        // Ateş topunu fırlat
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setIsIncendiary(true);
        fireball.setYield(1.5f);
        player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1f, 1f);
        player.sendMessage("§8§l» §cAteş Topu fırlatıldı! §8§l«");

        // Cooldown'ı başlat
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + COOLDOWN_MS);
        
        // XP barını hemen doldurmaya başla
        player.setLevel(3);
        player.setExp(0f);
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
