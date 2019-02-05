package chuxin.shimo.shimowendang.smrouter.response;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import chuxin.shimo.shimowendang.smrouter.interfaces.RouteStatus;
import chuxin.shimo.shimowendang.smrouter.request.RouteRequest;

public final class RouteResponse {
    public static final int ROUTE_RESULT_OK = -1;
    public static final int ROUTE_RESULT_CANCEL = 0;
    private RouteRequest mOriginalRequest;
    //请求返回的数据
    private Bundle mDatas = new Bundle();
    //本次请求路由
    private String mPath;
    private RouteStatus mCode;
    private String mMessage;
    private int mRequestCode;
    private int mResultCode;
    private String mRedirectUrl;

    private RouteResponse(Builder builder) {
        mOriginalRequest = builder.mOriginalRequest;
        addData(builder.mDatas);
        mPath = builder.mPath;
        mCode = builder.mCode;
        mMessage = builder.mMessage;
        mRequestCode = builder.mRequestCode;
        mResultCode = builder.mResultCode;
    }

    @NonNull
    public RouteStatus getStatus() {
        return mCode;
    }

    public void setStatus(@NonNull RouteStatus status) {
        this.mCode = status;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public void setResultCode(int resultCode) {
        mResultCode = resultCode;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public void addData(Bundle data) {
        if (data != null) {
            mDatas.putAll(data);
        }
    }

    public Bundle getDatas() {
        return mDatas;
    }

    public RouteRequest getOriginalRequest() {
        return mOriginalRequest;
    }

    @Override
    public String toString() {
        return "RouteResponse{" +
            "mOriginalRequest=" + mOriginalRequest +
            ", mDatas=" + mDatas +
            ", mPath='" + mPath + '\'' +
            ", mCode=" + mCode +
            ", mMessage='" + mMessage + '\'' +
            ", mRequestCode=" + mRequestCode +
            ", mResultCode=" + mResultCode +
            '}';
    }

    public void openRedirect(String redirectUrl) {
        mRedirectUrl = redirectUrl;
    }

    public boolean isRedirect() {
        return !TextUtils.isEmpty(mRedirectUrl);
    }

    public String redirectUrl() {
        return mRedirectUrl;
    }

    public static class Builder {
        private RouteRequest mOriginalRequest;
        //请求返回的数据
        private Bundle mDatas;
        //本次请求路由
        private String mPath;
        private RouteStatus mCode = RouteStatus.FAILED;
        private String mMessage;
        private int mRequestCode;
        private int mResultCode;

        public Builder() {

        }

        Builder(RouteResponse response) {
            mOriginalRequest = response.mOriginalRequest;
            mDatas = response.mDatas;
            mPath = response.mPath;
            mCode = response.mCode;
            mMessage = response.mMessage;
            mRequestCode = response.mRequestCode;
            mResultCode = response.mResultCode;
        }

        public Builder request(RouteRequest request) {
            mOriginalRequest = request;
            return this;
        }

        public Builder data(Bundle datas) {
            mDatas = datas;
            return this;
        }

        public Builder path(String path) {
            mPath = path;
            return this;
        }

        public Builder message(String msg) {
            mMessage = msg;
            return this;
        }

        public Builder flag(RouteStatus code) {
            mCode = code;
            return this;
        }

        public Builder requestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        public Builder resultCode(int resultCode) {
            mResultCode = resultCode;
            return this;
        }

        public RouteResponse build() {
            return new RouteResponse(this);
        }
    }
}
