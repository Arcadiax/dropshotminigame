# Dropshot - Minigame
A Spigot Minecraft Minigame

## Description
This plugin contains everything required to setup a minigame in which players compete by shooting targets while falling. You can setup multiple arenas, configure specific spawn locations and settings, and the whole thing can be run self-contained on a server, or as part of a larger server, with lots of settings to configure how players interact with the plugin.

## Features
Built in Arena & Lobby system
Settings to limit player interactions with blocks and entities, stop weather and time, and use an integrated hub
Completely configure each arena, and make as many arenas as you want
Dropshot Minigame that pits 2 players against each other, shooting targets at terminal velocity

## Getting Started
### Standalone/New Server
Simply drop the plugin in a new bukkit or spigot server
Download and drop the official map in the server (here: https://www.curseforge.com/minecraft/worlds/dropshot-minigame-map)
Use the commands to play!
### Integrate Into Existing Server
Drop the plugin in your server
Run the server once to generate the config file (plugins/dropshotminigame/config.yml)
Set the "features" settings by checking the reference below
Configure your arenas by checking the reference below
Reload the server

## Config Reference
### Features
suppressWeather - when true, sets gamerule DoWeatherCycle to false and stops all weather events
suppressTime - when true, sets gamerule DoDaylightCycle to false
suppressHunger - when true, will stop players hunger draining
suppressDamage - when true, will stop players taking any damage in or out of a game
integratedHub - when true, will teleport players back to the hub spawn location in the config (otherwise, will tp to last location)
saveInventoryBetweenGames - when true, will save player inventory before entering a game, and give it back when leaving
suppressBlockDamage - when true, will stop players destroying blocks in or out of a game
### Settings
startGameCountdown - the seconds to wait before starting a game once a lobby has 2 players
gameLength - the length in seconds each game lasts
inviteLength - the length in seconds a game invite remains active for (expires after this)
### Arenas
arenaCount - the number of arenas you define below
Note: arena names should always be "arena" followed by an incrementing number, e.g. arena0, arena1, arena2 etc.
#### Arena
waitSpawn - The location a player is teleported to while waiting for a game to start
p1Spawn - The location the first player is teleported to when the game starts
p2Spawn - The location the second player is teleported to when the game starts
holeExitHeight - The y coordinate the player must be below before they can fire while in-game

### Score Values
The game is built to use colored wool as targets to earn points
The typical target will have rings of color from outside to inside in this order -> white, black, cyan, red, yellow
You can configure how many points shooting each wool color should grant in this section
Only the included default colors will be used, you can't add extra

## Commands
All commands start with /dropshot
/dropshot creategame - create a new game if an arena is available
/dropshot invite <username> - invite a player to your game
/dropshot leave - leave the game you are currently in
