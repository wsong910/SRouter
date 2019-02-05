package chuxin.shimo.shimowendang.smrouter.thread;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import chuxin.shimo.shimowendang.smrouter.thread.RealCall.AsyncCall;

public class Dispatcher {
    //    Thread args
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int INIT_THREAD_COUNT = CPU_COUNT + 1;
    private static final int MAX_THREAD_COUNT = INIT_THREAD_COUNT;
    private static final long SURPLUS_THREAD_LIFE = 30L;

    private Runnable mIdleCallback;

    /** Executes calls. Created lazily. */
    private ExecutorService mExecutorService;

    /** Ready async calls in the order they'll be run. */
    private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque<>();

    /** Running asynchronous calls. Includes canceled calls that haven't finished yet. */
    private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>();

    /** Running synchronous calls. Includes canceled calls that haven't finished yet. */
    private final Deque<RealCall> runningSyncCalls = new ArrayDeque<>();
    /**
     * 限制在同一个路由表中只，目标类只允许出现一次
     */
    private int mMaxRequestsPerHost = 1;
    /**
     * 限制一个路由中的路由数量
     */
    private int mMaxRequests = 5;

    public Dispatcher(ExecutorService executorService) {
        this.mExecutorService = executorService;
    }

    public Dispatcher() {
    }

    public synchronized ExecutorService executorService() {
        if (mExecutorService == null) {
            mExecutorService = new ThreadPoolExecutor(INIT_THREAD_COUNT,
                MAX_THREAD_COUNT,
                SURPLUS_THREAD_LIFE,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(64),
                new DefaultThreadFactory());
        }
        return mExecutorService;
    }

    public void setIdleCallback(Runnable idleCallback) {
        mIdleCallback = idleCallback;
    }

    /**
     * Cancel all calls currently enqueued or executing. Includes calls
     */
    public synchronized void cancelAll() {
        for (AsyncCall call : readyAsyncCalls) {
            call.get().cancel();
        }

        for (AsyncCall call : runningAsyncCalls) {
            call.get().cancel();
        }

        for (RealCall call : runningSyncCalls) {
            call.cancel();
        }
    }

    /** Used by {@code AsyncCall#run} to signal completion. */
    public void finished(AsyncCall call) {
        finished(runningAsyncCalls, call,true);
    }

    /** Used by {@code Call#execute} to signal completion. */
    public void finished(RealCall call) {
        finished(runningSyncCalls, call, false);
    }
    private <T> void finished(Deque<T> calls, T call, boolean promoteCalls) {
        int runningCallsCount;
        Runnable idleCallback;
        synchronized (this) {
            if (!calls.remove(call)) {
                throw new AssertionError("Call wasn't in-flight!");
            }
            if (promoteCalls) {
                promoteCalls();
            }
            runningCallsCount = runningCallsCount();
            idleCallback = this.mIdleCallback;
        }

        if (runningCallsCount == 0 && idleCallback != null) {
            idleCallback.run();
        }
    }

    private void promoteCalls() {
        if (runningAsyncCalls.size() >= mMaxRequests) {
            return; // Already running max capacity.
        }
        if (readyAsyncCalls.isEmpty()) {
            return; // No ready calls to promote.
        }

        for (Iterator<AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
            AsyncCall call = i.next();

            if (runningCallsForHost(call) < mMaxRequestsPerHost) {
                i.remove();
                runningAsyncCalls.add(call);
                executorService().execute(call);
            }

            if (runningAsyncCalls.size() >= mMaxRequests) {
                return; // Reached max capacity.
            }
        }
    }

    /** Returns the number of running calls that share a host with {@code call}. */
    private int runningCallsForHost(AsyncCall call) {
        int result = 0;
        for (AsyncCall c : runningAsyncCalls) {
            if (c.url().equals(call.url())) {
                result++;
            }
        }
        return result;
    }

    public synchronized int runningCallsCount() {
        return runningAsyncCalls.size();
    }

    public synchronized void enqueue(AsyncCall call) {
        if (runningAsyncCalls.size() < mMaxRequests && runningCallsForHost(call) < mMaxRequestsPerHost) {
            runningAsyncCalls.add(call);
            executorService().execute(call);
        } else {
            readyAsyncCalls.add(call);
        }
    }

    public void executed(RealCall realCall) {
        runningSyncCalls.add(realCall);
    }
}
