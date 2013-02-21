package org.tradeworld;

/**
 * Indicates server or client startup can not be completed.
 */
public class StartupError extends Error {

    public StartupError(String message) {
        super(message);
    }

    public StartupError(String message, Throwable cause) {
        super(message, cause);
    }
}
