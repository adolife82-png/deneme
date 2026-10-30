package com.cubuk.plugin.manager;

import com.cubuk.plugin.CubukPlugin;
import com.cubuk.plugin.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class WandManager {

    private final CubukPlugin plugin;

    public WandManager(CubukPlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack createWand() {
        return new ItemBuilder(Material.STICK)
                .name("§6§l✦ §e§lAteş Çubuğu §6§l✦")
                .lore(Arrays.asList(
                        "§8§m─────────────────────",
                        "§7Sağ tık: §cAteş Topu Fırlat",
                        "§7Cooldown: §e" + plugin.getConfig().getInt("cooldown-seconds", 3) + "s",
                        "§8§m─────────────────────",
                        "§a✔ §7Kullanıma hazır!"
                ))
                .glow(true)
                .build();
    }

    public boolean isWand(ItemStack item) {
        if (item == null || item.getType() != Material.STICK) return false;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;
        return item.getItemMeta().getDisplayName().equals("§6§l✦ §e§lAteş Çubuğu §6§l✦");
    }

    public boolean hasWand(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isWand(item)) return true;
        }
        return false;
    }

    public int removeAllWands(Player player) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (isWand(player.getInventory().getItem(i))) {
                player.getInventory().setItem(i, null);
                count++;
            }
        }
        return count;
    }
}
