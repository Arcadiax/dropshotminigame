package net.mysteryspace.dropshotminigame.Arena;

import net.mysteryspace.dropshotminigame.Main;
import net.mysteryspace.dropshotminigame.Util.Helpers;
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
        Helpers.SendSpecialChatMessage(_toPlayer,
                "{\"text\":\"§a§lYou have been invited to play by " +
                        _fromPlayer.getDisplayName() +
                        " \",\"extra\":[{\"text\":\"§b§lJoin Match\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to join\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/dropshot acceptinvite " +
                        _id +
                        "\"}}]}");
    }

    public void FinalNotice(){
        _toPlayer.sendMessage("§aYour invite from " + _fromPlayer.getDisplayName() + " has expired");
        _fromPlayer.sendMessage("§aYour invite to " + _toPlayer.getDisplayName() + " has expired");
    }

    public void Accept(){
        _arena.PlayerJoin(_toPlayer);
        _fromPlayer.sendMessage(_toPlayer.getDisplayName() + " accepted your invite!");
    }

    private static int _idCounter = 0;

    public static int GetNextId(){
        _idCounter++;
        return _idCounter;
    }
}
