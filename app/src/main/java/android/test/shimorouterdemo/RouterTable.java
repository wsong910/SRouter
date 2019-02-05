package android.test.shimorouterdemo;
public class RouterTable {
    public final static String MAIN = "/main";
    public final static String TEST1 = "test1";
    public final static String TEST2 = "/test2";
    public final static String TEST3 = "/test3";
    public final static String TEST2_ERR = "/test2/info?id=2&sex=male";
    public final static String TEST2_RIGHT = "/test2?id=2&sex=male";
    public static final String TEST4_INFO = "/test2/info";
    public static final String TEST4_INFO_WITH_ID = "/test2/info/:fileid";
    public static final String TEST5 = "/test5";
}
