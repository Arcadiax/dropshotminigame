package net.mysteryspace.dropshotminigame.Util;

import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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

    public static void SendGameInfoMessage(Player p, String message){
        IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(message);
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.GAME_INFO);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
    }

    public static void SendSpecialChatMessage(Player p, String message){
        IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(message);
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.CHAT);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
    }
}
