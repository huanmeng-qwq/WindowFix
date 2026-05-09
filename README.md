# WindowFix

[English](README.md) | [简体中文](README_ZH_CN.md)

WindowFix addresses a Windows-specific Minecraft issue where interacting with the title-bar system menu (right-click title bar, minimize, restore, etc.) may cause the game loop to hang or stutter.

## Environment

- Minecraft `1.20.x ~ 26.x`
- Windows client

## Fix Modes

WindowFix provides two modes via client config:

1. Block native system menu (stability-first)  
Blocks title-bar right-click system menu entirely to avoid entering the Win32 menu modal loop.

2. Native compatibility mode  
Keeps native right-click system menu and enables `MNS_MODELESS` to reduce blocking likelihood.

## Configuration

Config file is generated after first launch:

`config/windowfix-client.toml`

Example:

```toml
blockTitleBarSystemMenu = false
keepActiveDuringSystemMenu = true
```

Restart the game after changing config values.

- `blockTitleBarSystemMenu`
  - `true`: Disable title-bar right-click native system menu.
  - `false`: Keep native system menu behavior.
- `keepActiveDuringSystemMenu`
  - Only effective when `blockTitleBarSystemMenu = false`.
  - `true`: Suppress transient focus-loss messages during system menu operations to reduce brief hitching.
  - `false`: Keep default focus behavior.

## Build

Windows:

```powershell
.\gradlew.bat build
```

Output:

`versions/xxx/build/libs/`

## Usage

1. Put the built JAR into your client `mods` directory.
2. Launch the game and adjust `config/windowfix-client.toml` as needed.

## Log Keywords

Search these in `latest.log`:

- `Installed Win32 window hook for system menu freeze workaround.`
- `Enabled MNS_MODELESS on system menu.`

## Known Limitations

- Windows-only behavior.
- In native compatibility mode, brief hitching may still happen depending on system window management behavior.
