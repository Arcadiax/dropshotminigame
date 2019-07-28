package net.mysteryspace.dropshotminigame.Commands;

import net.mysteryspace.dropshotminigame.Arena.Arena;
import net.mysteryspace.dropshotminigame.Arena.ArenaManager;
import net.mysteryspace.dropshotminigame.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveGameCommand implements CommandExecutor {

    private Main _plugin;

    public LeaveGameCommand(Main plugin){
        _plugin = plugin;
        _plugin.getCommand("leaveGame").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] bits){

        Player p = (Player)sender;

        if(p == null){
            sender.sendMessage("Only players may use this command");
            return true;
        }

        Arena arena = ArenaManager.GetInstance().GetPlayerArena(p);

        if(arena == null){
            sender.sendMessage("You're not in a game");
            return true;
        }

        arena.PlayerQuit(p);

        return true;
    }

}
