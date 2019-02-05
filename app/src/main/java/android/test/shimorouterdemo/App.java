package android.test.shimorouterdemo;
import android.app.Application;

import chuxin.shimo.shimowendang.smrouter.annotation.Module;
import chuxin.shimo.shimowendang.smrouter.annotation.Modules;
import chuxin.shimo.shimowendang.smrouter.core.Routers;

@Modules(value = {"demo", "testModule"})
@Module("demo")
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Routers.init(this);
        //        Routers.showLog(true);
    }
}
