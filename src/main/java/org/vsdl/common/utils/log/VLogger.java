package org.vsdl.common.utils.log;

import static org.vsdl.common.utils.log.VLogger.Level.ALL;
import static org.vsdl.common.utils.log.VLogger.Level.NONE;

public class VLogger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public enum Level {

        NONE(ANSI_BLACK),
        FATAL(ANSI_PURPLE),
        ERROR(ANSI_RED),
        WARN(ANSI_YELLOW),
        INFO(ANSI_GREEN),
        DEBUG(ANSI_BLUE),
        TRACE(ANSI_CYAN),
        ALL(ANSI_WHITE);


        private final String consoleColor;

        Level(String consoleColor) {
            this.consoleColor = consoleColor;
        }

        public String getConsoleColor() {
            return consoleColor;
        }
    }

    public enum Mode {
        CONSOLE,
        FILE
    }

    private static Level logLevel = NONE;
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
        int ord = level.ordinal();
        if (ord == NONE.ordinal() || ord >= ALL.ordinal())
            throw new IllegalArgumentException("Log Level " + level + " may not be used for logging.");
        if (ord > logLevel.ordinal()) return;
        switch (logMode) {
            case CONSOLE:
                System.out.println(level.consoleColor + level.name() + ANSI_RESET + ": " + msg);
                break;
            case FILE:
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static void log(String msg, int ordinal) {
        log(msg, Level.values()[ordinal]);
    }
}
