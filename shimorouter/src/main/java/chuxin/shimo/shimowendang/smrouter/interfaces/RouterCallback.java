package chuxin.shimo.shimowendang.smrouter.interfaces;

import android.support.annotation.Nullable;

import java.io.Serializable;

import chuxin.shimo.shimowendang.smrouter.request.RouteRequest;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;

public interface RouterCallback extends Serializable {
    void onFailure(@Nullable RouteRequest request, RuntimeException e);

    void onResponse(RouteResponse response);

    /**
     * Callback when find the destination.
     *
     * @param request meta
     */
    void onFound(RouteRequest request);

    /**
     * Callback after navigation.
     *
     * @param request meta
     */
    void onArrival(RouteRequest request);

    /**
     * Callback on interrupt and redirect.
     *
     * @param redirectUrl meta
     */
    void onRedirect(String redirectUrl);

}
