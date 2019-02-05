package chuxin.shimo.shimowendang.smrouter.interfaces;

import chuxin.shimo.shimowendang.smrouter.request.RouteRequest;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;

public interface Call {
    /** Returns the original request that initiated this call. */
    RouteRequest request();

    /**
     * 同步
     * @return
     * @throws RuntimeException
     */
    RouteResponse execute(RouterCallback responseCallback) throws RuntimeException;

    /**
     * 异步
     * @param responseCallback
     * @throws RuntimeException
     */
    void enqueue(RouterCallback responseCallback) throws RuntimeException;

    void cancel();

    boolean isExecuted();

    boolean isCanceled();
}
