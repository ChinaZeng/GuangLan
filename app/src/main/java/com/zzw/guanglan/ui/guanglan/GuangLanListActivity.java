package com.zzw.guanglan.ui.guanglan;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.GuangLanItemBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.ui.guanglan.add.GuangLanAddActivitty;
import com.zzw.guanglan.ui.guangland.GuangLanDListActivity;
import com.zzw.guanglan.utils.RequestBodyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class GuangLanListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recy)
    RecyclerView recy;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private GuangLanListAdapter adapter;


    private final static int PAGE_SIZE = 10;
    private int pageNo = 1;

    public static void open(Context context) {
        context.startActivity(new Intent(context, GuangLanListActivity.class));
    }


    @Override
    protected int initLayoutId() {
        return R.layout.activity_guang_lan_d_list;
    }

    @Override
    protected void initData() {
        super.initData();
        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuangLanListAdapter(new ArrayList<GuangLanItemBean>());
        adapter.setOnItemClickListener(this);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        recy.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);

        onRefresh();
    }

    void getData() {

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getGuangLanByPage(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("model.cableNo", "");
                        put("model.cableName", "");
                        put("pageNum", String.valueOf(pageNo));
                    }
                }))
                .compose(LifeObservableTransformer.<ListDataBean<GuangLanItemBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<GuangLanItemBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<GuangLanItemBean> GuangLanItemBeans) {
                        if (GuangLanItemBeans != null && GuangLanItemBeans.getList() != null) {
                            setData(GuangLanItemBeans.getList());
                            if (adapter.getData().size() >= GuangLanItemBeans.getTotal()) {
                                adapter.loadMoreEnd();
                            } else {
                                adapter.loadMoreComplete();
                            }
                        }
                    }
                });
    }

    void setData(List<GuangLanItemBean> datas) {
        if (pageNo == 1) {
            adapter.replaceData(datas);
            refreshLayout.setRefreshing(false);
        } else {
            adapter.addData(datas);
        }

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        GuangLanDListActivity.open(this);
    }


    @OnClick(R.id.add)
    public void onViewClicked() {
        GuangLanAddActivitty.open(this);
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
