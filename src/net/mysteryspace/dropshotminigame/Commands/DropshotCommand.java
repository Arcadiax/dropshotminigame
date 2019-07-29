package net.mysteryspace.dropshotminigame.Commands;

import net.mysteryspace.dropshotminigame.Arena.Arena;
import net.mysteryspace.dropshotminigame.Arena.ArenaManager;
import net.mysteryspace.dropshotminigame.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DropshotCommand implements CommandExecutor {
    private Main _plugin;

    public DropshotCommand(Main plugin) {
        _plugin = plugin;
        _plugin.getCommand("dropshot").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] bits) {

        Player p = (Player)sender;

        if (p == null) {
            sender.sendMessage("Only players can use this command");
            return true;
        }


        if(bits.length == 0) {
            p.sendMessage("Commands: /dropshot [creategame, invite, leave]");
            return true;
        }

        switch(bits[0]){
            case "creategame":
                CreateGame(p, bits);
                break;

            case "invite":
                Invite(p, bits);
                break;

            case "leave":
                Leave(p, bits);
                break;

            case "acceptinvite":
                AcceptInvite(p, bits);
                break;

            default:
                p.sendMessage("Command not found");
                break;
        }

        return true;
    }

    private void CreateGame(Player p, String[] bits) {
        Arena arena = ArenaManager.GetInstance().GetEmptyArena();

        if(arena == null){
            p.sendMessage("No open slots");
            return;
        }

        arena.PlayerJoin(p);
        p.sendMessage("Creating new match! Invite a friend using /invite");
    }

    private void Invite(Player p, String[] bits){
        Player other = Bukkit.getPlayer(bits[1]);

        if(bits.length < 2){
            p.sendMessage("You need to enter a player name to invite");
            return;
        }

        if(other == null){
            p.sendMessage("Player " + bits[1] + " not found");
            return;
        }

        if(other.getDisplayName().equals(p.getDisplayName())){
            p.sendMessage("You can't invite yourself to a game");
            return;
        }

        Arena arena = ArenaManager.GetInstance().GetPlayerArena(p);
        if(arena == null){
            p.sendMessage("You're not in a game, use /creategame to start a new one");
            return;
        }

        ArenaManager.GetInstance().SendInvite(p, other, arena);
        p.sendMessage("Invite sent, it will time out in 30 seconds");
    }

    private void Leave(Player p, String[] bits){
        Arena arena = ArenaManager.GetInstance().GetPlayerArena(p);

        if(arena == null){
            p.sendMessage("You're not in a game");
            return;
        }

        arena.PlayerQuit(p);
    }

    private void AcceptInvite(Player p, String[] bits){

        if(bits.length < 2){
            p.sendMessage("You can't use this command manually");
        }

        int inviteId = Integer.parseInt(bits[1]);
        if(!ArenaManager.GetInstance().AcceptInvite(inviteId)){
            p.sendMessage("Invite not found, it probably expired");
        }
    }
}
