package net.mysteryspace.dropshotminigame.Util;

import net.mysteryspace.dropshotminigame.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Iterator;

public class Timers {
    private static Timers _instance;
    private Main _plugin;

    private int _taskId;

    private ArrayList<Timer> _timers;

    public static Timers GetInstance(){
        return _instance;
    }

    public Timers(Main plugin){
        _plugin = plugin;
        _instance = this;

        _timers = new ArrayList<>();

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        _taskId = scheduler.scheduleSyncRepeatingTask(_plugin, new Runnable(){
            @Override
            public void run(){
                TickTimers();
            }
        }, 0L, 20L);
    }

    private void TickTimers(){
        if(_timers.isEmpty())
            return;

        Iterator<Timer> iterator = _timers.iterator();
        while(iterator.hasNext()){
            Timer timer = iterator.next();

            timer.Tick();

            if(timer.IsDisposed()){
                iterator.remove();
            }
        }
    }

    public Timer NewTimer(int time){
        Timer t = new Timer(time);
        _timers.add(t);
        return t;
    }
}