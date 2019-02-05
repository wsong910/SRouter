package chuxin.shimo.shimowendang.smrouter.interfaces;

/**
 * Result for each route.
 * <p>
 * Created by chenenyu on 2017/3/9.
 */
public enum RouteStatus {
    /**
     * 成功
     */
    SUCCEED,
    /**
     * 跳转失败
     */
    FAILED;

    public boolean isSuccessful() {
        return this == SUCCEED;
    }
}
