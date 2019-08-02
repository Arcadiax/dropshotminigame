package net.mysteryspace.dropshotminigame.Arena;

import net.mysteryspace.dropshotminigame.Main;
import net.mysteryspace.dropshotminigame.Util.Helpers;
import net.mysteryspace.dropshotminigame.Util.Timer;
import net.mysteryspace.dropshotminigame.Util.Timers;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Arena {

    private Location _waitSpawn;
    private Location _p1Spawn;
    private Location _p2Spawn;
    private double _holeExitHeight;

    private ArrayList<Player> _players;
    private HashMap<Player, ItemStack[]> _playerTempInventory;
    private HashMap<Player, Location> _playerTempLocations;
    private Timer _timer;
    private int _logicTaskId;

    private Scoreboard _scoreboard;
    private Team[] _scoreboardTeams;
    private Objective _scoreObjective;

    private Main _plugin;

    private ArenaGameState _state;

    public Arena(Main plugin, Location waitSpawn, Location p1Spawn, Location p2Spawn, double holeExitHeight){
        _plugin = plugin;

        _waitSpawn = waitSpawn;
        _p1Spawn = p1Spawn;
        _p2Spawn = p2Spawn;
        _holeExitHeight = holeExitHeight;

        _players = new ArrayList<>();
        _playerTempInventory = new HashMap<>();
        _playerTempLocations = new HashMap<>();

        _state = ArenaGameState.WAITING;

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

        _logicTaskId = scheduler.scheduleSyncRepeatingTask(_plugin, new Runnable() {
            @Override
            public void run() {
                GameLogic();
            }
        }, 0, 20);

        SetupScoreboard();
    }

    public boolean IsEmpty(){
        return _players.isEmpty();
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
        for(Player p : _players){
            Helpers.SendGameInfoMessage(p, "{\"text\":\"§b§l§nGame Starting in §e§l§n" + Helpers.SecondsToString(_timer.GetTime()) + "\"}");
        }
    }

    private void SendGameCounterMessage(){
        for(Player p : _players){
            Helpers.SendGameInfoMessage(p, "{\"text\":\"§b§l§nTime Left: §e§l§n" + Helpers.SecondsToString(_timer.GetTime()) + "\"}");
        }
    }

    private void SendKickoutCounterMessage(){
        for(Player p : _players){
            Helpers.SendGameInfoMessage(p, "{\"text\":\"§b§l§nLeaving Game in: §e§l§n" + Helpers.SecondsToString(_timer.GetTime())+ "\"}");
        }

    }

    public void PlayerJoin(Player p){
        if(_players.size() == 2){
            p.sendMessage("Could not join game, it's full");
            return;
        }

        if(_state != ArenaGameState.WAITING){
            p.sendMessage("Game is already in progress");
            return;
        }

        if(!_plugin.GetSettings().IntegratedHub){
            _playerTempLocations.put(p, p.getLocation());
        }

        if(_plugin.GetSettings().SaveInventoryBetweenGames) {
            _playerTempInventory.put(p, p.getInventory().getContents().clone());
        }

        p.getInventory().clear();

        _players.add(p);
        p.teleport(_waitSpawn);

        if(_players.size() == 2)
            StartCountdown();
    }

    public void PlayerQuit(Player p){
        int index = _players.indexOf(p);
        if(_scoreboardTeams[index].hasEntry(GetPlayerNiceId(p)))
            _scoreboardTeams[index].removeEntry(GetPlayerNiceId(p));

        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        if(_playerTempLocations.containsKey(p)){
            p.teleport(_playerTempLocations.get(p));
            _playerTempLocations.remove(p);
        } else if(_plugin.GetSettings().IntegratedHub)
            p.teleport(ArenaManager.GetInstance().HubLocation());

        if(_playerTempInventory.containsKey(p)){
            p.getInventory().setContents(_playerTempInventory.get(p).clone());
            _playerTempInventory.remove(p);
        }

        if(_players.size() == 1) {
            ResetArena();
        }

        if(_players.size() == 2) {
            if(_state == ArenaGameState.PLAYING) {
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
        _playerTempLocations = new HashMap<>();
        _playerTempInventory = new HashMap<>();
    }

    private void StartGame(){
        _players.get(0).teleport(_p1Spawn);
        _players.get(1).teleport(_p2Spawn);
        _timer = Timers.GetInstance().NewTimer(ArenaManager.GetInstance().GetGameLength());
        PreparePlayerScoreboard();
        GivePlayersKit();
        _state = ArenaGameState.PLAYING;
    }

    private void EndGame(){
        _players.get(0).teleport(_waitSpawn);
        _players.get(1).teleport(_waitSpawn);

        int[] scores = {
                _scoreObjective.getScore(GetPlayerNiceId(0)).getScore(),
                _scoreObjective.getScore(GetPlayerNiceId(1)).getScore()
        };

        _timer = Timers.GetInstance().NewTimer(ArenaManager.GetInstance().GetStartGameCountdownLength());

        int winnerId = 0;
        int secondId = 1;
        if(scores[1] > scores[0]) {
            winnerId = 1;
            secondId = 0;
        }
        BroadcastToPlayers("§a§l" + GetPlayerNiceId(winnerId) + " Wins with " + scores[winnerId] + " points!");
        BroadcastToPlayers("§f§l" + GetPlayerNiceId(secondId) + " got " + scores[secondId] + " points");

        TakePlayerKits();
        _state = ArenaGameState.FINISHED;
    }

    private void EjectPlayers(){
        for(Player p : _players){
            PlayerQuit(p);
        }
    }

    public void HandleFallDamage(Player p){
        if(_state != ArenaGameState.PLAYING)
            return;

        if(_players.get(0) == p)
            p.teleport(_p1Spawn);
        else if(_players.get(1) == p)
            p.teleport(_p2Spawn);
    }

    public void HandleArrowHit(Player p, int points){
        if(_state != ArenaGameState.PLAYING)
            return;

        if(points <= 0)
            return;

        AddPlayerPoints(p, points);
    }

    private void AddPlayerPoints(Player p, int points){
        if(points == ArenaManager.GetInstance().GetMaxScore())
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
        else
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);

        Score score = _scoreObjective.getScore(GetPlayerNiceId(p));
        int oldScore = score.getScore();
        score.setScore(oldScore + points);
    }

    public void SetupScoreboard(){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        _scoreboard = manager.getNewScoreboard();
        _scoreboardTeams = new Team[2];
        _scoreboardTeams[0] = _scoreboard.registerNewTeam("0");
        _scoreboardTeams[1] = _scoreboard.registerNewTeam("1");
        _scoreObjective = _scoreboard.registerNewObjective("score", "dummy", "Points");
        _scoreObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private void PreparePlayerScoreboard(){
        _scoreboardTeams[0].addEntry(GetPlayerNiceId(0));
        _scoreboardTeams[1].addEntry(GetPlayerNiceId(1));

        for(Player p : _players) {
            Score score = _scoreObjective.getScore(GetPlayerNiceId(p));
            score.setScore(0);
            p.setScoreboard(_scoreboard);
        }
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

    private void TakePlayerKits(){
        for(Player p : _players){
            p.getInventory().clear();
        }
    }

    public boolean IsWaiting(){return _state == ArenaGameState.WAITING; }

    public double GetHoleExitHeight(){
        return _holeExitHeight;
    }

    private String GetPlayerNiceId(int index){
        return _players.get(index).getDisplayName();
    }
    private String GetPlayerNiceId(Player p){
        return p.getDisplayName();
    }
}
