# Dialogs
Plugin for [PaperMC/Paper](https://github.com/PaperMC/Paper) servers granting you ability to create complex dialogs and display them to your players. Check 

### Default Configuration Showcase
https://github.com/Grabsky/Dialogs/assets/44530932/4c975a4d-fe33-4c8c-85ac-f3a6c80160f0

<br />

## Requirements
Requires **Java 17** (or higher) and **Paper 1.20.1 #161** (or higher).

<br />

## Future
List of things that **may** be implemented in the future:
- [ ] Refactor `ConsoleCommandElement` to `CommandElement` with support for console and player commands in mind. This may involve some breaking configuration changes.
- [ ] Replace `dialogs.json` file with `plugins/Dialogs/dialogs/` directory which can contain multiple files defining dialogs. This allows to form groups/categories of dialogs which will drastically improve readability/edit times for servers with lots of them. This may involve some breaking configuration changes.
- [ ] More elements, including but not limited to titles, subtitles and perhaps toast pop-ups.

<br />

## Building (Linux)
```shell
# Cloning repository
$ git clone https://github.com/Grabsky/Dialogs.git
# Entering cloned repository
$ cd ./Dialogs
# Compiling
$ ./gradlew clean aseemble
```

<br />

## Contributing
This project is open for contributions. Help in regards of improving performance, adding new features or fixing bugs is greatly appreciated.
