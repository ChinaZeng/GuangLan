package com.zzw.socketdemo.ui.guanglan;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zzw.socketdemo.R;
import com.zzw.socketdemo.base.BaseActivity;
import com.zzw.socketdemo.bean.GuanLanItemBean;
import com.zzw.socketdemo.http.Api;
import com.zzw.socketdemo.http.retrofit.RetrofitHttpEngine;
import com.zzw.socketdemo.rx.ErrorObserver;
import com.zzw.socketdemo.rx.LifeObservableTransformer;
import com.zzw.socketdemo.rx.ResultRevFunction;
import com.zzw.socketdemo.ui.qianxin.QianXinListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class GuangLanListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.recy)
    RecyclerView recy;

    private GuangLanListAdapter adapter;


    public static void open(Context context) {
        context.startActivity(new Intent(context, GuangLanListActivity.class));
    }


    @Override
    protected int initLayoutId() {
        return R.layout.activity_guang_lan_list;
    }

    @Override
    protected void initData() {
        super.initData();
        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuangLanListAdapter(new ArrayList<GuanLanItemBean>());
        adapter.setOnItemClickListener(this);
        recy.setAdapter(adapter);

        getData();
    }

    void getData() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppListDuanByPage(new HashMap<String, String>() {
                    {
                        put("model.cabelOpCode:app", "1");
                        put("model.cabelOpName", "1");
                    }
                })
                .map(new ResultRevFunction<List<GuanLanItemBean>>())
                .compose(LifeObservableTransformer.<List<GuanLanItemBean>>create(this))
                .subscribe(new ErrorObserver<List<GuanLanItemBean>>(this) {
                    @Override
                    public void onNext(List<GuanLanItemBean> guanLanItemBeans) {
                        setData(guanLanItemBeans);
                    }
                });
    }

    void setData(List<GuanLanItemBean> datas) {
        adapter.replaceData(datas);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        QianXinListActivity.open(this);
    }
}
