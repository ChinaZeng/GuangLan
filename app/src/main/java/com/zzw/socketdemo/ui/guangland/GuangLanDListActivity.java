package com.zzw.socketdemo.ui.guangland;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zzw.socketdemo.R;
import com.zzw.socketdemo.base.BaseActivity;
import com.zzw.socketdemo.bean.GuanLanItemBean;
import com.zzw.socketdemo.bean.ListDataBean;
import com.zzw.socketdemo.http.Api;
import com.zzw.socketdemo.http.retrofit.RetrofitHttpEngine;
import com.zzw.socketdemo.rx.ErrorObserver;
import com.zzw.socketdemo.rx.LifeObservableTransformer;
import com.zzw.socketdemo.ui.MainActivity;
import com.zzw.socketdemo.ui.guangland.add.GuangLanDAddActivitty;
import com.zzw.socketdemo.ui.login.LoginActivity;
import com.zzw.socketdemo.ui.qianxin.QianXinListActivity;
import com.zzw.socketdemo.utils.RequestBodyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class GuangLanDListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.recy)
    RecyclerView recy;

    private GuangLanDListAdapter adapter;


    public static void open(Context context) {
        context.startActivity(new Intent(context, GuangLanDListActivity.class));
    }


    @Override
    protected int initLayoutId() {
        return R.layout.activity_guang_lan_d_list;
    }

    @Override
    protected void initData() {
        super.initData();
        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuangLanDListAdapter(new ArrayList<GuanLanItemBean>());
        adapter.setOnItemClickListener(this);
        recy.setAdapter(adapter);

        getData();
    }

    void getData() {

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppListDuanByPage(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("model.cabelOpCode", "1");
                        put("model.cabelOpName", "1");
                    }
                }))
                .compose(LifeObservableTransformer.<ListDataBean<GuanLanItemBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<GuanLanItemBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<GuanLanItemBean> guanLanItemBeans) {
                        if (guanLanItemBeans != null && guanLanItemBeans.getList() != null) {
                            setData(guanLanItemBeans.getList());
                        }
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


    @OnClick(R.id.add)
    public void onViewClicked() {
        GuangLanDAddActivitty.open(this);
    }
}
