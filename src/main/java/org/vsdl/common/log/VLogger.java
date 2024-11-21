package org.vsdl.common.log;

public class VLogger {

    public static enum Level {
        NONE,
        FATAL,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE,
        ALL;
    }

    public static enum Mode {
        CONSOLE,
        FILE;
    }

    private static Level logLevel = Level.NONE;
    private static Mode logMode = Mode.CONSOLE;

    public static void setLogLevel(int ordinal) {
        setLogLevel(Level.values()[ordinal]);
    }

    public static void setLogLevel(Level level) {
        logLevel = level;
    }

    public static void setLogMode(Mode mode) {
        if (mode != Mode.CONSOLE) throw new IllegalArgumentException("Unsupported mode " + mode);
        logMode = mode;
    }

    public static void log(String msg, Level level) {
        log(msg, level.ordinal());
    }

    public static void log(String msg, int ordinal) {
        if (ordinal > logLevel.ordinal()) return;
        switch (logMode) {
            case CONSOLE:
                System.out.println(msg);
                break;
            case FILE:
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
