package net.mysteryspace.dropshotminigame.Commands;

import net.mysteryspace.dropshotminigame.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    Main _plugin;

    public TabCompleter(Main plugin){
        _plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if(!(sender instanceof Player))
            return null;

        if(cmd.getName().equalsIgnoreCase("dropshot")){
            List<String> commands = new ArrayList<>();
            if(args.length == 1) {
                commands.add("creategame");
                commands.add("invite");
                commands.add("leave");
                return commands;
            }
        }

        return null;
    }
}
