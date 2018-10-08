package com.zzw.guanglan.dialogs.date;

import android.text.TextUtils;


import com.zzw.guanglan.dialogs.multilevel.INamedEntity;

import java.io.Serializable;


public class DateTimeBean implements Serializable, INamedEntity {
    public enum Type {
        YEAR,
        MONTH,
        DAY
    }

    private int value = 0;
    private String unit = "";
    private Type type;

    public int getValue() {
        return value;
    }

    public DateTimeBean setValue(int value) {
        this.value = value;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public DateTimeBean setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public Type getType() {
        return type;
    }

    public DateTimeBean setType(Type type) {
        this.type = type;
        return this;
    }

    @Override
    public String _getDisplayName_() {
        return TextUtils.isEmpty(unit) ? String.valueOf(value) : (value + "" + unit);
    }

    public String getUnitedValue() {
        int minLength = 2;
        if (type == Type.YEAR) {
            minLength = 4;
        }

        return getUnitedValue(minLength);
    }

    private String getUnitedValue(int minLength) {
        String str = value + "";
        while (str.length() < minLength) {
            str = "0" + str;
        }
        return str;
    }
}
