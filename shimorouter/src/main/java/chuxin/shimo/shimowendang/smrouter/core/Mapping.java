package chuxin.shimo.shimowendang.smrouter.core;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import chuxin.shimo.shimowendang.smrouter.interfaces.Interceptor;

public class Mapping {
    private final List<Interceptor> mInterceptors = new ArrayList<>();
    protected String mPathSrc;
    protected Class<? extends Activity> mActivity;
    protected ExtraTypes mExtraTypes;
    protected int mFlag = -1;
    protected Path mFormatPath;

    public Mapping(String pathSrc, Class<? extends Activity> activity, ExtraTypes extraTypes) {
        this(pathSrc, activity, extraTypes, null);
    }

    public Mapping(String pathSrc, Class<? extends Activity> activity, ExtraTypes extraTypes,
        List<Interceptor> interceptors) {
        if (interceptors != null) {
            mInterceptors.addAll(interceptors);
        }
        if (pathSrc == null) {
            throw new NullPointerException("format can not be null");
        }
        this.mPathSrc = pathSrc;
        this.mActivity = activity;
        this.mExtraTypes = extraTypes;
        if (mExtraTypes != null) {
            final String[] flag = extraTypes.getFlag();
            if (flag != null && flag.length > 0 && !TextUtils.isEmpty(flag[0])) {
                this.mFlag = Integer.parseInt(flag[0]);
            }
        }
        this.mFormatPath = Path.create(Uri.parse(pathSrc));
    }

    public Class<? extends Activity> getActivity() {
        return mActivity;
    }

    ExtraTypes getExtraTypes() {
        return mExtraTypes;
    }

    public String getPathSrc() {
        return mPathSrc;
    }

    public int getFlag() {
        return mFlag;
    }

    @Override
    public String toString() {
        return String.format("%s => %s", mPathSrc, mActivity);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Mapping) {
            Mapping that = (Mapping) o;
            return mPathSrc.equals(that.mPathSrc);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mPathSrc.hashCode();
    }

    boolean match(Path fullLink) {
        if (mFormatPath.isHttp()) {
            return Path.match(mFormatPath, fullLink);
        } else {
            // fullLink without host
            boolean match = Path.match(mFormatPath.next(), fullLink.next());
            if (!match && fullLink.next() != null) {
                // fullLink with host
                match = Path.match(mFormatPath.next(), fullLink.next().next());
            }
            return match;
        }
    }

    Bundle parseExtras(Uri uri) {
        Bundle bundle = new Bundle();
        // path segments // ignore scheme
        Path p = mFormatPath.next();
        Path y = Path.create(uri).next();
        while (p != null) {
            if (p.isArgument()) {
                put(bundle, p.argument(), y.value());
            }
            p = p.next();
            y = y.next();
        }
        // parameter
        Set<String> names = getQueryParameterNames(uri);
        for (String name : names) {
            String value = uri.getQueryParameter(name);
            put(bundle, name, value);
        }
        return bundle;
    }


    private void put(Bundle bundle, String name, String value) {
        if(mExtraTypes==null) {
            return;
        }
        int type = mExtraTypes.getType(name);
        if (type == ExtraTypes.STRING) {
            type = mExtraTypes.getType(name);
        }
        switch (type) {
            case ExtraTypes.INT:
                bundle.putInt(name, Integer.parseInt(value));
                break;
            case ExtraTypes.LONG:
                bundle.putLong(name, Long.parseLong(value));
                break;
            case ExtraTypes.BOOL:
                bundle.putBoolean(name, Boolean.parseBoolean(value));
                break;
            case ExtraTypes.SHORT:
                bundle.putShort(name, Short.parseShort(value));
                break;
            case ExtraTypes.FLOAT:
                bundle.putFloat(name, Float.parseFloat(value));
                break;
            case ExtraTypes.DOUBLE:
                bundle.putDouble(name, Double.parseDouble(value));
                break;
            case ExtraTypes.BYTE:
                bundle.putByte(name, Byte.parseByte(value));
                break;
            case ExtraTypes.CHAR:
                bundle.putChar(name, value.charAt(0));
                break;
            default:
                bundle.putString(name, value);
                break;
        }
    }

    private static Set<String> getQueryParameterNames(Uri uri) {
        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }

        Set<String> names = new LinkedHashSet<String>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(Uri.decode(name));
            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }


    public List<Interceptor> interceptors() {
        return mInterceptors;
    }

}
