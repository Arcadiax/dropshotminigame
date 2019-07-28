package net.mysteryspace.dropshotminigame.Commands;

import net.mysteryspace.dropshotminigame.Arena.Arena;
import net.mysteryspace.dropshotminigame.Arena.ArenaManager;
import net.mysteryspace.dropshotminigame.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateGameCommand implements CommandExecutor {

    private Main _plugin;

    public CreateGameCommand(Main plugin){
        _plugin = plugin;
        _plugin.getCommand("createGame").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] bits){

        Player p = (Player)sender;

        if(p == null){
            sender.sendMessage("Only players may use this command");
            return true;
        }

        Arena arena = ArenaManager.GetInstance().GetEmptyArena();

        if(arena == null){
            sender.sendMessage("No open slots");
            return true;
        }

        arena.PlayerJoin(p);
        sender.sendMessage("Creating new match! Invite a friend using /invite");

        return true;
    }

}
