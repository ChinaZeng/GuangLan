package com.zzw.guanglan.ui.resource;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.zzw.guanglan.manager.LocationManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.ui.room.EngineRoomDetailsActivity;
import com.zzw.guanglan.utils.PopWindowUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by zzw on 2018/12/24
 * 附近资源
 */
public class NearbyResActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_engine_room)
    TextView tvEngineRoom;
    @BindView(R.id.recy)
    RecyclerView recy;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private ResourceAdapter adapter;
    private LocationManager.LocationBean location;

    private String[] types = new String[]{"机房", "光交"};
    private String type = types[0];
    private int distance = 1;


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
        adapter = new ResourceAdapter(new ArrayList<ResBean>());
        recy.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                EngineRoomDetailsActivity.open(NearbyResActivity.this, (ResBean) adapter.getData().get(position));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    protected void initData() {
        super.initData();
        onRefresh();
    }

    @OnClick({R.id.tv_distance, R.id.tv_engine_room})
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
            case R.id.tv_engine_room:
                PopWindowUtils.showListPop(this, view, types, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        type = types[position];
                        tvEngineRoom.setText(type);
                        onRefresh();
                    }
                });
                break;
        }
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppJfOrGlByType(type,
                        String.valueOf(location.longitude),
                        String.valueOf(location.latitude),
                        String.valueOf(String.valueOf(distance)))
                .compose(LifeObservableTransformer.<ListDataBean<ResBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<ResBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<ResBean> listDataBean) {
                        adapter.replaceData(listDataBean.getList());
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                });
    }

}
