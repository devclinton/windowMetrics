package com.devclinton.WindowMetrics.tasks.WindowInfo;

/**
 * Created by Clinton Collins <clinton.collins@cgi.com> on 10/16/15.
 */
public class ProcessInfo {
    private String arguments;
    private String executable;
    private boolean fromCache = false;

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public boolean isFromCache() {
        return fromCache;
    }

    public void setFromCache(@SuppressWarnings("SameParameterValue") boolean fromCache) {
        this.fromCache = fromCache;
    }

    @Override
    public boolean equals(Object other) {

        if (other.getClass() == ProcessInfo.class) {
            ProcessInfo o = (ProcessInfo) other;
            return executable.equals(o.executable) && ((arguments == null && o.arguments == null) || arguments.equals(o.arguments));
        }

        return false;
    }
}
