package com.zzw.guanglan.ui.guangland;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.GuanLanItemBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.ui.guangland.add.GuangLanDAddActivitty;
import com.zzw.guanglan.ui.qianxin.QianXinListActivity;
import com.zzw.guanglan.utils.RequestBodyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class GuangLanDListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recy)
    RecyclerView recy;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private GuangLanDListAdapter adapter;


    private final static int PAGE_SIZE = 10;
    private int pageNo = 1;

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
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        recy.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);

        onRefresh();
    }

    void getData() {

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppListDuanByPage(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("model.cabelOpCode", "1");
                        put("model.cabelOpName", "1");
                        put("pageSize", String.valueOf(PAGE_SIZE));
                        put("pageNo", String.valueOf(pageNo));
                    }
                }))
                .compose(LifeObservableTransformer.<ListDataBean<GuanLanItemBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<GuanLanItemBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<GuanLanItemBean> guanLanItemBeans) {
                        if (guanLanItemBeans != null && guanLanItemBeans.getList() != null) {
                            setData(guanLanItemBeans.getList());
                            if (adapter.getData().size() >= guanLanItemBeans.getTotal()) {
                                adapter.loadMoreEnd();
                            }else {
                                adapter.loadMoreComplete();
                            }
                        }
                    }
                });
    }

    void setData(List<GuanLanItemBean> datas) {
        if (pageNo == 1) {
            adapter.replaceData(datas);
            refreshLayout.setRefreshing(false);
        } else {
            adapter.addData(datas);
        }

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        QianXinListActivity.open(this, (GuanLanItemBean) adapter.getData().get(position));
    }


    @OnClick(R.id.add)
    public void onViewClicked() {
        GuangLanDAddActivitty.open(this);
    }

    @Override
    public void onLoadMoreRequested() {
        pageNo++;
        getData();
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        getData();
    }
}
