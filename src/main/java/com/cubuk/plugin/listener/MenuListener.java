package com.cubuk.plugin.listener;

import com.cubuk.plugin.CubukPlugin;
import com.cubuk.plugin.menu.DeleteConfirmMenu;
import com.cubuk.plugin.menu.WandMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {

    private final CubukPlugin plugin;

    public MenuListener(CubukPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onWandMenuClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(WandMenu.TITLE)) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;

        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem().getType() == Material.BARRIER) {
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 0.8f, 1f);
            p.sendMessage("§8§l» §cZaten çubuğun var! §7Sil: §f/sil");
            return;
        }

        if (e.getCurrentItem().getType() == Material.STICK) {
            if (plugin.getWandManager().hasWand(p)) {
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 0.8f, 1f);
                p.sendMessage("§8§l» §cZaten çubuğun var!");
                return;
            }
            p.closeInventory();
            p.getInventory().addItem(plugin.getWandManager().createWand());
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 0.8f, 1.4f);
            p.sendMessage("§8§l» §aAteş Çubuğu envanterine eklendi! §8§l«");
        }
    }

    @EventHandler
    public void onDeleteMenuClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(DeleteConfirmMenu.TITLE)) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;

        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem().getType() == Material.EMERALD_BLOCK) {
            int count = plugin.getWandManager().removeAllWands(p);
            p.closeInventory();
            if (count > 0) {
                p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1f, 1f);
                p.sendMessage("§8§l» §a" + count + " adet Ateş Çubuğu silindi. §8§l«");
            } else {
                p.playSound(p.getLocation(), Sound.VILLAGER_NO, 0.8f, 1f);
                p.sendMessage("§8§l» §cEnvanterinde çubuk yok!");
            }
        } else if (e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.CLICK, 1f, 0.8f);
            p.sendMessage("§8§l» §7Silme işlemi iptal edildi.");
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        if (title.equals(WandMenu.TITLE) || title.equals(DeleteConfirmMenu.TITLE)) {
            if (e.getPlayer() instanceof Player) {
                ((Player) e.getPlayer()).playSound(e.getPlayer().getLocation(), Sound.CHEST_CLOSE, 0.7f, 1.3f);
            }
        }
    }
}
