package com.cubuk.plugin.util;

import org.bukkit.Location;
import org.bukkit.Sound;

public class SoundUtil {
    public static void play(Location loc, Sound sound, float vol, float pitch) {
        if (loc.getWorld() != null) loc.getWorld().playSound(loc, sound, vol, pitch);
    }
}
