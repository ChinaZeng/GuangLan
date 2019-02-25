package com.zzw.guanglan.ui.qianxin;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.guanglan.R;
import com.zzw.guanglan.bean.QianXinItemBean;

import java.util.HashSet;
import java.util.List;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class QianXinListAdapter extends BaseQuickAdapter<QianXinItemBean, BaseViewHolder> {
    private HashSet<String> arginData;
    private HashSet<String> exceptionData;

    public QianXinListAdapter(@Nullable List<QianXinItemBean> data) {
        super(R.layout.item_qianxin, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final QianXinItemBean item) {
        helper.setText(R.id.guang_lan_d_name, "光缆序号:" + item.getNO());
        helper.setText(R.id.status, "纤芯状态: " + item.getSTATENAME());

        helper.setText(R.id.last_time, "上次测试时间: " + (TextUtils.isEmpty(item.getMODIFY_DATE_STR())
                ? "无" : item.getMODIFY_DATE_STR()));

        if (TextUtils.isEmpty(item.getTestLocalFilePath())) {
            helper.setVisible(R.id.test_ok, false);
        } else {
            helper.setVisible(R.id.test_ok, true);
        }

        helper.setVisible(R.id.upload_ok, item.isUpload());

        helper.setOnClickListener(R.id.qianxin_test, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTestListener != null) {
                    onTestListener.onTest(item);
                }
            }
        });


        helper.setOnClickListener(R.id.qianxin_upload, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUploadListener != null) {
                    onUploadListener.onUpload(item);
                }
            }
        });

        helper.setOnClickListener(R.id.qianxin_status, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onStatusListener != null) {
                    onStatusListener.onStatus(item);
                }
            }
        });

        View itemRoot = helper.getView(R.id.item_root);

        int backColor = Color.WHITE;
        if (arginData != null && arginData.contains(item.getFIBER_ID())) {
            backColor = Color.argb(255, 12, 214, 211);
        }
        if (exceptionData != null && exceptionData.contains(item.getFIBER_ID())) {
            backColor = Color.argb(125, 255, 0, 0);
        }
        itemRoot.setBackgroundColor(backColor);

    }

    private OnTestListener onTestListener;
    private OnUploadListener onUploadListener;
    private OnStatusListener onStatusListener;

    public void setOnStatusListener(OnStatusListener onStatusListener) {
        this.onStatusListener = onStatusListener;
    }

    public void setOnUploadListener(OnUploadListener onUploadListener) {
        this.onUploadListener = onUploadListener;
    }

    public void setOnTestListener(OnTestListener onTestListener) {
        this.onTestListener = onTestListener;
    }

    public QianXinListAdapter setExceptionData(HashSet<String> exceptionData) {
        this.exceptionData = exceptionData;
        return this;
    }

    public void setArginData(HashSet<String> arginData) {
        this.arginData = arginData;
    }


    public interface OnTestListener {
        void onTest(QianXinItemBean bean);
    }


    public interface OnUploadListener {
        void onUpload(QianXinItemBean bean);
    }

    public interface OnStatusListener {
        void onStatus(QianXinItemBean bean);
    }


}
