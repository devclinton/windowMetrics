package com.devclinton.WindowMetrics.tasks.WindowInfo;


import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

public class Windows implements WindowInfoInterface {
    private int ACCESS_FLAGS = 0x0439;
    private char[] char_buffer = new char[1024];
    private byte[] byte_buffer = new byte[1024];

    private WinDef.HWND activeWindow = new WinDef.HWND();


    public long getIdleTime() {
        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return (Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime) / 1000;
    }

    private boolean getActiveWindowHandle() {
        activeWindow = User32.INSTANCE.GetForegroundWindow();
        return true;
    }

    public String getActiveWindowTitle() {
        String r = null;
        if (getActiveWindowHandle()) {
            User32.INSTANCE.GetWindowText(activeWindow, char_buffer,
                    char_buffer.length);
            r = Native.toString(char_buffer);
        }
        return r;
    }

    public String getProcessName() {
        // get process name
        IntByReference dwProcessId = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(activeWindow, dwProcessId);

        Pointer zero = new Pointer(0);

        WinNT.HANDLE hProcess = Kernel32.INSTANCE.OpenProcess(ACCESS_FLAGS, false,
                dwProcessId.getValue());
        Psapi.INSTANCE.GetModuleFileNameExA(hProcess.getPointer(), zero,
                byte_buffer, byte_buffer.length);
        String r = Native.toString(byte_buffer);
        return r;
    }
}