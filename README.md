# WindowFix

用于修复 Minecraft 在 Windows 下，操作窗口标题栏系统菜单（右键标题栏、最小化、恢复等）后可能出现的主循环挂起/卡住问题。

## 适用环境

- Minecraft `1.20.1`
- Forge `47.4.20`
- Windows 客户端

## 修复模式

本项目提供两种模式，通过客户端配置切换：

1. 阻止原生系统菜单（稳定优先）
阻止标题栏右键系统菜单弹出，从根源避免进入 Win32 菜单模态循环。

2. 原生兼容模式
保留标题栏右键菜单，同时对系统菜单启用原生 `MNS_MODELESS`，降低菜单打开时主循环被阻塞的概率。

## 配置

启动一次游戏后会生成配置文件：

`config/windowfix-client.toml`

示例：

```toml
blockTitleBarSystemMenu = false
keepActiveDuringSystemMenu = true
```

切换配置后建议重启游戏生效。

- `blockTitleBarSystemMenu`
  - `true`：禁用标题栏右键系统菜单。
  - `false`：启用原生兼容模式，保留右键菜单。
- `keepActiveDuringSystemMenu`
  - 仅在 `blockTitleBarSystemMenu = false` 时生效。
  - `true`：抑制系统菜单期间的瞬时失焦消息，减少“卡一下”。
  - `false`：保留系统默认失焦行为。

## 构建

Windows:

```powershell
.\gradlew.bat build
```

产物路径：

`build/libs/`

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
