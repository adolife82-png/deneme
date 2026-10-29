private void spawnBossBar(Player player) {
    removeBossBar(player.getUniqueId());

    Location loc = player.getLocation().clone().add(0, -500, 0);
    Wither wither = (Wither) player.getWorld().spawnEntity(loc, EntityType.WITHER);
    wither.setCustomName("§c§lCooldown: §e3s §8| §7Ateş Çubuğu");
    wither.setCustomNameVisible(true);
    wither.setHealth(wither.getMaxHealth());
    // setInvulnerable ve setSilent 1.8'de yok - kaldırıldı
    wither.setCanPickupItems(false);

    // 1.8'de hideEntity yok - diğer oyuncular zaten 500 blok altındaki Wither'ı göremez

    bossBars.put(player.getUniqueId(), wither);

    new BukkitRunnable() {
        @Override
        public void run() {
            Wither w = bossBars.get(player.getUniqueId());
            if (w == null || w.isDead() || !player.isOnline()) {
                cancel();
                return;
            }
            w.teleport(player.getLocation().clone().add(0, -500, 0));
        }
    }.runTaskTimer(this, 0L, 5L);
}
