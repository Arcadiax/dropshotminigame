package net.mysteryspace.dropshotminigame.Arena;

import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;
import net.mysteryspace.dropshotminigame.Main;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ArenaInvite {

    private Main _plugin;

    private Player _fromPlayer;
    private Player _toPlayer;
    private Arena _arena;
    private int _timer;
    private int _id;

    public ArenaInvite(Main plugin, Player fromPlayer, Player toPlayer, Arena arena, int time, int id){
        _plugin = plugin;
        _arena = arena;
        _fromPlayer = fromPlayer;
        _toPlayer = toPlayer;
        _arena = arena;
        _timer = time;
        _id = id;

        InitialNotice();
    }

    public void TickInvite(){
        _timer -= 1;
    }

    public boolean Expired(){
        return _timer <= 0;
    }

    public int GetId(){
        return _id;
    }

    public void InitialNotice(){
        IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"You have been invited to play by " + _fromPlayer.getDisplayName() + " \",\"extra\":[{\"text\":\"§bJoin Match\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to join\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/acceptInvite " + _id + "\"}}]}");
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.CHAT);
        ((CraftPlayer)_toPlayer).getHandle().playerConnection.sendPacket(packet);
    }

    public void FinalNotice(){
        _toPlayer.sendMessage("Your invite from " + _fromPlayer.getDisplayName() + " has expired");
        _fromPlayer.sendMessage("Your invite to " + _toPlayer.getDisplayName() + " has expired");
    }

    public void Accept(){
        _arena.PlayerJoin(_toPlayer);
        _fromPlayer.sendMessage(_toPlayer.getDisplayName() + " accepted your invite!");
    }

    //Static Stuff
    private static int _idCounter = 0;

    public static int GetNextId(){
        _idCounter++;
        return _idCounter;
    }
}
