package chuxin.shimo.shimowendang.smrouter.core;

public class ExtraTypes {
    public static final int STRING = -1;
    public static final int INT = 1;
    public static final int LONG = 2;
    public static final int BOOL = 3;
    public static final int SHORT = 4;
    public static final int FLOAT = 5;
    public static final int DOUBLE = 6;
    public static final int BYTE = 7;
    public static final int CHAR = 8;
    private String[] mIntExtra;
    private String[] mLongExtra;
    private String[] mBooleanExtra;
    private String[] mShortExtra;
    private String[] mFloatExtra;
    private String[] mDoubleExtra;
    private String[] mByteExtra;
    private String[] mCharExtra;
    private String[] mFlag;

    private static boolean arrayContain(String[] array, String value) {
        if (array == null) {
            return false;
        }
        for (String s : array) {
            if (s.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public String[] getIntExtra() {
        return mIntExtra;
    }

    public void setIntExtra(String[] intExtra) {
        this.mIntExtra = intExtra;
    }

    public String[] getLongExtra() {
        return mLongExtra;
    }

    public void setLongExtra(String[] longExtra) {
        this.mLongExtra = longExtra;
    }

    public String[] getBooleanExtra() {
        return mBooleanExtra;
    }

    public void setBooleanExtra(String[] booleanExtra) {
        this.mBooleanExtra = booleanExtra;
    }

    public String[] getShortExtra() {
        return mShortExtra;
    }

    public void setShortExtra(String[] shortExtra) {
        this.mShortExtra = shortExtra;
    }

    public String[] getFloatExtra() {
        return mFloatExtra;
    }

    public void setFloatExtra(String[] floatExtra) {
        this.mFloatExtra = floatExtra;
    }

    public String[] getDoubleExtra() {
        return mDoubleExtra;
    }

    public void setDoubleExtra(String[] doubleExtra) {
        this.mDoubleExtra = doubleExtra;
    }

    public String[] getByteExtra() {
        return mByteExtra;
    }

    public void setByteExtra(String[] byteExtra) {
        this.mByteExtra = byteExtra;
    }

    public String[] getCharExtra() {
        return mCharExtra;
    }

    public void setCharExtra(String[] charExtra) {
        this.mCharExtra = charExtra;
    }

    public String[] getFlag() {
        return mFlag;
    }

    public void setFlag(String[] flag) {
        mFlag = flag;
    }

    public int getType(String name) {
        if (arrayContain(mIntExtra, name)) {
            return INT;
        }
        if (arrayContain(mLongExtra, name)) {
            return LONG;
        }
        if (arrayContain(mBooleanExtra, name)) {
            return BOOL;
        }
        if (arrayContain(mShortExtra, name)) {
            return SHORT;
        }
        if (arrayContain(mFloatExtra, name)) {
            return FLOAT;
        }
        if (arrayContain(mDoubleExtra, name)) {
            return DOUBLE;
        }
        if (arrayContain(mByteExtra, name)) {
            return BYTE;
        }
        if (arrayContain(mCharExtra, name)) {
            return CHAR;
        }
        return STRING;
    }

}
