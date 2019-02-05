package chuxin.shimo.shimowendang.smrouter.utils;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ActivityLauncher {

    private PublishSubject<ActivityResult> mPublishSubject;

    public Observable<ActivityResult> startActivityForResult(Activity activity,
        @NonNull
            Intent intent, int requestCode,
        int enterAnim, int exitAnim, @Nullable
        Bundle options) {
        mPublishSubject = PublishSubject.create();
        try {
            ActivityCompat.startActivityForResult(activity, intent, requestCode, options);
        } catch (ActivityNotFoundException | SecurityException e) {
            e.printStackTrace();
            mPublishSubject.onError(e);
            return Observable.just(new ActivityResult(Activity.RESULT_CANCELED, null));
        }
        if ((-1 != enterAnim && -1 != exitAnim)) {
            activity.overridePendingTransition(enterAnim,
                exitAnim);
        }
        return mPublishSubject;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mPublishSubject != null) {
            try {
                mPublishSubject.onNext(new ActivityResult(resultCode, data));
            } catch (Exception e) {
                mPublishSubject.onError(e);
                e.printStackTrace();
            }
            mPublishSubject.onComplete();
        }
    }

}
