package net.mysteryspace.dropshotminigame.Util;

import net.mysteryspace.dropshotminigame.Arena.Arena;
import net.mysteryspace.dropshotminigame.Arena.ArenaManager;
import net.mysteryspace.dropshotminigame.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class BaseGameSuppression implements Listener {
    private Main _plugin;

    public BaseGameSuppression(Main plugin){
        _plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, _plugin);
    }

    @EventHandler
    public void FoodLevelChangeEvent(FoodLevelChangeEvent event){
        if(!_plugin.GetSettings().SuppressHunger)
            return;

        event.setCancelled(true);

        Player p = (Player)event.getEntity();
        if(p == null)
            return;

        p.setFoodLevel(20);
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player))
            return;

        Player p = ((Player)event.getEntity()).getPlayer();

        if(p == null)
            return;

        if(event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            ArenaManager.GetInstance().HandleFallDamage(p);
            event.setCancelled(true);
            return;
        }

        if(_plugin.GetSettings().SuppressDamage)
            event.setCancelled(true);
    }

    @EventHandler
    public void EntityDamageByBlockEvent(EntityDamageByBlockEvent event){
        if(_plugin.GetSettings().SuppressDamage)
            event.setCancelled(true);
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if(_plugin.GetSettings().SuppressDamage)
            event.setCancelled(true);
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event){

        Player p = event.getPlayer();
        if(p == null)
            return;

        ArenaManager.GetInstance().HandlePlayerQuit(p);
    }

    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent event) {
        if(!(event.getEntity() instanceof Arrow)) //Remove to check all projectiles
            return;

        Arrow arrow = (Arrow)event.getEntity();
        if(!(arrow.getShooter() instanceof Player)) //Making sure the shooter is a player
            return;

        World world = arrow.getWorld();
        BlockIterator iterator = new BlockIterator(world, arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);
        Block hitBlock = null;

        while(iterator.hasNext()) {
            hitBlock = iterator.next();
            if(hitBlock.getType() != Material.AIR)
                break;
        }

        if(hitBlock == null)
            return;

        Player p = ((Player) arrow.getShooter()).getPlayer();
        ArenaManager.GetInstance().HandleArrowHitBlockEvent(p, hitBlock);
    }

    @EventHandler
    public void OnWeatherChange(WeatherChangeEvent event) {
        if(!_plugin.GetSettings().SuppressWeather)
            return;

        event.setCancelled(true);
        event.getWorld().setStorm(false);
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event){

        if(!_plugin.GetSettings().IntegratedHub)
            return;

        Player p = event.getPlayer();

        if(p == null)
            return;

        p.teleport(ArenaManager.GetInstance().HubLocation());
    }

    @EventHandler
    public void BlockDamageEvent(BlockDamageEvent event){

        if(_plugin.GetSettings().SuppressBlockDamage) {
            event.setCancelled(true);
            return;
        }

        Player p = event.getPlayer();
        if(p == null)
            return;

        if(ArenaManager.GetInstance().GetPlayerArena(p) != null)
            event.setCancelled(true);
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event){
        //TODO: Move to handle in arena
        Player p = event.getPlayer();

        if(p == null)
            return;

        Arena pArena = ArenaManager.GetInstance().GetPlayerArena(p);
        if(pArena == null)
            return;

        if(!pArena.IsPlaying())
            return;

        Vector pVel = p.getVelocity();
        if(pVel.getY() >= 0)
            return;

        //TODO: Tinker with fall speed
        p.setVelocity(new Vector(pVel.getX(), pVel.getY() * 0.5f, pVel.getZ()));
    }
}
