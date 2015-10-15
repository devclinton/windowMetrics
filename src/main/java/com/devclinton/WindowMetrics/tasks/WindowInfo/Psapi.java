package com.devclinton.WindowMetrics.tasks.WindowInfo;

/**
 * Created by Clinton Collins <clinton.collins@gmail.com> on 10/15/15.
 */

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public interface Psapi extends StdCallLibrary {
    Psapi INSTANCE = (Psapi) Native.loadLibrary("Psapi", Psapi.class);

    /*
     * http://msdn.microsoft.com/en-us/library/ms683198(VS.85).aspx
	 */
    int GetModuleFileNameExA(Pointer pointer, Pointer hModule, byte[] buffer2,
                             int nSize);
}
