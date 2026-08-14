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
    private static boolean modelessMenuConfigurationAttempted;
    private static boolean callbackFailureLogged;
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
        /*? if (forgeLike && >= 1.21.9) || >= 26.1 {*/
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
            hookProc = WindowProc.create(WindowMessageFix::dispatchMessage);
            //? if >= 26.1 {
            originalWindowProc = User32.SetWindowLongPtr(null, hwnd, User32.GWL_WNDPROC, hookProc.address());
            //? } else
            //originalWindowProc = User32.SetWindowLongPtr(hwnd, User32.GWL_WNDPROC, hookProc.address());
            if (originalWindowProc == 0L) {
                hookProc.free();
                hookProc = null;
                throw new IllegalStateException("SetWindowLongPtr returned a null previous window procedure.");
            }
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

    private static long dispatchMessage(long hwnd, int message, long wParam, long lParam) {
        try {
            return handleMessage(hwnd, message, wParam, lParam);
        } catch (Throwable throwable) {
            if (!callbackFailureLogged) {
                callbackFailureLogged = true;
                try {
                    Windowfix.LOGGER.error("Unhandled error in Win32 window hook; forwarding to the original procedure.", throwable);
                } catch (Throwable ignored) {
                }
            }
            return callOriginal(hwnd, message, wParam, lParam);
        }
    }

    private static long handleMessage(long hwnd, int message, long wParam, long lParam) {
        boolean blockMenu = WindowfixConfig.shouldBlockTitlebarSystemMenu();
        if (!blockMenu) {
            configureSystemMenuModeless(hwnd);
        }

        if (blockMenu
            && (message == User32.WM_NCRBUTTONDOWN || message == User32.WM_NCRBUTTONUP)
            && wParam == User32.HTCAPTION) {
            // Suppress title-bar right-click system menu when blocking native menu.
            postEmptyEvent();
            return 0L;
        }

        long command = wParam & SYS_COMMAND_MASK;
        if (blockMenu && message == User32.WM_SYSCOMMAND) {
            if (command == User32.SC_MOUSEMENU || command == User32.SC_KEYMENU) {
                postEmptyEvent();
                return 0L;
            }
        }

        long result = callOriginal(hwnd, message, wParam, lParam);

        if (message == User32.WM_NCDESTROY) {
            resetHookState();
            return result;
        }

        if (message == User32.WM_ENTERMENULOOP || message == User32.WM_ENTERSIZEMOVE) {
            postEmptyEvent();
            return result;
        }

        if (message == User32.WM_EXITMENULOOP || message == User32.WM_EXITSIZEMOVE) {
            postEmptyEvent();
            return result;
        }

        if (message == User32.WM_SYSCOMMAND) {
            if (command == User32.SC_CLOSE || command == User32.SC_MINIMIZE || command == User32.SC_MAXIMIZE || command == User32.SC_RESTORE) {
                postEmptyEvent();
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
        if (modelessMenuConfigurationAttempted) {
            return;
        }
        modelessMenuConfigurationAttempted = true;

        if (PFN_GET_SYSTEM_MENU == 0L || PFN_GET_MENU_INFO == 0L || PFN_SET_MENU_INFO == 0L) {
            Windowfix.LOGGER.warn("Required Win32 system-menu functions are unavailable; keeping default menu behavior.");
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
                return;
            }

            MemoryUtil.memPutInt(menuInfo + MENUINFO_FMASK, MIM_STYLE);
            MemoryUtil.memPutInt(menuInfo + MENUINFO_DWSTYLE, style | MNS_MODELESS);

            if (JNI.callPPI(menuHandle, menuInfo, PFN_SET_MENU_INFO) == 0) {
                Windowfix.LOGGER.warn("SetMenuInfo(MNS_MODELESS) failed, keep default menu behavior.");
                return;
            }

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

    private static void postEmptyEvent() {
        try {
            GLFW.glfwPostEmptyEvent();
        } catch (Throwable throwable) {
            if (!callbackFailureLogged) {
                callbackFailureLogged = true;
                try {
                    Windowfix.LOGGER.error("Failed to wake the GLFW event loop from the Win32 window hook.", throwable);
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private static void resetHookState() {
        installed = false;
        installAttempted = false;
        modelessMenuConfigurationAttempted = false;
        callbackFailureLogged = false;
        originalWindowProc = 0L;
    }
}
