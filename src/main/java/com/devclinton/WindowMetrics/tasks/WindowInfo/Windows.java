package com.devclinton.WindowMetrics.tasks.WindowInfo;


import com.codahale.metrics.annotation.Timed;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

public class Windows extends CachingInfoInterface implements WindowInfoInterface {
    private int ACCESS_FLAGS = 0x0439;
    private char[] char_buffer = new char[1024];
    private byte[] byte_buffer = new byte[1024];

    @Timed
    public long getIdleTime() {
        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return (Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime) / 1000;
    }

    @Timed
    private WinDef.HWND getActiveWindowHandle() {
        return User32.INSTANCE.GetForegroundWindow();
    }

    @Timed
    public String getActiveWindowTitle() {
        String r;
        User32.INSTANCE.GetWindowText(getActiveWindowHandle(), char_buffer,
                char_buffer.length);
        r = Native.toString(char_buffer);
        return r;
    }

    @Timed
    public ProcessInfo getProcessName() {
        ProcessInfo r;
        // get process name
        IntByReference dwProcessId = new IntByReference();
        WinDef.HWND active = getActiveWindowHandle();
        r = cache.getIfPresent(active.toString());
        if (r == null) {
            r = new ProcessInfo();
            User32.INSTANCE.GetWindowThreadProcessId(getActiveWindowHandle(), dwProcessId);

            Pointer zero = new Pointer(0);

            WinNT.HANDLE hProcess = Kernel32.INSTANCE.OpenProcess(ACCESS_FLAGS, false,
                    dwProcessId.getValue());
            Psapi.INSTANCE.GetModuleFileNameExA(hProcess.getPointer(), zero,
                    byte_buffer, byte_buffer.length);
            r.setExecutable(Native.toString(byte_buffer));
        }
        return r;
    }

}