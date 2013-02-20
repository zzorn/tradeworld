package org.tradeworld.systems.networking.messages;

/**
 * Indicates some error condition.
 */
public class ErrorMessage extends MessageBase {
    protected String errorType;
    protected String errorMessage;

    /**
     * @param errorType the type of the error.
     */
    public ErrorMessage(String errorType) {
        this(errorType, null);
    }

    /**
     * @param errorType the type of the error.
     * @param errorMessage details about the error.
     */
    public ErrorMessage(String errorType, String errorMessage) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "Error: " + errorType + (errorMessage == null ? "" : ": " + errorMessage);
    }
}
