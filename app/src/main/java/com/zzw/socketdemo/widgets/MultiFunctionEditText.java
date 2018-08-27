package com.zzw.socketdemo.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zzw.socketdemo.R;


/**
 * Created by LuoHaifeng on 2018/2/25 0025.
 * Email:496349136@qq.com
 */

public class MultiFunctionEditText extends AppCompatEditText implements TextWatcher {
    private boolean letterSpaceEnable;
    private boolean clearButtonEnable;
    private boolean togglePasswordEnable;
    private boolean startZero;
    private boolean onlyTextBold;
    private Drawable clearDrawable;
    private String letterSpaceRule;
    private Drawable passwordVisibleIcon, passwordInVisibleIcon;
    private int iconPadding = 0;

    private int[] letterSpaceGap = new int[]{Integer.MAX_VALUE};
    private boolean isRepeatLastGap = false;

    private ProxyEditable proxyEditable;

    private float touchDownX, touchDownY;
    private final Drawable[] extraDrawablesSrc = new Drawable[2];//0是clearButton，1是passwordToggle
    private HorizontalDrawables extraDrawables = new HorizontalDrawables(extraDrawablesSrc);
    private TextWatcher onlyTextBoldWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getPaint().setFakeBoldText(s.length() > 0);
        }
    };

    private TextWatcher noZeroStartWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (getInputType() == InputType.TYPE_CLASS_NUMBER) {
                if (s.toString().startsWith("0") && !s.toString().equals("0")) {
                    s.replace(0, 1, "");
                }
            }
        }
    };

    public MultiFunctionEditText(Context context) {
        super(context);
        init(null, -1);
    }

    public MultiFunctionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, -1);
    }

    public MultiFunctionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attributeSet, int defStyleAttr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.MultiFunctionEditText, defStyleAttr, -1);
        letterSpaceEnable = typedArray.getBoolean(R.styleable.MultiFunctionEditText_letterSpaceEnable, false);
        clearButtonEnable = typedArray.getBoolean(R.styleable.MultiFunctionEditText_clearButtonEnable, false);
        togglePasswordEnable = typedArray.getBoolean(R.styleable.MultiFunctionEditText_togglePasswordEnable, false);
        clearDrawable = typedArray.getDrawable(R.styleable.MultiFunctionEditText_clearButtonIcon);
        letterSpaceRule = typedArray.getString(R.styleable.MultiFunctionEditText_letterSpaceRule);
        passwordVisibleIcon = typedArray.getDrawable(R.styleable.MultiFunctionEditText_togglePasswordVisibleIcon);
        passwordInVisibleIcon = typedArray.getDrawable(R.styleable.MultiFunctionEditText_togglePasswordHideIcon);
        iconPadding = typedArray.getDimensionPixelSize(R.styleable.MultiFunctionEditText_iconPadding, 0);
        startZero = typedArray.getBoolean(R.styleable.MultiFunctionEditText_startZero, true);
        onlyTextBold = typedArray.getBoolean(R.styleable.MultiFunctionEditText_onlyTextBold, false);
        if (TextUtils.isEmpty(letterSpaceRule)) {
            letterSpaceRule = "3,4...";
        }
        typedArray.recycle();

        if (letterSpaceEnable) {
            enableLetterSpaceFunc();
        }

        if (!startZero) {
            removeTextChangedListener(noZeroStartWatcher);
            addTextChangedListener(noZeroStartWatcher);
        }

        if (onlyTextBold) {
            removeTextChangedListener(onlyTextBoldWatcher);
            addTextChangedListener(onlyTextBoldWatcher);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    public boolean notInV(int value, int[] comVal) {
        for (int aComVal : comVal) {
            if (value == aComVal) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!letterSpaceEnable) {
            return;
        }
        if (s == null || s.length() == 0) {
            return;
        }
        int spaceStartPos[] = new int[letterSpaceGap.length];
        int position = 0;
        for (int i = 0; i < letterSpaceGap.length; i++) {
            int append;
            if (i == 0) {
                append = 0;
            } else {
                append = 1;
            }
            position = position + letterSpaceGap[i] + append;
            spaceStartPos[i] = position;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (!notInV(i, spaceStartPos) || s.charAt(i) != ' ') {
                sb.append(s.charAt(i));
                for (int spaceStartPo : spaceStartPos) {
                    if (sb.length() == (spaceStartPo + 1) && sb.charAt(sb.length() - 1) != ' ') {
                        sb.insert(sb.length() - 1, ' ');
                    }
                }
            }
        }
        try {
            if (!sb.toString().equals(s.toString())) {
                int index = start + 1;
                if (sb.charAt(start) == ' ') {
                    if (before == 0) {
                        index++;
                    } else {
                        index--;
                    }
                } else {
                    if (before == 1) {
                        index--;
                    }
                }
                setText(sb.toString());
                setSelection(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {//修正第一次设置值后光标位置
            setSelection(getText().length());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public Editable getText() {
        if (letterSpaceEnable) {
            if (proxyEditable == null) {
                proxyEditable = new ProxyEditable() {
                    @Override
                    public String toString() {
                        return super.toString().replace(" ", "");
                    }
                };
            }
            return proxyEditable.setSrc(super.getText());
        }
        return super.getText();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        updateExtraDrawables();
        super.onDraw(canvas);
        //drawExtra drawables
        //偏移getScrollX是为了修正当文字内容超出范围后引起滚动时，clearButton位置
        int left = getMeasuredWidth() - extraDrawables.getIntrinsicWidth() - getPaddingRight() + getScrollX();
        Drawable rightDrawable = getCompoundDrawables()[2];
        if (rightDrawable != null) {
            left -= (rightDrawable.getIntrinsicWidth() + getCompoundDrawablePadding());
        }
        int top = getMeasuredHeight() / 2 - extraDrawables.getIntrinsicHeight() / 2;
        int right = left + extraDrawables.getIntrinsicWidth();
        int bottom = top + extraDrawables.getIntrinsicHeight();
        extraDrawables.setBounds(left, top, right, bottom);
        extraDrawables.draw(canvas);
    }

    @Override
    public int getCompoundPaddingRight() {
        if (extraDrawables != null) {
            return super.getCompoundPaddingRight() + extraDrawables.getIntrinsicWidth() + getCompoundDrawablePadding();
        } else {
            return super.getCompoundPaddingRight();
        }
    }

    protected void updateExtraDrawables() {
        extraDrawablesSrc[0] = isShowClearButton() ? clearDrawable : null;
        if (isShowPasswordToggleButton()) {
            if (getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {//明文状态
                extraDrawablesSrc[1] = passwordVisibleIcon;
            } else if (getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                extraDrawablesSrc[1] = passwordInVisibleIcon;
            } else {
                extraDrawablesSrc[1] = null;
            }
        } else {
            extraDrawablesSrc[1] = null;
        }
    }

    protected boolean isShowClearButton() {
        return isFocused() && getText().length() > 0 && clearButtonEnable && clearDrawable != null;
    }

    protected boolean isShowPasswordToggleButton() {
        return isFocused() && togglePasswordEnable && passwordInVisibleIcon != null && passwordVisibleIcon != null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchDownX = event.getX();
                touchDownY = event.getY();
                if (isInExtraDrawableRegion(touchDownX, touchDownY)) { //修复触发 粘帖 的bug
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (isShowClearButton()) {
                    boolean isTouchDownInClearRegion = isInClearDrawableRegion(touchDownX, touchDownY);
                    boolean isTouchUpInClearRegion = isInClearDrawableRegion(event.getX(), event.getY());
                    if (isTouchUpInClearRegion && isTouchDownInClearRegion) {
                        setText("");//清空数据
                        return false;
                    }
                }

                if (isShowPasswordToggleButton()) {
                    boolean isTouchDownInToggleRegion = isInToggleVisibleDrawableRegion(touchDownX, touchDownY);
                    boolean isTouchUpInToggleRegion = isInToggleVisibleDrawableRegion(event.getX(), event.getY());
                    if (isTouchDownInToggleRegion && isTouchUpInToggleRegion) {
                        togglePassword();
                        return false;
                    }
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void togglePassword() {
        int selectionIndex = getSelectionStart();
        if (getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {//明文状态
            setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else if (getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        setSelection(selectionIndex);
    }

    private boolean isInClearDrawableRegion(float x, float y) {
        if (extraDrawablesSrc[0] != null) {
            Rect bounds = new Rect(extraDrawablesSrc[0].getBounds());
            bounds.top = 0;
            bounds.bottom = getMeasuredHeight();
            return bounds.contains((int) x + getScrollX(), (int) y);//偏移getScrollX是为了修正当文字内容超出范围后引起滚动时，clearButton位置
        }
        return false;
    }

    private boolean isInExtraDrawableRegion(float x, float y) {
        Rect bounds = new Rect(extraDrawables.getBounds());
        bounds.top = 0;
        bounds.bottom = getMeasuredHeight();
        return bounds.contains((int) x + getScrollX(), (int) y);
    }

    private boolean isInToggleVisibleDrawableRegion(float x, float y) {
        if (extraDrawablesSrc[1] != null) {
            Rect bounds = new Rect(extraDrawablesSrc[1].getBounds());
            bounds.top = 0;
            bounds.bottom = getMeasuredHeight();
            return bounds.contains((int) x + getScrollX(), (int) y);//偏移getScrollX是为了修正当文字内容超出范围后引起滚动时，toggleButton位置
        }
        return false;
    }

    private void enableLetterSpaceFunc() {
        isRepeatLastGap = letterSpaceRule.endsWith("...");
        String[] strs = letterSpaceRule.replace("...", "").split(",");
        letterSpaceGap = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            int gap = Integer.valueOf(strs[i]);
            letterSpaceGap[i] = gap;
        }

        removeTextChangedListener(this);
        addTextChangedListener(this);
    }

    private class HorizontalDrawables extends Drawable {
        private Drawable[] drawables;

        public HorizontalDrawables() {
        }

        public HorizontalDrawables(Drawable... drawables) {
            this.drawables = drawables == null ? new Drawable[]{} : drawables;
        }

        public HorizontalDrawables setDrawables(Drawable[] drawables) {
            this.drawables = drawables;
            return this;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    drawable.draw(canvas);
                }
            }
        }

        @Override
        public void setBounds(@NonNull Rect bounds) {
            int widthUsed = 0;
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    int left = bounds.left + widthUsed;
                    int top = bounds.centerY() - drawable.getIntrinsicHeight() / 2;
                    int right = left + drawable.getIntrinsicWidth();
                    int bottom = top + drawable.getIntrinsicHeight();
                    drawable.setBounds(left, top, right, bottom);
                    widthUsed += drawable.getIntrinsicWidth();
                    widthUsed += iconPadding;
                }
            }
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            setBounds(new Rect(left, top, right, bottom));
        }

        @Override
        public int getIntrinsicWidth() {
            int width = 0;
            int validDrawableCount = 0;
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    width += drawable.getIntrinsicWidth();
                    validDrawableCount++;
                }
            }
            width += (validDrawableCount == 0 ? 0 : (validDrawableCount - 1) * iconPadding);
            return width;
        }

        @Override
        public int getIntrinsicHeight() {
            int height = 0;
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    height = Math.max(height, drawable.getIntrinsicHeight());
                }
            }
            return height;
        }

        @Override
        public void setAlpha(int i) {
            for (Drawable drawable : drawables) {
                if (drawable != null)
                    drawable.setAlpha(i);
            }
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            for (Drawable drawable : drawables) {
                if (drawable != null)
                    drawable.setColorFilter(colorFilter);
            }
        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    }
}
