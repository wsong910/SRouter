package chuxin.shimo.shimowendang.smrouter.request;

import android.os.Bundle;

public final class RouteRequest {
    public static final int INVALID_CODE = -1;
    //本次请求携带的数据
    private final Bundle mDatas = new Bundle();
    //本次请求路由
    private String mPath;
    //本次请求启动方式
    private int mFlag;
    // Animation
    private Bundle mOptionsCompat;    // The transition animation of activity
    private int mEnterAnim;
    private int mExitAnim;
    private int mRequestCode;


    public RouteRequest(Builder builder) {
        addDatas(builder.mDatas);
        mPath = builder.mPath;
        mFlag = builder.mFlag;
        mOptionsCompat = builder.mOptionsCompat;
        mEnterAnim = builder.mEnterAnim;
        mExitAnim = builder.mExitAnim;
        mRequestCode = builder.mRequestCode;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public Bundle getData() {
        return mDatas;
    }

    public int getFlag() {
        return mFlag;
    }

    public void setFlag(int flag) {
        mFlag = flag;
    }

    public Bundle getOptionsCompat() {
        return mOptionsCompat;
    }

    public void setOptionsCompat(Bundle optionsCompat) {
        this.mOptionsCompat = optionsCompat;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    public void setRequestCode(int requestCode) {
        mRequestCode = requestCode;
    }

    public int getEnterAnim() {
        return mEnterAnim;
    }

    public void setEnterAnim(int enterAnim) {
        if (enterAnim < 0) {
            this.mEnterAnim = INVALID_CODE;
        } else {
            this.mEnterAnim = enterAnim;
        }
    }

    public int getExitAnim() {
        return mExitAnim;
    }

    public void setExitAnim(int exitAnim) {
        if (exitAnim < 0) {
            this.mExitAnim = INVALID_CODE;
        } else {
            this.mExitAnim = exitAnim;
        }
    }

    public void addDatas(Bundle bundle) {
        if (bundle != null) {
            mDatas.putAll(bundle);
        }
    }


    @Override
    public String toString() {
        return "RouteRequest{" +
            ", mDatas=" + mDatas +
            ", mPath='" + mPath + '\'' +
            ", mFlag=" + mFlag +
            ", mOptionsCompat=" + mOptionsCompat +
            ", mEnterAnim=" + mEnterAnim +
            ", mExitAnim=" + mExitAnim +
            ", mRequestCode=" + mRequestCode +
            '}';
    }

    public static class Builder {
        private Bundle mDatas = new Bundle();
        private String mPath;
        private int mFlag = INVALID_CODE;
        private Bundle mOptionsCompat;    // The transition animation of activity
        private int mEnterAnim = INVALID_CODE;
        private int mExitAnim = INVALID_CODE;
        private int mRequestCode;

        public Builder() {

        }

        Builder(RouteRequest request) {
            data(request.mDatas);
            mPath = request.mPath;
            mFlag = request.mFlag;
            mOptionsCompat = request.mOptionsCompat;
            mEnterAnim = request.mEnterAnim;
            mExitAnim = request.mExitAnim;
            mRequestCode = request.mRequestCode;
        }

        public Builder data(Bundle datas) {
            if (datas != null) {
                mDatas.putAll(datas);
            }
            return this;
        }

        public Builder path(String path) {
            mPath = path;
            return this;
        }

        public Builder flag(int flag) {
            mFlag = flag;
            return this;
        }

        public Builder optionsCompat(Bundle optionsCompat) {
            mOptionsCompat = optionsCompat;
            return this;
        }

        public Builder enterAnim(int enterAnim) {
            mEnterAnim = enterAnim;
            return this;
        }

        public Builder exitAnim(int exitAnim) {
            mExitAnim = exitAnim;
            return this;
        }

        public RouteRequest build() {
            return new RouteRequest(this);
        }

        public Builder requestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }
    }
}
