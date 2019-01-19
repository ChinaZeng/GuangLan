package com.zzw.guanglan.ui.resource;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.ResBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.location.LocationManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.ui.guangland.GuangLanDListActivity;
import com.zzw.guanglan.ui.room.EngineRoomDetailsActivity;
import com.zzw.guanglan.utils.PopWindowUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by zzw on 2018/12/24
 * 附近资源
 */
public class NearbyResActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.RequestLoadMoreListener {
    @BindView(R.id.tv_distance)
    TextView tvDistance;

    @BindView(R.id.recy)
    RecyclerView recy;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private ResourceAdapter adapter;
    private LocationManager.LocationBean location;

    private int distance = 1;
    private int pageNum = 1;

    public static void open(Context context, LocationManager.LocationBean location) {
        Intent intent = new Intent(context, NearbyResActivity.class);
        intent.putExtra("location", location);
        context.startActivity(intent);
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_nearby_res;
    }

    @Override
    protected void initView() {
        super.initView();
        location = (LocationManager.LocationBean) getIntent().getSerializableExtra("location");
        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResourceAdapter(true);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        recy.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ResBean resBean = (ResBean) adapter.getData().get(position);
//                EngineRoomDetailsActivity.open(NearbyResActivity.this, resBean);
                GuangLanDListActivity.open(NearbyResActivity.this,resBean.getRoomId(),location);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    protected void initData() {
        super.initData();
        onRefresh();
    }

    @OnClick({R.id.tv_distance})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_distance:
                PopWindowUtils.showListPop(this, view, new String[]{"1km", "2km", "3km", "4km"}, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        distance = position + 1;
                        tvDistance.setText(String.format("距离(%s千米)", String.valueOf(distance)));
                        onRefresh();
                    }
                });
                break;

        }
    }


    @Override
    public void onRefresh() {
        pageNum = 1;
        getData();
    }


    void getData() {
        if (pageNum == 1) {
            swipeRefreshLayout.setRefreshing(true);
        }
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppJfInfo(
                        String.valueOf(location.longitude),
                        String.valueOf(location.latitude),
                        String.valueOf(String.valueOf(distance)), String.valueOf(pageNum))
                .compose(LifeObservableTransformer.<ListDataBean<ResBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<ResBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<ResBean> listDataBean) {
                        List<ResBean> list = listDataBean.getList();
                        setData(list);

                        if (adapter.getData().size() >= listDataBean.getTotal()) {
                            adapter.loadMoreEnd();
                        } else {
                            adapter.loadMoreComplete();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }


    void setData(List<ResBean> list) {
        if (pageNum == 1) {
            adapter.replaceData(list);
        } else {
            adapter.addData(list);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        pageNum++;
        getData();
    }
}
