package me.huanmeng.windowfix;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Pointer;
import org.lwjgl.system.windows.User32;
import org.lwjgl.system.windows.WindowProc;

public final class WindowMessageFix {
    private static final long SYS_COMMAND_MASK = 0xFFF0L;
    private static final int MIM_STYLE = 0x00000010;
    private static final int MNS_MODELESS = 0x40000000;

    private static final int MENUINFO_CB_SIZE = 0;
    private static final int MENUINFO_FMASK = 4;
    private static final int MENUINFO_DWSTYLE = 8;
    private static final int MENUINFO_DWCONTEXTHELPID = 16 + Pointer.POINTER_SIZE;
    private static final int MENUINFO_DWMENUDATA = align(MENUINFO_DWCONTEXTHELPID + 4, Pointer.POINTER_SIZE);
    private static final int MENUINFO_SIZEOF = MENUINFO_DWMENUDATA + Pointer.POINTER_SIZE;

    private static final long PFN_GET_SYSTEM_MENU = resolveFunctionAddress("GetSystemMenu");
    private static final long PFN_GET_MENU_INFO = resolveFunctionAddress("GetMenuInfo");
    private static final long PFN_SET_MENU_INFO = resolveFunctionAddress("SetMenuInfo");

    private static boolean installAttempted;
    private static boolean installed;
    private static boolean modelessMenuConfigured;
    private static boolean nativeSystemMenuPending;
    private static boolean nativeSystemMenuActive;
    private static long originalWindowProc;
    private static WindowProc hookProc;

    private WindowMessageFix() {
    }

    public static void tryInstall() {
        if (installed || installAttempted) {
            return;
        }
        if (!isWindows()) {
            installAttempted = true;
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getWindow() == null) {
            return;
        }
        /*? if forgeLike && >= 1.21.9 {*/
        long glfwWindow = minecraft.getWindow().handle();
        /*?} else {*/
        /*long glfwWindow = minecraft.getWindow().getWindow();
        *//*?}*/
        if (glfwWindow == 0L) {
            return;
        }

        long hwnd = GLFWNativeWin32.glfwGetWin32Window(glfwWindow);
        if (hwnd == 0L) {
            return;
        }

        try {
            hookProc = WindowProc.create((windowHandle, message, wParam, lParam) ->
                handleMessage(windowHandle, message, wParam, lParam));
            originalWindowProc = User32.SetWindowLongPtr(hwnd, User32.GWL_WNDPROC, hookProc.address());
            installed = true;
            installAttempted = true;
            Windowfix.LOGGER.info("Installed Win32 window hook for system menu freeze workaround.");
            if (!WindowfixConfig.shouldBlockTitlebarSystemMenu()) {
                configureSystemMenuModeless(hwnd);
            }
        } catch (Throwable throwable) {
            installAttempted = true;
            Windowfix.LOGGER.error("Failed to install Win32 window hook.", throwable);
        }
    }

    private static boolean isWindows() {
        return Windowfix.isWindows();
    }

    private static long handleMessage(long hwnd, int message, long wParam, long lParam) {
        boolean blockMenu = WindowfixConfig.shouldBlockTitlebarSystemMenu();
        if (!blockMenu) {
            configureSystemMenuModeless(hwnd);
        } else {
            nativeSystemMenuPending = false;
            nativeSystemMenuActive = false;
        }

        if (blockMenu
            && (message == User32.WM_NCRBUTTONDOWN || message == User32.WM_NCRBUTTONUP)
            && wParam == User32.HTCAPTION) {
            // Suppress title-bar right-click system menu when blocking native menu.
            GLFW.glfwPostEmptyEvent();
            return 0L;
        }

        long command = wParam & SYS_COMMAND_MASK;
        if (!blockMenu && message == User32.WM_SYSCOMMAND && (command == User32.SC_MOUSEMENU || command == User32.SC_KEYMENU)) {
            nativeSystemMenuPending = true;
            GLFW.glfwPostEmptyEvent();
        }

        if (blockMenu && message == User32.WM_SYSCOMMAND) {
            if (command == User32.SC_MOUSEMENU || command == User32.SC_KEYMENU) {
                GLFW.glfwPostEmptyEvent();
                return 0L;
            }
        }

        if (!blockMenu && WindowfixConfig.shouldKeepActiveDuringSystemMenu()) {
            if (message == User32.WM_ENTERMENULOOP) {
                nativeSystemMenuActive = true;
            } else if (message == User32.WM_EXITMENULOOP) {
                nativeSystemMenuPending = false;
                nativeSystemMenuActive = false;
            }

            if ((nativeSystemMenuPending || nativeSystemMenuActive) && shouldSuppressTransientFocusLoss(message, wParam)) {
                GLFW.glfwPostEmptyEvent();
                return message == User32.WM_NCACTIVATE ? 1L : 0L;
            }
        }

        long result = callOriginal(hwnd, message, wParam, lParam);

        if (message == User32.WM_ENTERMENULOOP || message == User32.WM_ENTERSIZEMOVE) {
            GLFW.glfwPostEmptyEvent();
            return result;
        }

        if (message == User32.WM_EXITMENULOOP || message == User32.WM_EXITSIZEMOVE) {
            GLFW.glfwPostEmptyEvent();
            return result;
        }

        if (message == User32.WM_SYSCOMMAND) {
            if (command == User32.SC_CLOSE || command == User32.SC_MINIMIZE || command == User32.SC_MAXIMIZE || command == User32.SC_RESTORE) {
                GLFW.glfwPostEmptyEvent();
            }
        }

        return result;
    }

