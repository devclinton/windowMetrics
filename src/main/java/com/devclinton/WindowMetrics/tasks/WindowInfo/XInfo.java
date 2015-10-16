package com.devclinton.WindowMetrics.tasks.WindowInfo;

import com.codahale.metrics.annotation.Timed;
import com.sun.jna.*;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class XInfo implements WindowInfoInterface {
    private static final X11 x11 = X11.INSTANCE;
    private static final Pattern JVMs = Pattern.compile("(.*?)/?(java|python|perl|ruby|bash|sh|ksh)", Pattern.CASE_INSENSITIVE);
    private static Logger m_logger = Logger.getLogger(XInfo.class.getName());
    X11.Display display;

    @Timed
    public long getIdleTime() {
        X11.Window win = null;
        Xss.XScreenSaverInfo info = null;
        final X11 x11 = X11.INSTANCE;
        final Xss xss = Xss.INSTANCE;

        long idlemillis = 0L;
        try {
            display = x11.XOpenDisplay(null);
            win = x11.XDefaultRootWindow(display);
            info = xss.XScreenSaverAllocInfo();
            xss.XScreenSaverQueryInfo(display, win, info);

            idlemillis = info.idle.longValue();
        } finally {
            if (info != null)
                x11.XFree(info.getPointer());
            info = null;

            if (display != null)
                x11.XCloseDisplay(display);
            display = null;
        }
        return idlemillis / 1000;
    }

    @Timed
    public String getActiveWindowTitle() {
        String r = "";

        try {
            X11.Window active = getActiveWindow(false);
            X11.XTextProperty property = new X11.XTextProperty();
            x11.XGetWMName(display, active, property);

            r = property.value;
        } finally {
            if (display != null) {
                x11.XCloseDisplay(display);
                display = null;
            }
        }

        return r;
    }


    private X11.Window getActiveWindow(Boolean cleanupDisplay) {
        PointerByReference ret_prop_ref = null;
        X11.Window active = null;
        try {
            display = x11.XOpenDisplay(null);
            X11.Window root = x11.XDefaultRootWindow(display);
            X11.Atom xa_prop_type = X11.XA_WINDOW;
            X11.Atom xa_prop_name = x11.XInternAtom(display, "_NET_ACTIVE_WINDOW", false);

            NativeLong long_offset = new NativeLong(0);
            NativeLong long_length = new NativeLong(4096 / 4);

            X11.AtomByReference xa_ret_type_ref = new X11.AtomByReference();
            IntByReference ret_format_ref = new IntByReference();
            NativeLongByReference ret_nitems_ref = new NativeLongByReference();
            NativeLongByReference ret_bytes_after_ref = new NativeLongByReference();
            ret_prop_ref = new PointerByReference();

            if (x11.XGetWindowProperty(display, root, xa_prop_name, long_offset, long_length, false,
                    xa_prop_type, xa_ret_type_ref, ret_format_ref,
                    ret_nitems_ref, ret_bytes_after_ref, ret_prop_ref) == X11.Success) {
                Pointer ret_prop = ret_prop_ref.getValue();
                active = new X11.Window(ret_prop.getLong(0));
            }
        } finally {
            if (ret_prop_ref.getPointer() == null) {
                x11.XFree(ret_prop_ref.getPointer());
            }

            if (display != null && cleanupDisplay) {
                x11.XCloseDisplay(display);
                display = null;
            }
        }
        return active;
    }

    private String getScript(String filePath) {

        String ret = "";
        try {
            ret = new String(Files.readAllBytes(Paths.get(filePath)));
            ret = ret.substring(0, ret.indexOf("\0"));
        } catch (IOException e) {
            m_logger.severe("Issues Reading: " + filePath);
        }
        return ret;
    }

    @Timed
    public String getProcessName() {
        String r = "";
        PointerByReference ret_prop_ref = null;
        try {
            X11.Window activeWindow = getActiveWindow(false);

            X11.Atom xa_prop_name = x11.XInternAtom(display, "_NET_WM_PID", false);
            X11.Atom xa_prop_type = X11.XA_CARDINAL;

            NativeLong long_offset = new NativeLong(0);
            NativeLong long_length = new NativeLong(4096 / 4);

            X11.AtomByReference xa_ret_type_ref = new X11.AtomByReference();
            IntByReference ret_format_ref = new IntByReference();
            NativeLongByReference ret_nitems_ref = new NativeLongByReference();
            NativeLongByReference ret_bytes_after_ref = new NativeLongByReference();

            ret_prop_ref = new PointerByReference();

            if (x11.XGetWindowProperty(display, activeWindow, xa_prop_name, long_offset, long_length, false,
                    xa_prop_type, xa_ret_type_ref, ret_format_ref,
                    ret_nitems_ref, ret_bytes_after_ref, ret_prop_ref) == X11.Success) {

                int pid = ret_prop_ref.getValue().getInt(0);


                try {
                    r = Files.readSymbolicLink(new File("/proc/" + pid + "/exe").toPath()).toAbsolutePath().toString();
                    /*if (JVMs.matcher(r).matches()) {
                        if (m_logger.isLoggable(Level.FINE)) {
                            m_logger.fine("The executable is a JVM/script engine, so we are now looking for the script.");
                        }
                       r = getScript("/proc/" + pid + "/cmdline");
                    }*/
                } catch (IOException e) {
                    m_logger.severe("Issues Locating executable: /proc/" + pid + "/exe");
                }
            }

        } finally {
            if (ret_prop_ref != null)
                x11.XFree(ret_prop_ref.getPointer());

            if (display != null)
                x11.XCloseDisplay(display);
            display = null;
        }

        return r;
    }

    /**
     * Definition (incomplete) of the Xext library.
     */
    interface Xss extends Library {
        Xss INSTANCE = (Xss) Native.loadLibrary("Xss", Xss.class);

        XScreenSaverInfo XScreenSaverAllocInfo();

        int XScreenSaverQueryInfo(X11.Display dpy, X11.Drawable drawable,
                                  XScreenSaverInfo saver_info);

        public class XScreenSaverInfo extends Structure {
            public X11.Window window; /* screen saver window */
            public int state; /* ScreenSaver{Off,On,Disabled} */
            public int kind; /* ScreenSaver{Blanked,Internal,External} */
            public NativeLong til_or_since; /* milliseconds */
            public NativeLong idle; /* milliseconds */
            public NativeLong event_mask; /* events */

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[]{"window", "state", "kind", "til_or_since",
                        "idle", "event_mask"});
            }
        }
    }
}