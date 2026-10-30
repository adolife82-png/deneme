package com.cubuk.plugin.menu;

import com.cubuk.plugin.CubukPlugin;
import com.cubuk.plugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class WandMenu {

    public static final String TITLE = "§8§l✦ §e§lAteş Çubuğu Menüsü §8§l✦";

    public static void open(Player player, CubukPlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE);

        ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7).name(" ");
        ItemBuilder inner = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).name(" ");

        for (int i = 0; i < 9; i++) { inv.setItem(i, glass.build()); inv.setItem(36 + i, glass.build()); }
        for (int i = 1; i < 4; i++) { inv.setItem(i * 9, glass.build()); inv.setItem(i * 9 + 8, glass.build()); }
        for (int i = 10; i < 35; i++) if (i % 9 != 0 && i % 9 != 8) inv.setItem(i, inner.build());

        inv.setItem(13, new ItemBuilder(Material.BLAZE_POWDER)
                .name("§6§l✦ Ateş Gücü")
                .lore(Arrays.asList(
                        "§8§m─────────────────",
                        "§7Bu çubukla ateş topu fırlatabilirsin.",
                        "§7Cooldown: §e" + plugin.getConfig().getInt("cooldown-seconds", 3) + "s",
                        "§7Gösterge: §aHotbar (XP Bar)",
                        "§8§m─────────────────"
                )).glow(true).build());

        inv.setItem(29, new ItemBuilder(Material.PAPER)
                .name("§a§lℹ Bilgi")
                .lore(Arrays.asList(
                        "§7Çubuğu almak için",
                        "§7ortadaki çubuğa tıkla."
                )).build());

        inv.setItem(33, new ItemBuilder(Material.REDSTONE)
                .name("§c§l⚠ Dikkat")
                .lore(Arrays.asList(
                        "§7Çubuk " + plugin.getConfig().getInt("cooldown-seconds", 3) + " saniyede",
                        "§7bir ateş topu fırlatır."
                )).build());

        // Orta: çubuk veya bariyer
        if (plugin.getWandManager().hasWand(player)) {
            inv.setItem(22, new ItemBuilder(Material.BARRIER)
                    .name("§c§l✘ Zaten Çubuğun Var!")
                    .lore(Arrays.asList(
                            "§8§m─────────────────",
                            "§7Envanterinde zaten Ateş Çubuğu var.",
                            "§7Silmek için: §f/sil",
                            "§8§m─────────────────"
                    )).build());
        } else {
            inv.setItem(22, new ItemBuilder(Material.STICK)
                    .name("§6§l✦ §e§lAteş Çubuğu §6§l✦")
                    .lore(Arrays.asList(
                            "§8§m─────────────────",
                            "§7Sağ tık: §cAteş Topu",
                            "§7Cooldown: §e" + plugin.getConfig().getInt("cooldown-seconds", 3) + "s",
                            "§8§m─────────────────",
                            "§a▶ §fAlmak için tıkla"
                    )).glow(true).build());
        }

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1.5f);
        player.openInventory(inv);
    }
}
