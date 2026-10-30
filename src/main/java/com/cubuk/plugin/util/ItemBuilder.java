package com.cubuk.plugin.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material mat) { this(mat, 1, (short) 0); }
    public ItemBuilder(Material mat, int amount) { this(mat, amount, (short) 0); }
    public ItemBuilder(Material mat, int amount, short data) {
        this.item = new ItemStack(mat, amount, data);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder name(String name) { meta.setDisplayName(name); return this; }
    public ItemBuilder lore(List<String> lore) { meta.setLore(lore); return this; }

    public ItemBuilder glow(boolean glow) {
        if (glow) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemStack build() { item.setItemMeta(meta); return item; }
}
