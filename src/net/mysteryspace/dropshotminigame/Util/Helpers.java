package net.mysteryspace.dropshotminigame.Util;

import net.mysteryspace.dropshotminigame.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class Helpers {

    public static Location LocationFromConfig(ConfigurationSection config){
        return new Location(
                Bukkit.getWorld(config.getString("world")),
                config.getDouble("x"),
                config.getDouble("y"),
                config.getDouble("z")
        );
    }

    public static Location LocationAndFacingFromConfig(ConfigurationSection config){
        return new Location(
                Bukkit.getWorld(config.getString("world")),
                config.getDouble("x"),
                config.getDouble("y"),
                config.getDouble("z"),
                (float)config.getDouble("fx"),
                (float)config.getDouble("fy")
        );
    }

    public static String SecondsToString(int time){
        int min = time / 60;
        int sec = time-(min*60);

        return String.format("%s:%s", AddLeadingZero(min), AddLeadingZero(sec));
    }

    public static String AddLeadingZero(int number) {
        return (number >=10)? Integer.toString(number):String.format("0%s",Integer.toString(number));
    }

    public static int scheduleSyncRepeatingTask(final Main plugin, final Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period).getTaskId();
    }
}
