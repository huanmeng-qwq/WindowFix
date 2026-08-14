# WindowFix

[English](README.md) | [简体中文](README_ZH_CN.md)

用于修复 Minecraft 在 Windows 下，操作窗口标题栏系统菜单（右键标题栏、最小化、恢复等）后可能出现的主循环挂起/卡住问题。

## 适用环境

- Minecraft `1.20.x ~ 26.x`
- Windows 客户端

## 配置

启动一次游戏后会生成配置文件：

`config/windowfix-client.toml`

示例：

```toml
blockTitleBarSystemMenu = false
```

切换配置后建议重启游戏生效。

- `blockTitleBarSystemMenu`
  - `true`：阻止标题栏右键系统菜单，稳定性优先。
  - `false`：保留系统菜单，并启用原生 `MNS_MODELESS` 兼容处理。

旧版的 `keepActiveDuringSystemMenu` 配置已移除。窗口焦点消息现在始终交给 GLFW/Minecraft 正常处理，避免输入状态与实际窗口焦点不一致。

## 构建

Windows:

```powershell
.\gradlew.bat build
```

产物路径：

`versions/xxx/build/libs/`

## 使用

1. 构建后将产物 JAR 放入客户端 `mods` 目录。
2. 启动游戏，按需调整 `config/windowfix-client.toml`。

## 日志关键字

可在 `latest.log` 中搜索以下关键字确认行为：

- `Installed Win32 window hook for system menu freeze workaround.`
- `Enabled MNS_MODELESS on system menu.`

## 已知限制

- 仅对 Windows 生效。
- 原生兼容模式受系统环境和窗口管理行为影响，如仍出现卡住，建议切回 `blockTitleBarSystemMenu = true`。
