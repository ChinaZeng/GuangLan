package com.zzw.guanglan.ui.resource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.AreaBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.ResBean;
import com.zzw.guanglan.dialogs.area.AreaDialog;
import com.zzw.guanglan.dialogs.multilevel.OnConfirmCallback;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.location.LocationManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.ui.guangland.GuangLanDListActivity;
import com.zzw.guanglan.ui.room.EngineRoomDetailsActivity;
import com.zzw.guanglan.utils.InputMethodSoftUtil;
import com.zzw.guanglan.utils.PopWindowUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Create by zzw on 2018/12/7
 */
public class ResourceSearchActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener, TextView.OnEditorActionListener, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.et_param)
    EditText etParam;
    @BindView(R.id.recy)
    RecyclerView recy;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private ResourceAdapter adapter;
    private LocationManager.LocationBean location;


    public static void open(Context context, LocationManager.LocationBean location) {
        Intent intent = new Intent(context, ResourceSearchActivity.class);
        intent.putExtra("location", location);
        context.startActivity(intent);
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_res_search;
    }

    @Override
    protected void initView() {
        super.initView();
        location = (LocationManager.LocationBean) getIntent().getSerializableExtra("location");

        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResourceAdapter(new ArrayList<ResBean>());
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        recy.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ResBean resBean = (ResBean) adapter.getData().get(position);
//                EngineRoomDetailsActivity.open(ResourceSearchActivity.this, (ResBean) adapter.getData().get(position));
                GuangLanDListActivity.open(ResourceSearchActivity.this,resBean.getRoomId(),location);

            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);

        etParam.setOnEditorActionListener(this);
    }


    @Override
    protected void initData() {
        super.initData();
        onRefresh();
    }

    @OnClick({R.id.tv_area, R.id.search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_area:
                area();
                break;
            case R.id.search:
                hideKeyWordSearch();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyWordSearch();
            return true;
        }
        return false;
    }

    void hideKeyWordSearch() {
        InputMethodSoftUtil.hideSoftInput(etParam);
        onRefresh();
    }

    private String cityName, areaName;
    private int pageNum = 1;

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
                .getAppJfByOthers(etParam.getText().toString().trim(),
                        cityName,
                        areaName, pageNum)
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

    private void area() {
        AreaDialog.createCityDialog(this, "选择地区", new OnConfirmCallback<AreaBean>() {
            @Override
            public void onConfirm(List<AreaBean> selectedEntities) {
                if (selectedEntities.size() > 2) {
                    AreaBean area = selectedEntities.get(selectedEntities.size() - 1);
                    AreaBean city = selectedEntities.get(selectedEntities.size() - 2);

                    cityName = city.getText();
                    areaName = area.getText();

                    tvArea.setText(cityName + areaName);

                    hideKeyWordSearch();
                }
            }
        }).show(getSupportFragmentManager(), "area");
    }

    @Override
    public void onLoadMoreRequested() {
        pageNum++;
        getData();
    }
}
