package net.mysteryspace.dropshotminigame;

import org.bukkit.Material;

import java.util.HashMap;

public class Settings {
    public boolean SuppressWeather;
    public boolean SuppressTime;
    public boolean SuppressBlockDamage;
    public boolean SuppressHunger;
    public boolean SuppressDamage;
    public boolean IntegratedHub;
    public boolean SaveInventoryBetweenGames;

    private HashMap<Material, Integer> _scoreValues;

    public Settings(boolean weather, boolean time, boolean blockDamage, boolean hunger, boolean damage, boolean hub, boolean saveInventory){
        SuppressWeather = weather;
        SuppressTime = time;
        SuppressBlockDamage = blockDamage;
        SuppressHunger = hunger;
        SuppressDamage = damage;
        IntegratedHub = hub;
        SaveInventoryBetweenGames = saveInventory;
    }

    public void SetScoreValues(int white, int black, int cyan, int red, int yellow){
        _scoreValues = new HashMap<>();
        _scoreValues.put(Material.WHITE_WOOL, white);
        _scoreValues.put(Material.BLACK_WOOL, black);
        _scoreValues.put(Material.CYAN_WOOL, cyan);
        _scoreValues.put(Material.RED_WOOL, red);
        _scoreValues.put(Material.YELLOW_WOOL, yellow);
    }

    public int GetScoreValue(Material mat){
        if(_scoreValues.containsKey(mat))
            return _scoreValues.get(mat);

        return 0;
    }
}
