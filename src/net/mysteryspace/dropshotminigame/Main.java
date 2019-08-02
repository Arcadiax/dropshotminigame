package net.mysteryspace.dropshotminigame;
import net.mysteryspace.dropshotminigame.Arena.ArenaManager;
import net.mysteryspace.dropshotminigame.Commands.*;
import net.mysteryspace.dropshotminigame.Util.BaseGameSuppression;
import net.mysteryspace.dropshotminigame.Util.Timers;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private Settings _settings;
    public Settings GetSettings(){return _settings; }

    @Override
    public void onEnable(){

        //Config
        saveDefaultConfig();
        LoadSettings();

        //Setup
        new ArenaManager(this);
        new Timers(this);

        //Commands
        new DropshotCommand(this);
        getCommand("dropshot").setTabCompleter(new TabCompleter(this));

        //Listeners
        new BaseGameSuppression(this);

        //World Setup
        World world = Bukkit.getWorlds().get(0);
        if(_settings.SuppressTime)
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

        if(_settings.SuppressWeather) {
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setTime(0);
        }

    }

    private void LoadSettings(){
        //Settings
        ConfigurationSection featuresConfig = getConfig().getConfigurationSection("features");
        _settings = new Settings(
                featuresConfig.getBoolean("suppressWeather", false),
                featuresConfig.getBoolean("suppressTime", false),
                featuresConfig.getBoolean("suppressBlockDamage", false),
                featuresConfig.getBoolean("suppressHunger", false),
                featuresConfig.getBoolean("suppressDamage", false),
                featuresConfig.getBoolean("integratedHub", false),
                featuresConfig.getBoolean("saveInventoryBetweenGames", false)
        );

        //Scores
        ConfigurationSection scoreConfig = getConfig().getConfigurationSection("scoreValues");
        _settings.SetScoreValues(
                scoreConfig.getInt("WHITE_WOOL", 1),
                scoreConfig.getInt("BLACK_WOOL", 2),
                scoreConfig.getInt("CYAN_WOOL", 3),
                scoreConfig.getInt("RED_WOOL", 4),
                scoreConfig.getInt("YELLOW_WOOL", 5)
        );
    }

    @Override
    public void onDisable(){

    }

    public void TellConsole(String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }
}
