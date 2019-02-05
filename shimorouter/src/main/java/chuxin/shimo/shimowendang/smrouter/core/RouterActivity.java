package chuxin.shimo.shimowendang.smrouter.core;
import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import io.reactivex.Observable;

public class RouterActivity extends Activity {

    public static final String ACTION_NAME = "chuxin.shimo.shimowendang.smrouter.routerActivity";
    public static final String KEY_OPTIONS_COMPAT = "optionsCompat";
    public static final String KEY_ENTER_ANIM = "enterAnim";
    public static final String KEY_EXIT_ANIM = "exitAnim";
    public static final String KEY_SRC_PATH = "PathSrc";
    public static final String KEY_INTENT_FLAG = "flag";
    public static final int ROUTER_REQUEST_CODE = 2014;
    public static final String JUMP_ACTIVITY_NAME = "jumpActivity";
    public static final String JUMP_ACTIVITY_PACKAGE_NAME = "jumpActivityPackageName";
    private Observable<ActivityResult> mActivityResultObservable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        ComponentName componentName = new ComponentName(intent.getStringExtra(JUMP_ACTIVITY_PACKAGE_NAME),intent.getStringExtra(JUMP_ACTIVITY_NAME));
        Intent jumpIntent = new Intent();
        jumpIntent.setComponent(componentName);

        jumpIntent.putExtra(KEY_SRC_PATH,intent.getStringExtra(KEY_SRC_PATH));
        final int flag = intent.getIntExtra(KEY_INTENT_FLAG, -1);
        if(flag!=-1) {
            jumpIntent.setFlags(flag);
        }
        final Bundle extras = intent.getExtras();
        if (extras != null) {
            jumpIntent.putExtras(extras);
        }

        final Bundle mOptionsCompat = intent.getBundleExtra(KEY_OPTIONS_COMPAT);
        final int enterAnim = intent.getIntExtra(KEY_ENTER_ANIM,-1);
        final int exitAnim = intent.getIntExtra(KEY_EXIT_ANIM,-1);
        mActivityResultObservable = Routers.getInstance()
            .getActivityLauncher()
            .startActivityForResult(this, jumpIntent, ROUTER_REQUEST_CODE,enterAnim,exitAnim,mOptionsCompat);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Routers.getInstance().getActivityLauncher().onActivityResult(requestCode, resultCode, data);
        finish();
    }


    public Observable<ActivityResult> getObservableActivityResult() {
        return mActivityResultObservable;
    }
}
