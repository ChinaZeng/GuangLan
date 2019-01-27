package com.zzw.guanglan.dialogs.multilevel;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.zzw.guanglan.R;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;


public class MultilLevelDialog <T extends INamedEntity>extends DialogFragment {
    private ViewPager viewPager;
    private TextView tvTitle,tvLeft,tvRight;
    private FixPagerSlidingTabStrip pagerTitleStrip;
    private IDataSet dataset;
    private String title;
    private boolean showAction;
    private MultiLevelPagerAdapter pagerAdapter;
    private OnConfirmCallback onConfirmCallback;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private OnConfirmCallback confirmCallback = new OnConfirmCallback<T>() {
        @SuppressWarnings("unchecked")
        @Override
        public void onConfirm(List<T> selectedEntities) {
            if (onConfirmCallback != null) {
                onConfirmCallback.onConfirm(selectedEntities);
            }
            dismiss();
        }
    };

    public static MultilLevelDialog newInstance(IDataSet dataset, String title) {
        return newInstance(dataset,title,false);
    }

    public static MultilLevelDialog newInstance(IDataSet dataset, String title,boolean showAction) {
        return new MultilLevelDialog().setTitle(title).setDataset(dataset).setShowAction(showAction);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME,android.R.style.Theme_Dialog);
        if(this.dataset == null){
            dismiss();
        }
    }

    private MultilLevelDialog setDataset(IDataSet dataset) {
        this.dataset = dataset;
        return this;
    }

    private MultilLevelDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    private MultilLevelDialog setShowAction(boolean showAction) {
        this.showAction = showAction;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_multilevel, container, false);
        viewPager = view.findViewById(R.id.view_pager);
        tvTitle = view.findViewById(R.id.tv_title);
        tvLeft = view.findViewById(R.id.tv_left);
        tvRight = view.findViewById(R.id.tv_right);
        pagerTitleStrip = view.findViewById(R.id.pst_titles);
        pagerTitleStrip.setTypeface(null, Typeface.NORMAL);
        init();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if(window != null){
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = getResources().getDisplayMetrics().heightPixels * 2 / 3;
            attributes.gravity = Gravity.BOTTOM;
            window.setAttributes(attributes);
        }
    }

    private void init() {
        tvTitle.setText(title);
        if(showAction){
            tvLeft.setVisibility(View.VISIBLE);
            tvRight.setVisibility(View.VISIBLE);

            tvLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pagerAdapter!=null){
                        //触发选择回调
                        pagerAdapter.onSelectedLastLevel();
                    }
                }
            });

        }else {
            tvLeft.setVisibility(View.GONE);
            tvRight.setVisibility(View.GONE);
        }

        pagerAdapter = new MultiLevelPagerAdapter(compositeDisposable, dataset, viewPager, pagerTitleStrip);
        pagerAdapter.setOnConfirmCallback(confirmCallback);
        viewPager.setAdapter(pagerAdapter);
        pagerTitleStrip.setViewPager(viewPager);
    }

    public MultilLevelDialog setConfirmCallback(OnConfirmCallback callback) {
        this.onConfirmCallback = callback;
        return this;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();
    }
}
