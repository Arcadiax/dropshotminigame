package net.mysteryspace.dropshotminigame.Commands;

import net.mysteryspace.dropshotminigame.Arena.Arena;
import net.mysteryspace.dropshotminigame.Arena.ArenaManager;
import net.mysteryspace.dropshotminigame.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptInviteCommand implements CommandExecutor {

    private Main _plugin;

    public AcceptInviteCommand (Main plugin){
        _plugin = plugin;
        _plugin.getCommand("acceptInvite").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] bits){

        Player p = (Player)sender;

        if(p == null){
            sender.sendMessage("Only players can use this command");
            return true;
        }

        int inviteId = Integer.parseInt(bits[0]);
        if(!ArenaManager.GetInstance().AcceptInvite(inviteId)){
            sender.sendMessage("Invite not found, it probably expired");
        }



        return true;
    }
}
