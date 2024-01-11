# Dialogs
<span>
    <a href=""><img alt="Build Status" src="https://img.shields.io/github/actions/workflow/status/Grabsky/Dialogs/gradle.yml?style=for-the-badge&logo=github&logoColor=white&label=%20"></a>
    <a href=""><img alt="CodeFactor Grade" src="https://img.shields.io/codefactor/grade/github/Grabsky/Dialogs/main?style=for-the-badge&logo=codefactor&logoColor=white&label=%20"></a>
</span>
<p></p>

Plugin for [PaperMC/Paper](https://github.com/PaperMC/Paper) servers granting you ability to create complex dialogs and display them to your players.

<br />

#### Default Configuration Showcase
https://github.com/Grabsky/Dialogs/assets/44530932/4c975a4d-fe33-4c8c-85ac-f3a6c80160f0

<br />

## Requirements
Requires **Java 17** (or higher) and **Paper 1.20.1 #161** (or higher).

<br />

## Future
List of things that **may** be implemented in the future:
- [x] Replace `dialogs.json` file with `plugins/Dialogs/dialogs/` directory which can contain multiple files defining dialogs. This allows to form groups/categories of dialogs which will drastically improve readability/edit times for servers with lots of them. This may involve some breaking configuration changes.
- [x] Refactor `ConsoleCommandElement` to `CommandElement` with support for console and player commands in mind. This may involve some breaking configuration changes.
- [ ] More elements, including but not limited to: bossbar, titles, subtitles and perhaps toast pop-ups.
- [ ] Configurable typing sound and a way to disable it.

<br />

## Known Issues
List of known issues that will be fixed in the upcoming plugin release(s):
- [x] ~~Multiple dialogs can be shown to player at once, causing them to overlap.~~ (**[1.20.1 #9](https://github.com/Grabsky/Dialogs/releases/tag/1.20.1-9)**)

<br />

## Building (Linux)
Some dependencies use [GitHub Gradle Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry) and thus may require extra configuration steps for the project to build properly.

```shell
# Cloning repository.
$ git clone https://github.com/Grabsky/Dialogs.git
# Entering cloned repository.
$ cd ./Dialogs
# Compiling and building artifacts.
$ ./gradlew clean build
```

<br />

## Contributing
This project is open for contributions. Help in regards of improving performance, adding new features or fixing bugs is greatly appreciated.
