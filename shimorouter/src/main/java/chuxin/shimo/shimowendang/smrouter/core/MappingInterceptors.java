package chuxin.shimo.shimowendang.smrouter.core;
import android.app.Activity;

import chuxin.shimo.shimowendang.smrouter.interfaces.Interceptor;

public class MappingInterceptors {
    private final Class<? extends Activity> mActivity;
    private final Interceptor mInterceptor;

    public MappingInterceptors(Class<? extends Activity> activity, Interceptor interceptor) {
        mActivity = activity;
        mInterceptor = interceptor;
    }
}
