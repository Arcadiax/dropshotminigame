package net.mysteryspace.dropshotminigame.Arena;

import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;
import net.mysteryspace.dropshotminigame.Main;
import net.mysteryspace.dropshotminigame.Util.Helpers;
import net.mysteryspace.dropshotminigame.Util.Timer;
import net.mysteryspace.dropshotminigame.Util.Timers;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Arena {

    private Location _waitSpawn;
    private Location _p1Spawn;
    private Location _p2Spawn;

    private ArrayList<Player> _players;
    private Timer _timer;
    private int _logicTaskId;

    private int[] _playerScores;

    private Main _plugin;

    private ArenaGameState _state;

    public Arena(Main plugin, Location waitSpawn, Location p1Spawn, Location p2Spawn){
        _plugin = plugin;

        _waitSpawn = waitSpawn;
        _p1Spawn = p1Spawn;
        _p2Spawn = p2Spawn;

        _players = new ArrayList<>();
        _playerScores = new int[2];

        _state = ArenaGameState.WAITING;

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

        _logicTaskId = scheduler.scheduleSyncRepeatingTask(_plugin, new Runnable() {
            @Override
            public void run() {
                GameLogic();
            }
        }, 0, 20);
    }

    public boolean IsEmpty(){
        return _players.isEmpty();
    }

    public void PlayerJoin(Player p){
        if(_players.size() == 2){
            p.sendMessage("Could not join game, it's full");
            return;
        }

        _players.add(p);
        p.teleport(_waitSpawn);

        if(_players.size() == 2)
            StartCountdown();
    }

    private void StartCountdown(){
        _timer = Timers.GetInstance().NewTimer(ArenaManager.GetInstance().GetStartGameCountdownLength());
    }

    public boolean HasPlayer(Player p){
        if(_players.contains(p))
            return true;

        return false;
    }

    public void GameLogic() {
        if (_state == ArenaGameState.WAITING) {
            if (_timer != null) {
                SendCountdownMessage();
                if(_timer.GetTime() <= 0){
                    _timer.Dispose();
                    StartGame();
                }
            }
        } else if(_state == ArenaGameState.PLAYING){
            if (_timer != null) {
                SendGameCounterMessage();
                if(_timer.GetTime() <= 0){
                    _timer.Dispose();
                    EndGame();
                }
            }
        } else if(_state == ArenaGameState.FINISHED){
            if(_timer != null){
                SendKickoutCounterMessage();
                if(_timer.GetTime() <= 0){
                    _timer.Dispose();
                    EjectPlayers();
                    ResetArena();
                }
            }
        }
    }

    private void SendCountdownMessage(){
        //TODO: Move beginning of message to config
        IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§b§l§nGame Starting in §e§l§n" + Helpers.SecondsToString(_timer.GetTime()) + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.GAME_INFO);
        ((CraftPlayer)_players.get(0)).getHandle().playerConnection.sendPacket(packet);
    }

    private void SendGameCounterMessage(){
        //TODO: Move beginning of message to config
        IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§b§l§nTime Left: §e§l§n" + Helpers.SecondsToString(_timer.GetTime()) + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.GAME_INFO);

        for(Player p : _players){
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    private void SendKickoutCounterMessage(){
        //TODO: Move beginning of message to config
        IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a("{\"text\":\"§b§l§nMoving to Hub in: §e§l§n" + Helpers.SecondsToString(_timer.GetTime())+ "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.GAME_INFO);

        for(Player p : _players){
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
        }

    }

    public void PlayerQuit(Player p){
        if(_players.size() == 1) {
            _players = new ArrayList<>();
            ResetArena();
        }

        if(_players.size() == 2) {
            if(_state == ArenaGameState.PLAYING) {
                int ind = _players.indexOf(p);
                p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
            _players.remove(p);
            PartiallyResetArena();
        }
    }

    private void PartiallyResetArena(){
        if(_timer != null)
            _timer.Dispose();

        _state = ArenaGameState.WAITING;
    }

    private void ResetArena(){
        _timer = null;

        _state = ArenaGameState.WAITING;
        _players = new ArrayList<>();
        _playerScores = new int[2];
    }

    private void StartGame(){
        _players.get(0).teleport(_p1Spawn);
        _players.get(1).teleport(_p2Spawn);
        _timer = Timers.GetInstance().NewTimer(ArenaManager.GetInstance().GetGameLength());
        PreparePlayerScoreboard();
        _playerScores[0] = 0;
        _playerScores[1] = 0;
        GivePlayersKit();
        _state = ArenaGameState.PLAYING;
    }

    private void EndGame(){
        _players.get(0).teleport(_waitSpawn);
        _players.get(1).teleport(_waitSpawn);

        _timer = Timers.GetInstance().NewTimer(ArenaManager.GetInstance().GetStartGameCountdownLength());

        int winnerId = 0;
        int secondId = 1;
        if(_playerScores[1] > _playerScores[0]) {
            winnerId = 1;
            secondId = 0;
        }
        BroadcastToPlayers("§a§l" + _players.get(winnerId).getDisplayName() + " Wins with " + _playerScores[winnerId] + " points!");
        BroadcastToPlayers("§f§l" + _players.get(secondId).getDisplayName() + " got " + _playerScores[secondId] + " points");

        TakePlayerKit();
        _state = ArenaGameState.FINISHED;
    }

    private void EjectPlayers(){
        for(Player p : _players){
            p.teleport(ArenaManager.GetInstance().HubLocation());
        }
    }

    public void HandleFallDamage(Player p){
        if(_players.get(0) == p)
            p.teleport(_p1Spawn);
        else if(_players.get(1) == p)
            p.teleport(_p2Spawn);
    }

    public void HandleArrowHit(Player p, int points){

        if(_state != ArenaGameState.PLAYING) {
            return;
        }

        if(points <= 0)
            return;

        int ind = _players.indexOf(p);
        _playerScores[ind] += points;

        if(points == ArenaManager.GetInstance().GetMaxScore())
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
        else
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);

        BroadcastToPlayers("§c§l" + p.getDisplayName() + " Scored §e§l" + points + " points!");
    }

    private void PreparePlayerScoreboard(){

    }

    private void BroadcastToPlayers(String message){
        for(Player p : _players){
            p.sendMessage(message);
        }
    }

    private void GivePlayersKit(){

        ItemStack[] items = {
                new ItemStack(Material.BOW, 1),
                new ItemStack(Material.ARROW, 320)
        };

        for(Player p : _players){
            p.getInventory().clear();
            p.getInventory().addItem(items);
            p.getInventory().setHeldItemSlot(0);
        }
    }

    private void TakePlayerKit(){
        for(Player p : _players){
            p.getInventory().clear();
        }
    }
}
