package com.zzw.guanglan.ui.guangland.param;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.GuangLanParamBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zzw on 2018/10/14.
 * 描述:
 */
public class GuangLanParamActivity extends BaseActivity implements
        TextView.OnEditorActionListener,
        BaseQuickAdapter.RequestLoadMoreListener,
        BaseQuickAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.et_param)
    EditText etParam;
    @BindView(R.id.recy)
    RecyclerView recy;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private int pageNo = 1;
    private GuangLanParamListAdapter adapter;


    public static void open(Activity activity, int code) {
        activity.startActivityForResult(
                new Intent(activity, GuangLanParamActivity.class), code);
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_guang_lan_param_list;
    }

    @Override
    protected void initView() {
        super.initView();

        etParam.setOnEditorActionListener(this);

        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuangLanParamListAdapter(new ArrayList<GuangLanParamBean>());
        adapter.setOnItemClickListener(this);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        recy.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);

        onRefresh();
    }

    @OnClick(R.id.search)
    public void onViewClicked() {
        search();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) { // 当按了搜索之后关闭软键盘
            ((InputMethodManager) etParam.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus()
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            search();
            return true;
        }
        return false;
    }

    void search() {
        String key = etParam.getText().toString().trim();

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .searchParam(key, String.valueOf(pageNo))
                .compose(LifeObservableTransformer.<ListDataBean<GuangLanParamBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<GuangLanParamBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<GuangLanParamBean> guanLanItemBeans) {
                        setData(guanLanItemBeans.getList());
                        if (adapter.getData().size() >= guanLanItemBeans.getTotal()) {
                            adapter.loadMoreEnd();
                        } else {
                            adapter.loadMoreComplete();
                        }
                    }
                });
    }

    void setData(List<GuangLanParamBean> datas) {
        if (pageNo == 1) {
            adapter.replaceData(datas);
            refreshLayout.setRefreshing(false);
        } else {
            adapter.addData(datas);
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        setResult(Activity.RESULT_OK,
                new Intent().putExtra("bean", (GuangLanParamBean) adapter.getData().get(position)));
        finish();
    }

    @Override
    public void onLoadMoreRequested() {
        pageNo++;
        search();
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        search();
    }
}
