package com.zzw.guanglan.widgets;

import android.text.Editable;
import android.text.InputFilter;

public class ProxyEditable implements Editable {
    private Editable src;

    public ProxyEditable() {
    }

    public ProxyEditable(Editable src) {
        this.src = src;
    }

    public ProxyEditable setSrc(Editable src) {
        this.src = src;
        return this;
    }

    @Override
    public Editable replace(int i, int i1, CharSequence charSequence, int i2, int i3) {
        return src.replace(i, i1, charSequence, i2, i3);
    }

    @Override
    public Editable replace(int i, int i1, CharSequence charSequence) {
        return src.replace(i, i1, charSequence);
    }

    @Override
    public Editable insert(int i, CharSequence charSequence, int i1, int i2) {
        return src.insert(i, charSequence, i1, i2);
    }

    @Override
    public Editable insert(int i, CharSequence charSequence) {
        return src.insert(i, charSequence);
    }

    @Override
    public Editable delete(int i, int i1) {
        return src.delete(i1, i1);
    }

    @Override
    public Editable append(CharSequence charSequence) {
        return src.append(charSequence);
    }

    @Override
    public Editable append(CharSequence charSequence, int i, int i1) {
        return src.append(charSequence, i, i1);
    }

    @Override
    public Editable append(char c) {
        return src.append(c);
    }

    @Override
    public void clear() {
        src.clear();
    }

    @Override
    public void clearSpans() {
        src.clearSpans();
    }

    @Override
    public void setFilters(InputFilter[] inputFilters) {
        src.setFilters(inputFilters);
    }

    @Override
    public InputFilter[] getFilters() {
        return src.getFilters();
    }

    @Override
    public void getChars(int i, int i1, char[] chars, int i2) {
        src.getChars(i, i1, chars, i2);
    }

    @Override
    public void setSpan(Object o, int i, int i1, int i2) {
        src.setSpan(o, i, i1, i2);
    }

    @Override
    public void removeSpan(Object o) {
        src.removeSpan(o);
    }

    @Override
    public <T> T[] getSpans(int i, int i1, Class<T> aClass) {
        return src.getSpans(i, i1, aClass);
    }

    @Override
    public int getSpanStart(Object o) {
        return src.getSpanStart(o);
    }

    @Override
    public int getSpanEnd(Object o) {
        return src.getSpanEnd(o);
    }

    @Override
    public int getSpanFlags(Object o) {
        return src.getSpanFlags(o);
    }

    @Override
    public int nextSpanTransition(int i, int i1, Class aClass) {
        return src.nextSpanTransition(i, i1, aClass);
    }

    @Override
    public int length() {
        return src.length();
    }

    @Override
    public char charAt(int i) {
        return src.charAt(i);
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return src.subSequence(i, i1);
    }

    @Override
    public String toString() {
        return src.toString();
    }
}