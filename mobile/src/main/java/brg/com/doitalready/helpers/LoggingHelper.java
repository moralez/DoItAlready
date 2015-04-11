package brg.com.doitalready.helpers;

import android.util.Log;

/**
 * Created by jmo on 4/8/2015.
 */
public class LoggingHelper {

    private enum LogLevel {
        Info,
        Debug,
        Warning,
        Error
    }

    public static void logDebug(String logStatement) {
        LoggingHelper.log(LogLevel.Debug, logStatement);
    }

    public static void log(LogLevel level, String logStatement) {
        if (LoggingHelper.getDebuggingEnabled()) {
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement e = stacktrace[4];
            StringBuilder sb = new StringBuilder();
            sb.append(e.getMethodName());
            sb.append("():");
            sb.append(e.getLineNumber());
            sb.append(" ");
            sb.append(logStatement);
            switch (level) {
                case Info:
                    Log.i("JMO", sb.toString());
                    break;
                case Debug:
                    Log.d("JMO", sb.toString());
                    break;
                case Warning:
                    Log.w("JMO", sb.toString());
                    break;
                case Error:
                    Log.e("JMO", sb.toString());
                    break;
                default:
                    break;
            }
        }
    }

    public static void logFunctionEnter(Object ... args) {
        if (LoggingHelper.getDebuggingEnabled()) {
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement e = stacktrace[4];
            StringBuilder sb = new StringBuilder();
            sb.append(e.getMethodName());
            sb.append("(): Entered");
            if (args.length > 0) {
                sb.append(" - ");
                for (Object arg : args) {
                    sb.append(arg.toString());
                    sb.append(" ");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            Log.i("JMO", sb.toString());
        }
    }

    public static boolean getDebuggingEnabled() {
        return true;
    }
}
