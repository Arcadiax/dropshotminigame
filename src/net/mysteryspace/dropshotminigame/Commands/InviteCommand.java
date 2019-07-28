package net.mysteryspace.dropshotminigame.Commands;

import net.mysteryspace.dropshotminigame.Arena.Arena;
import net.mysteryspace.dropshotminigame.Arena.ArenaManager;
import net.mysteryspace.dropshotminigame.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteCommand implements CommandExecutor {

    private Main _plugin;

    public InviteCommand (Main plugin){
        _plugin = plugin;
        _plugin.getCommand("invite").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] bits){

        //TODO: check the player hasn't invited themselves
        Player p = (Player)sender;

        if(p == null){
            sender.sendMessage("Only players may use this command");
            return true;
        }

        Player other = Bukkit.getPlayer(bits[0]);
        if(other == null){
            p.sendMessage("Player " + bits[0] + " not found");
            return true;
        }

        Arena arena = ArenaManager.GetInstance().GetPlayerArena(p);
        if(arena == null){
            p.sendMessage("You're not in a game, use /creategame to start a new one");
            return true;
        }

        ArenaManager.GetInstance().SendInvite(p, other, arena);
        p.sendMessage("Invite sent, it will time out in 30 seconds");

        return true;
    }
}
