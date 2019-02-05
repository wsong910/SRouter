package chuxin.shimo.shimowendang.smrouter.compiler;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class Logger {
    private static final String PREFIX_OF_LOGGER = "ShimoRoute::Compiler";
    private Messager mMsg;

    public Logger(Messager messager) {
        mMsg = messager;
    }

    /**
     * Print info log.
     */
    public void info(CharSequence info) {
        if (isNotEmpty(info)) {
            mMsg.printMessage(Diagnostic.Kind.NOTE, PREFIX_OF_LOGGER + info);
        }
    }

    private boolean isNotEmpty(CharSequence info) {
        return info != null && !info.equals("");
    }

    public void error(CharSequence error) {
        if (isNotEmpty(error)) {
            mMsg.printMessage(Diagnostic.Kind.ERROR,
                PREFIX_OF_LOGGER + "An exception is encountered, [" + error + "]");
        }
    }

    public void error(Throwable error) {
        if (null != error) {
            mMsg.printMessage(Diagnostic.Kind.ERROR,
                PREFIX_OF_LOGGER + "An exception is encountered, [" + error.getMessage() + "]" + "\n" +
                    formatStackTrace(error.getStackTrace()));
        }
    }

    public void warning(CharSequence warning) {
        if (isNotEmpty(warning)) {
            mMsg.printMessage(Diagnostic.Kind.WARNING, PREFIX_OF_LOGGER + warning);
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
