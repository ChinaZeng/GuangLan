package com.zzw.guanglan.ui.gongdan;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.GongDanBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.manager.UserManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.rx.ResultBooleanFunction;
import com.zzw.guanglan.service.SocketService;
import com.zzw.guanglan.ui.HotConnActivity;
import com.zzw.guanglan.ui.qianxin.QianXinListActivity;
import com.zzw.guanglan.utils.InputMethodSoftUtil;
import com.zzw.guanglan.utils.SPUtil;
import com.zzw.guanglan.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zzw on 2019/4/27.
 * 描述:
 */
public class GongDanListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, TextView.OnEditorActionListener {
    @BindView(R.id.et_param)
    EditText etParam;
    @BindView(R.id.recy)
    RecyclerView recy;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private GongDanListAdapter adapter;

    private int pageNo = 1;
    private String searchKey;


    public static void open(Context context) {
        context.startActivity(new Intent(context, GongDanListActivity.class));
    }


    @Override
    protected int initLayoutId() {
        return R.layout.activity_gongdan_list;
    }

    @Override
    protected void initView() {
        super.initView();
        etParam.setOnEditorActionListener(this);
        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GongDanListAdapter(new ArrayList<GongDanBean>());
        adapter.setOnItemClickListener(this);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        recy.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);

        onRefresh();
    }


    void getData() {

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppWorkOrderByPage(UserManager.getInstance().getUserId(),
                        UserManager.getInstance().getAreaId(),
                        String.valueOf(pageNo), searchKey)
                .compose(LifeObservableTransformer.<ListDataBean<GongDanBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<GongDanBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<GongDanBean> data) {
                        if (data != null && data.getList() != null) {
                            setData(data.getList());
                            if (adapter.getData().size() >= data.getTotal()) {
                                adapter.loadMoreEnd();
                            } else {
                                adapter.loadMoreComplete();
                            }
                        }
                    }
                });
    }

    void setData(List<GongDanBean> datas) {
        if (pageNo == 1) {
            adapter.replaceData(datas);
            refreshLayout.setRefreshing(false);
        } else {
            adapter.addData(datas);
        }

    }

    @OnClick(R.id.search)
    public void onViewClicked() {
        hideKeyWordFresh();
    }

    void hideKeyWordFresh() {
        searchKey = etParam.getText().toString().trim();
        InputMethodSoftUtil.hideSoftInput(etParam);
        refreshLayout.setRefreshing(true);
        onRefresh();
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

    @Override
    public void onItemClick(final BaseQuickAdapter adapter, View view, final int position) {

        if (SocketService.getDeviceNum() == null) {
            ToastUtils.showToast("设备号没有获取，请先获取设备号");
            HotConnActivity.open(this);
            return;
        }
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .checkSerial(SocketService.getDeviceNum())
                .map(ResultBooleanFunction.create())
                .compose(LifeObservableTransformer.<Boolean>create(this))
                .subscribe(new ErrorObserver<Boolean>(this) {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            GongDanBean bean = (GongDanBean) adapter.getData().get(position);
                            if (bean.getGUANG_LAN_DUAN() != null) {
                                QianXinListActivity.open(GongDanListActivity.this, bean.getGUANG_LAN_DUAN().get(0));
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyWordFresh();
            return true;
        }
        return false;
    }
}
