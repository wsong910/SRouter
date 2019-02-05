package chuxin.shimo.shimowendang.smrouter.core;

import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

import chuxin.shimo.shimowendang.smrouter.interfaces.RouterCallback;
import chuxin.shimo.shimowendang.smrouter.request.RouteRequest;
import chuxin.shimo.shimowendang.smrouter.request.RouteRequest.Builder;
import chuxin.shimo.shimowendang.smrouter.response.RouteResponse;
import io.reactivex.Observable;

import static chuxin.shimo.shimowendang.smrouter.core.RouterActivity.KEY_ENTER_ANIM;
import static chuxin.shimo.shimowendang.smrouter.core.RouterActivity.KEY_EXIT_ANIM;
import static chuxin.shimo.shimowendang.smrouter.core.RouterActivity.KEY_INTENT_FLAG;
import static chuxin.shimo.shimowendang.smrouter.core.RouterActivity.KEY_OPTIONS_COMPAT;
import static chuxin.shimo.shimowendang.smrouter.core.RouterActivity.KEY_SRC_PATH;

final public class Postcard extends Mapping {
    private static final String TAG = "Postcard";
    private final Bundle mBundle;
    // Animation
    private Bundle mOptionsCompat;    // The transition animation of activity
    private int mEnterAnim = -1;
    private int mExitAnim = -1;
    private int mRequestCode = -1;
    private volatile Observable<ActivityResult> mActivityResultObservable;

    Postcard(String path, Mapping mapping) {
        super(path, mapping.getActivity(), mapping.getExtraTypes());
        mBundle = parseExtras(Uri.parse(path));
    }

    public Bundle getExtras() {
        return mBundle;
    }

    public Postcard with(Bundle bundle) {
        if (null != bundle) {
            mBundle.putAll(bundle);
        }
        return this;
    }

    public Postcard withFlags(int flag) {
        this.mFlag = flag;
        return this;
    }

    public Postcard addFlags(int flags) {
        this.mFlag |= flags;
        return this;
    }

    public Bundle getOptionsBundle() {
        return mOptionsCompat;
    }

    public int getEnterAnim() {
        return mEnterAnim;
    }

