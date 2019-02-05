package chuxin.shimo.shimowendang.smrouter.utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class Util {
    /** Returns an immutable copy of {@code list}. */
    public static <T> List<T> immutableList(List<T> list) {
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }
}
