package net.mysteryspace.dropshotminigame;

public class Settings {
    public boolean SuppressWeather;
    public boolean SuppressTime;
    public boolean SuppressBlockDamage;
    public boolean SuppressHunger;
    public boolean SuppressDamage;
    public boolean IntegratedHub;

    public Settings(boolean weather, boolean time, boolean blockDamage, boolean hunger, boolean damage, boolean hub){
        SuppressWeather = weather;
        SuppressTime = time;
        SuppressBlockDamage = blockDamage;
        SuppressHunger = hunger;
        SuppressDamage = damage;
        IntegratedHub = hub;
    }
}