    private static long callOriginal(long hwnd, int message, long wParam, long lParam) {
        if (originalWindowProc != 0L) {
            return User32.nCallWindowProc(originalWindowProc, hwnd, message, wParam, lParam);
        }
        return User32.DefWindowProc(hwnd, message, wParam, lParam);
    }

    private static void configureSystemMenuModeless(long hwnd) {
        if (modelessMenuConfigured) {
            return;
        }
        if (PFN_GET_SYSTEM_MENU == 0L || PFN_GET_MENU_INFO == 0L || PFN_SET_MENU_INFO == 0L) {
            return;
        }

        long menuHandle = JNI.callPP(hwnd, 0, PFN_GET_SYSTEM_MENU);
        if (menuHandle == 0L) {
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            long menuInfo = stack.nmalloc(Pointer.POINTER_SIZE, MENUINFO_SIZEOF);
            MemoryUtil.memSet(menuInfo, 0, MENUINFO_SIZEOF);
            MemoryUtil.memPutInt(menuInfo + MENUINFO_CB_SIZE, MENUINFO_SIZEOF);

            if (JNI.callPPI(menuHandle, menuInfo, PFN_GET_MENU_INFO) == 0) {
                Windowfix.LOGGER.warn("GetMenuInfo failed, keep default menu behavior.");
                return;
            }

            int style = MemoryUtil.memGetInt(menuInfo + MENUINFO_DWSTYLE);
            if ((style & MNS_MODELESS) != 0) {
                modelessMenuConfigured = true;
                return;
            }

            MemoryUtil.memPutInt(menuInfo + MENUINFO_FMASK, MIM_STYLE);
            MemoryUtil.memPutInt(menuInfo + MENUINFO_DWSTYLE, style | MNS_MODELESS);

            if (JNI.callPPI(menuHandle, menuInfo, PFN_SET_MENU_INFO) == 0) {
                Windowfix.LOGGER.warn("SetMenuInfo(MNS_MODELESS) failed, keep default menu behavior.");
                return;
            }

            modelessMenuConfigured = true;
            Windowfix.LOGGER.info("Enabled MNS_MODELESS on system menu.");
        } catch (Throwable throwable) {
            Windowfix.LOGGER.warn("Failed to apply MNS_MODELESS system menu mode.", throwable);
        }
    }

    private static long resolveFunctionAddress(String functionName) {
        try {
            return APIUtil.apiGetFunctionAddress(User32.getLibrary(), functionName);
        } catch (Throwable throwable) {
            Windowfix.LOGGER.warn("Failed to resolve Win32 function: {}", functionName);
            return 0L;
        }
    }

    private static int align(int value, int alignment) {
        int mask = alignment - 1;
        return (value + mask) & ~mask;
    }

    private static boolean shouldSuppressTransientFocusLoss(int message, long wParam) {
        if (message == User32.WM_ACTIVATE) {
            return lowWord(wParam) == User32.WA_INACTIVE;
        }
        if (message == User32.WM_ACTIVATEAPP || message == User32.WM_KILLFOCUS) {
            return wParam == 0L || message == User32.WM_KILLFOCUS;
        }
        if (message == User32.WM_NCACTIVATE) {
            return wParam == 0L;
        }
        return false;
    }

    private static int lowWord(long value) {
        return (int) (value & 0xFFFFL);
    }
}