    public int getExitAnim() {
        return mExitAnim;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    RouteRequest convertRouteRequest() {
        RouteRequest.Builder builder = new Builder();
        builder.data(mBundle)
            .enterAnim(mEnterAnim)
            .exitAnim(mExitAnim)
            .flag(mFlag)
            .optionsCompat(mOptionsCompat)
            .requestCode(mRequestCode)
            .path(mPathSrc);
        RouteRequest request = builder.build();
        return request;
    }

    //<editor-fold desc="异步调用方式">

    public void navigation() {
        navigation(null, null);
    }

    public void navigation(RouterCallback callback) {
        navigation(null, -1, callback);
    }

    public void navigation(Context mContext, int requestCode) {
        navigation(mContext, requestCode, null);
    }

    public void navigation(Context context, RouterCallback callback) {
        navigation(context, -1, callback);
    }

    public void navigation(Context context, int requestCode, RouterCallback callback) {
        mRequestCode = requestCode;
        Routers.getInstance().navigation(context, this, callback);
    }

    //</editor-fold>

    //<editor-fold desc="同步调用方式">

    public RouteResponse navigationSync() {
        return navigationSync(null, null);
    }

    public RouteResponse navigationSync(RouterCallback callback) {
        return navigationSync(null, callback);
    }

    public RouteResponse navigationSync(Context context, int requestCode) {
        mRequestCode = requestCode;
        return Routers.getInstance().navigationSync(context, this, null);
    }

    public RouteResponse navigationSync(Context context, RouterCallback callback) {
        return Routers.getInstance().navigationSync(context, this, callback);
    }

    //</editor-fold>


    Observable<ActivityResult> navigationSyncByActvityLauncher(Bundle data) {
        return Routers.getInstance().navigationSyncByActvityLauncher(data, this);
    }

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String, or null
     * @return current
     */
    public Postcard withString(@Nullable String key, @Nullable String value) {
        mBundle.putString(key, value);
        return this;
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a boolean
     * @return current
     */
    public Postcard withBoolean(@Nullable String key, boolean value) {
        mBundle.putBoolean(key, value);
        return this;
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a short
     * @return current
     */
    public Postcard withShort(@Nullable String key, short value) {
        mBundle.putShort(key, value);
        return this;
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value an int
     * @return current
     */
    public Postcard withInt(@Nullable String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }

    /**
     * Inserts a long value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a long
     * @return current
     */
    public Postcard withLong(@Nullable String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }

    /**
     * Inserts a double value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a double
     * @return current
     */
    public Postcard withDouble(@Nullable String key, double value) {
        mBundle.putDouble(key, value);
        return this;
    }

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a byte
     * @return current
     */
    public Postcard withByte(@Nullable String key, byte value) {
        mBundle.putByte(key, value);
        return this;
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a char
     * @return current
     */
    public Postcard withChar(@Nullable String key, char value) {
        mBundle.putChar(key, value);
        return this;
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a float
     * @return current
     */
    public Postcard withFloat(@Nullable String key, float value) {
        mBundle.putFloat(key, value);
        return this;
    }

    /**
     * Inserts a CharSequence value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence, or null
     * @return current
     */
    public Postcard withCharSequence(@Nullable String key, @Nullable CharSequence value) {
        mBundle.putCharSequence(key, value);
        return this;
    }

    /**
     * Inserts a Parcelable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Parcelable object, or null
     * @return current
     */
    public Postcard withParcelable(@Nullable String key, @Nullable Parcelable value) {
        mBundle.putParcelable(key, value);
        return this;
    }

    /**
     * Inserts an array of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an array of Parcelable objects, or null
     * @return current
     */
    public Postcard withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        mBundle.putParcelableArray(key, value);
        return this;
    }

    /**
     * Inserts a List of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList of Parcelable objects, or null
     * @return current
     */
    public Postcard withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        mBundle.putParcelableArrayList(key, value);
        return this;
    }

    /**
     * Inserts a SparceArray of Parcelable values into the mapping of this
     * Bundle, replacing any existing value for the given key.  Either key
     * or value may be null.
     *
     * @param key   a String, or null
     * @param value a SparseArray of Parcelable objects, or null
     * @return current
     */
    public Postcard withSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
        mBundle.putSparseParcelableArray(key, value);
        return this;
    }

    /**
     * Inserts an ArrayList value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public Postcard withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mBundle.putIntegerArrayList(key, value);
        return this;
    }

    /**
     * Inserts an ArrayList value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public Postcard withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
        return this;
    }

    /**
     * Inserts an ArrayList value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public Postcard withCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        mBundle.putCharSequenceArrayList(key, value);
        return this;
    }

    /**
     * Inserts a Serializable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Serializable object, or null
     * @return current
     */
    public Postcard withSerializable(@Nullable String key, @Nullable Serializable value) {
        mBundle.putSerializable(key, value);
        return this;
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a byte array object, or null
     * @return current
     */
    public Postcard withByteArray(@Nullable String key, @Nullable byte[] value) {
        mBundle.putByteArray(key, value);
        return this;
    }

    /**
     * Inserts a short array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a short array object, or null
     * @return current
     */
    public Postcard withShortArray(@Nullable String key, @Nullable short[] value) {
        mBundle.putShortArray(key, value);
        return this;
    }

    /**
     * Inserts a char array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a char array object, or null
     * @return current
     */
    public Postcard withCharArray(@Nullable String key, @Nullable char[] value) {
        mBundle.putCharArray(key, value);
        return this;
    }

    /**
     * Inserts a float array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a float array object, or null
     * @return current
     */
    public Postcard withFloatArray(@Nullable String key, @Nullable float[] value) {
        mBundle.putFloatArray(key, value);
        return this;
    }

    /**
     * Inserts a CharSequence array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence array object, or null
     * @return current
     */
    public Postcard withCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        mBundle.putCharSequenceArray(key, value);
        return this;
    }

    /**
     * Inserts a Bundle value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Bundle object, or null
     * @return current
     */
    public Postcard withBundle(@Nullable String key, @Nullable Bundle value) {
        mBundle.putBundle(key, value);
        return this;
    }

    /**
     * Set normal transition anim
     *
     * @param enterAnim enter
     * @param exitAnim  exit
     * @return current
     */
    public Postcard withTransition(int enterAnim, int exitAnim) {
        this.mEnterAnim = enterAnim;
        this.mExitAnim = exitAnim;
        return this;
    }

    /**
     * Set options compat
     *
     * @param compat compat
     * @return this
     */
    @RequiresApi(16)
    public Postcard withOptionsCompat(ActivityOptionsCompat compat) {
        if (null != compat) {
            this.mOptionsCompat = compat.toBundle();
        }
        return this;
    }

    @Override
    public String toString() {
        return "Postcard{" +
            "mBundle=" + mBundle +
            ", mOptionsCompat=" + mOptionsCompat +
            ", mEnterAnim=" + mEnterAnim +
            ", mExitAnim=" + mExitAnim +
            ", mPathSrc='" + mPathSrc + '\'' +
            ", mActivity=" + mActivity +
            ", mExtraTypes=" + mExtraTypes +
            ", mFlag=" + mFlag +
            ", mRequestCode=" + mRequestCode +
            '}';
    }

    void configIntent(@NonNull Intent intent) {
        intent.putExtras(mBundle);
        intent.putExtra(KEY_OPTIONS_COMPAT, mOptionsCompat);
        intent.putExtra(KEY_ENTER_ANIM, mEnterAnim);
        intent.putExtra(KEY_EXIT_ANIM, mExitAnim);
        intent.putExtra(KEY_SRC_PATH, mPathSrc);
        intent.putExtra(KEY_INTENT_FLAG, mFlag);
    }

    synchronized Observable<ActivityResult> getActivityResultObservable() {
        return mActivityResultObservable;
    }

    synchronized void setActivityResultObservable(Observable<ActivityResult> resultObservable) {
        mActivityResultObservable = resultObservable;
    }
}
