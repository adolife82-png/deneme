package com.cubuk.plugin.menu;

import com.cubuk.plugin.CubukPlugin;
import com.cubuk.plugin.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class DeleteConfirmMenu {

    public static final String TITLE = "§8§l✦ §c§lSilmeyi Onayla §8§l✦";

    public static void open(Player player, CubukPlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 14).name(" ");
        for (int i = 0; i < 27; i++) inv.setItem(i, glass.build());

        inv.setItem(11, new ItemBuilder(Material.EMERALD_BLOCK)
                .name("§a§l✔ EVET, SİL")
                .lore(Arrays.asList(
                        "§8§m─────────────────",
                        "§7Envanterindeki tüm",
                        "§6Ateş Çubukları §7silinecek.",
                        "§8§m─────────────────"
                )).glow(true).build());

        inv.setItem(13, new ItemBuilder(Material.BARRIER)
                .name("§c§l⚠ EMİN MİSİN?")
                .lore(Arrays.asList(
                        "§7Bu işlem geri alınamaz!",
                        "§7Çubuklar kalıcı olarak silinecek."
                )).build());

        inv.setItem(15, new ItemBuilder(Material.REDSTONE_BLOCK)
                .name("§c§l✘ HAYIR, İPTAL")
                .lore(Arrays.asList(
                        "§8§m─────────────────",
                        "§7Menüyü kapatır,",
                        "§7hiçbir şey silinmez.",
                        "§8§m─────────────────"
                )).build());

        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1f, 1f);
        player.openInventory(inv);
    }
}
