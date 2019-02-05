package chuxin.shimo.shimowendang.smrouter.core;

import android.net.Uri;
import android.webkit.URLUtil;

public class Path {
    private final String mValue;
    private Path mNext;

    private Path(String value) {
        this.mValue = value;
    }

    public static boolean match(final Path format, final Path link) {
        if (format == null || link == null) {
            return false;
        }
        if (format.length() != link.length()) {
            return false;
        }
        Path x = format;
        Path y = link;
        while (x != null) {
            if (!x.match(y)) {
                return false;
            }
            x = x.mNext;
            y = y.mNext;
        }
        return true;
    }

    public static Path create(Uri uri) {
        String encodedPath = uri.toString();
        //统一规范三个步骤：
        //删除第一个/
        if (encodedPath.startsWith("/")) {
            encodedPath = encodedPath.replaceFirst("/", "");
            uri = Uri.parse(encodedPath);
        }
        //添加协议
        if (!encodedPath.contains("://") && encodedPath.contains("/")) {
            encodedPath = "helper://".concat(encodedPath);
            uri = Uri.parse(encodedPath);
        }
        //删除最后一个/
        if (encodedPath.endsWith("/")) {
            encodedPath = encodedPath.substring(0, encodedPath.length() - 1);
            uri = Uri.parse(encodedPath);
        }
        //统一规范结束
        String scheme = uri.getScheme();
        if (scheme == null) {
            if (!URLUtil.isNetworkUrl(encodedPath)) {
                uri = Uri.parse("helper://".concat(encodedPath));
            }
            scheme = "helper";
        }
        Path path = new Path(scheme.concat("://"));
        String urlPath = uri.getPath();
        if (urlPath == null) {
            urlPath = "";
        }
        parse(path, uri.getHost() + urlPath);
        return path;
    }

    private static void parse(Path scheme, String s) {
        String[] components = s.split("/");
        Path curPath = scheme;
        for (String component : components) {
            Path temp = new Path(component);
            curPath.mNext = temp;
            curPath = temp;
        }
    }

    public Path next() {
        return mNext;
    }

    public int length() {
        Path path = this;
        int len = 1;
        while (path.mNext != null) {
            len++;
            path = path.mNext;
        }
        return len;
    }

    private boolean match(Path path) {
        return isArgument() || mValue.equals(path.mValue);
    }

    public boolean isArgument() {
        return mValue.startsWith(":");
    }

    public String argument() {
        return mValue.substring(1);
    }

    public String value() {
        return mValue;
    }

    public boolean isHttp() {
        return URLUtil.isNetworkUrl(mValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return match((Path) obj);
    }
}
