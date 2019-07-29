package net.mysteryspace.dropshotminigame.Arena;

import net.mysteryspace.dropshotminigame.Main;
import net.mysteryspace.dropshotminigame.Util.Helpers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Iterator;

public class ArenaManager {

    private Main _plugin;
    private static ArenaManager _instance;

    private Arena[] _arenas;
    private ArrayList<ArenaInvite> _invites;
    private int _inviteTaskId;
    private int _inviteLength;

    private ArrayList<Player> _onlinePlayers;
    private int _playersTaskId;

    private int _gameLength;
    private int _startGameCountdownLength;

    private Location _hubLocation;

    public static ArenaManager GetInstance(){
        return _instance;
    }

    public ArenaManager(Main plugin){
        _plugin = plugin;

        if(_instance == null)
            _instance = this;

        ConfigurationSection config = plugin.getConfig().getConfigurationSection("arenas");
        GetMiscConfig(plugin.getConfig());
        SetupFromConfig(config);
    }

    private void SetupFromConfig(ConfigurationSection config){
        _onlinePlayers = new ArrayList<Player>();

        int arenaCount = config.getInt("arenaCount");

        _inviteLength = config.getInt("inviteLength");
        _invites = new ArrayList<ArenaInvite>();

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

        _inviteTaskId = scheduler.scheduleSyncRepeatingTask(_plugin, new Runnable(){
            @Override
            public void run(){
                TickInvites();
            }
        }, 0L, 20L);

        _playersTaskId = scheduler.scheduleSyncRepeatingTask(_plugin, new Runnable(){
            @Override
            public void run(){
                TickPlayerList();
            }
        }, 0L, 60L);

        _arenas = new Arena[arenaCount];
        for(int i = 0; i < arenaCount; i++){
            ConfigurationSection arenaConfig = config.getConfigurationSection("arena" + i);
            _arenas[i] = ArenaFromConfiguration(arenaConfig);
        }
    }

    private void GetMiscConfig(ConfigurationSection config){
        ConfigurationSection settings = config.getConfigurationSection("settings");
        _gameLength = settings.getInt("gameLength");
        _startGameCountdownLength = settings.getInt("startGameCountdown");

        ConfigurationSection hub = config.getConfigurationSection("hub");
        _hubLocation = Helpers.LocationAndFacingFromConfig(hub.getConfigurationSection("spawn"));
    }

    private Arena ArenaFromConfiguration(ConfigurationSection config){
        //Wait Location
        ConfigurationSection waitSpawnConfig = config.getConfigurationSection("waitSpawn");
        Location waitSpawn = Helpers.LocationFromConfig(waitSpawnConfig);

        //Player 1 Spawn
        ConfigurationSection p1SpawnConfig = config.getConfigurationSection("p1Spawn");
        Location p1Spawn = Helpers.LocationAndFacingFromConfig(p1SpawnConfig);

        //Player 2 Spawn
        ConfigurationSection p2SpawnConfig = config.getConfigurationSection("p2Spawn");
        Location p2Spawn = Helpers.LocationAndFacingFromConfig(p2SpawnConfig);

        return new Arena(_plugin, waitSpawn, p1Spawn, p2Spawn);
    }

    public Arena GetEmptyArena(){
        for(Arena arena : _arenas){
            if(arena.IsEmpty())
                return arena;
        }

        return null;
    }

    public void SendInvite(Player fromPlayer, Player toPlayer, Arena arena){
        ArenaInvite invite = new ArenaInvite(_plugin, fromPlayer, toPlayer, arena, _inviteLength, ArenaInvite.GetNextId());
        _invites.add(invite);
    }

    public boolean AcceptInvite(int inviteId){

        Iterator<ArenaInvite> iterator = _invites.iterator();
        while(iterator.hasNext()){
            ArenaInvite inv = iterator.next();
            if(inv.GetId() == inviteId){
                inv.Accept();
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    private Runnable TickInvites(){
        if(_invites.isEmpty())
            return null;

        Iterator<ArenaInvite> iterator = _invites.iterator();
        while(iterator.hasNext()){
            ArenaInvite inv = iterator.next();

            inv.TickInvite();

            if(inv.Expired()) {
                inv.FinalNotice();
                iterator.remove();
            }
        }

        return null;
    }

    private Runnable TickPlayerList(){
        _onlinePlayers.clear();
        for(Player p : Bukkit.getOnlinePlayers()){
            _onlinePlayers.add(p);
        }

        return null;
    }

    public Arena GetPlayerArena(Player p){
        for(Arena arena : _arenas){
            if(arena.HasPlayer(p))
                return arena;
        }

        return null;
    }

    public void HandlePlayerQuit(Player p){
        for(Arena arena : _arenas){
            if(arena.HasPlayer(p))
                arena.PlayerQuit(p);
        }
    }

    public void HandleArrowHitBlockEvent(Player p, Block b){
        if(p == null)
            return;

        if(b == null)
            return;

        Arena pArena = GetPlayerArena(p);
        if(pArena == null)
            return;

        pArena.HandleArrowHit(p, GetScoreFromBlock(b));
    }

    public void HandleFallDamage(Player p){
        for(Arena arena : _arenas){
            if(arena.HasPlayer(p)) {
                arena.HandleFallDamage(p);
            }
        }
    }

    public int GetStartGameCountdownLength(){
        return _startGameCountdownLength;
    }

    public int GetGameLength(){
        return _gameLength;
    }

    public Location HubLocation(){
        return _hubLocation;
    }

    private int GetScoreFromBlock(Block b){
        return GetScoreFromMaterial(b.getType());
    }

    private int GetScoreFromMaterial(Material material){
        //TODO: Put these in config
        switch(material){
            case WHITE_WOOL:
                return 1;

            case BLACK_WOOL:
                return 2;

            case CYAN_WOOL:
                return 3;

            case RED_WOOL:
                return 4;

            case YELLOW_WOOL:
                return 5;

            default:
                return 0;
        }
    }

    public int GetMaxScore(){
        return GetScoreFromMaterial(Material.YELLOW_WOOL);
    }
}
