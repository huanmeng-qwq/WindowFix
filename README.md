# WindowFix

[English](README.md) | [简体中文](README_ZH_CN.md)

WindowFix addresses a Windows-specific Minecraft issue where interacting with the title-bar system menu (right-click title bar, minimize, restore, etc.) may cause the game loop to hang or stutter.

## Environment

- Minecraft `1.20.x ~ 26.x`
- Windows client

## Configuration

Config file is generated after first launch:

`config/windowfix-client.toml`

Example:

```toml
blockTitleBarSystemMenu = false
```

Restart the game after changing config values.

- `blockTitleBarSystemMenu`
  - `true`: Block the title-bar system menu for maximum stability.
  - `false`: Keep the system menu and enable native `MNS_MODELESS` compatibility handling.

The legacy `keepActiveDuringSystemMenu` option has been removed. Focus messages are now always forwarded to GLFW/Minecraft so their input state stays consistent with the actual window focus.

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
