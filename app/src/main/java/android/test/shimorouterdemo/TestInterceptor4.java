package android.test.shimorouterdemo;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import chuxin.shimo.shimowendang.smrouter.interfaces.Interceptor;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;
import io.reactivex.Observable;

public class TestInterceptor4 implements Interceptor {
    @Override
    public Observable<RouteResponse> intercept(@Nullable Context context, Chain chain) throws RuntimeException {
        if (true) {
            // 重定向 优先级大于 成功或者失败,不会继续走之前的 拦截器
            final RouteResponse currentResponse = chain.getCurrentResponse();
            currentResponse.openRedirect(RouterTable.TEST1);
            currentResponse.addData(new Bundle());
            return Observable.just(currentResponse);
        } else {

        }
        return null;
    }
}
