package com.zzw.guanglan.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.guanglan.R;

import java.util.ArrayList;
import java.util.List;


public class BottomListDialog<T> extends BottomSheetDialogFragment {

    List<T> datas = new ArrayList<>();

    Convert<T> convert;
    Callback<T> callback;

    private String title;

    public static <T> BottomListDialog<T> newInstance(List<T> datas, Convert<T> convert) {
        return new BottomListDialog<T>().setDatas(datas, convert);
    }

    public BottomListDialog<T> setDatas(List<T> datas, Convert<T> convert) {
        this.datas = datas;
        this.convert = convert;
        return this;
    }

    public BottomListDialog<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public BottomListDialog<T> setCallback(Callback<T> callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.BottomListDialogTheme);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_bottom_list, container, false);
        RecyclerView rvList = view.findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(new BottomListAdapter(datas));
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        return view;
    }

    private class BottomListAdapter extends BaseQuickAdapter<T, BaseViewHolder> {
        public BottomListAdapter(@Nullable List<T> data) {
            super(android.R.layout.simple_list_item_1, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final T item) {
            helper.setText(android.R.id.text1, convert.convert(item));
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        boolean isCompleted = callback.onSelected(item,helper.getAdapterPosition());
                        if (isCompleted) {
                            dismiss();
                        }
                    }
                }
            });
        }
    }

    public interface Convert<T> {
        String convert(T data);
    }

    public interface Callback<T> {
        boolean onSelected(T data, int position);
    }
}
