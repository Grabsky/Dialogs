<div align="center">

![Logo](https://github.com/Grabsky/Dialogs/blob/main/assets/logo.png)

[![GitHub Release](https://img.shields.io/github/v/release/Grabsky/Dialogs?logo=github&labelColor=%2324292F&color=%23454F5A)](https://github.com/Grabsky/Dialogs/releases/latest)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/dialogs?logo=modrinth&logoColor=white&label=downloads&labelColor=%23139549&color=%2318c25f)](https://modrinth.com/plugin/dialogs)
[![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/Grabsky/Dialogs?logo=codefactor&logoColor=white&label=%20)](https://www.codefactor.io/repository/github/grabsky/dialogs/issues/main)

[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/plugin/dialogs)

<br />

Plugin that grants you ability to create complex dialogs *(chains of elements)* and display them to your players.

</div>

<br />

## Requirements
Requires **Java 17** (or higher) and **Paper 1.20.1 #161** (or higher).

<br />

## Features
Dialogs allows you to do the following:
- Create animated NPC-like dialogs.
- Create complex chains of delayed actions.
- Choose from wide selection of available actions and customize them as you like.
  - `chat_message` - Sends a chat message to specified audience.
  - `actionbar_animation` - Sends a typing animation of specified text to the target.
  - `console_command` - Makes console execute a command.
  - `player_command` - Makes target player execute a command.
  - `pause` - Waits for desired amount of ticks, before proceeding to the next action.
  - ...more to come in the future!

Every element type except for `actionbar_animation` have built-in support for **[PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)**.

More detailed guide can be found in the [`plugins/Dialogs/dialogs/default.json`](https://github.com/Grabsky/Dialogs/blob/main/src/main/resources/example.json) file that is created the first time you start the plugin.

Recommended to be used along with **[FancyNPCs](https://github.com/FancyMcPlugins/FancyNpcs)**, **[LuaLink](https://github.com/LuaLink/LuaLink)**, **[Skript](https://github.com/SkriptLang/Skript)**, **[WorldGuardExtraFlags](https://github.com/aromaa/WorldGuardExtraFlags)** or other plugins that allow for **execution of console commands** upon certain action.

<br />

## Default Configuration Showcase
https://github.com/Grabsky/Dialogs/assets/44530932/4c975a4d-fe33-4c8c-85ac-f3a6c80160f0

<br />

## Building
Some dependencies use **[GitHub Gradle Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry)** and thus may require extra configuration steps for the project to build properly.

```shell
# Cloning repository.
$ git clone https://github.com/Grabsky/Dialogs.git
# Entering cloned repository.
$ cd Dialogs
# Compiling and building artifacts.
$ gradlew clean build
```

<br />

## Contributing
This project is open for contributions. Help in regards of improving performance, adding new features or fixing bugs is greatly appreciated.
