package net.mysteryspace.dropshotminigame;
import net.mysteryspace.dropshotminigame.Arena.ArenaManager;
import net.mysteryspace.dropshotminigame.Commands.AcceptInviteCommand;
import net.mysteryspace.dropshotminigame.Commands.CreateGameCommand;
import net.mysteryspace.dropshotminigame.Commands.InviteCommand;
import net.mysteryspace.dropshotminigame.Util.BaseGameSuppression;
import net.mysteryspace.dropshotminigame.Util.Timers;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private Main _instance;

    public Main GetInstance(){
        return _instance;
    }

    @Override
    public void onEnable(){

        _instance = this;

        saveDefaultConfig();

        //Setup
        new ArenaManager(this);
        new Timers(this);

        //Commands
        new CreateGameCommand(this);
        new InviteCommand(this);
        new AcceptInviteCommand(this);

        //Listeners
        new BaseGameSuppression(this);

        //World Setup
        World world = Bukkit.getWorlds().get(0);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setTime(0);

    }

    @Override
    public void onDisable(){

    }

    public void TellConsole(String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }
}
