package com.zzw.guanglan.ui.guangland;

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
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.socket.EventBusTag;
import com.zzw.guanglan.utils.InputMethodSoftUtil;
import com.zzw.guanglan.utils.RequestBodyUtils;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class GuangLanSearchActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener,
        TextView.OnEditorActionListener {
    @BindView(R.id.et_param)
    EditText etParam;
    @BindView(R.id.recy)
    RecyclerView recy;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;


    private GuangLanDListAdapter adapter;
    private String searchKey;

    private int pageNo = 1;

    public static void open(Context context) {
        context.startActivity(new Intent(context, GuangLanSearchActivity.class));
    }


    @Override
    protected int initLayoutId() {
        return R.layout.activity_search_guang_lan_d_list;
    }

    @Override
    protected void initData() {
        super.initData();
        etParam.setOnEditorActionListener(this);

        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuangLanDListAdapter(new ArrayList<GuangLanDItemBean>());
        adapter.setOnItemClickListener(this);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        recy.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);

        onRefresh();
    }

    @OnClick(R.id.search)
    public void onViewClicked() {
        hideKeyWordSearch();
    }

    void hideKeyWordSearch() {
        // 当按了搜索之后关闭软键盘
        InputMethodSoftUtil.hideSoftInput(etParam);
        String searchKey = etParam.getText().toString().trim();

        pageNo = 1;
        refreshLayout.setRefreshing(true);

        search(searchKey, pageNo);

    }


    private void search(String key, final int pageNo) {
        this.searchKey = key;

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppListDuanByPage(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("model.cabelOpName", searchKey);
                        put("pageNum", String.valueOf(pageNo));
                    }
                }))
                .compose(LifeObservableTransformer.<ListDataBean<GuangLanDItemBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<GuangLanDItemBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<GuangLanDItemBean> guanLanItemBeans) {
                        if (guanLanItemBeans != null && guanLanItemBeans.getList() != null) {
                            setData(guanLanItemBeans.getList());
                            if (adapter.getData().size() >= guanLanItemBeans.getTotal()) {
                                adapter.loadMoreEnd();
                            } else {
                                adapter.loadMoreComplete();
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyWordSearch();
            return true;
        }
        return false;
    }

    void setData(List<GuangLanDItemBean> datas) {
        if (pageNo == 1) {
            adapter.replaceData(datas);
            refreshLayout.setRefreshing(false);
        } else {
            adapter.addData(datas);
        }
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        search(searchKey, pageNo);
    }


    public static final String TAG_GUANG_LAN_D_NAME = "TAG_GUANG_LAN_D_NAME";

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        GuangLanDItemBean bean = (GuangLanDItemBean) adapter.getData().get(position);
        setResult(Activity.RESULT_OK, new Intent().putExtra("data", bean));
        EventBus.getDefault().post(bean, TAG_GUANG_LAN_D_NAME);
        finish();
    }


    @Override
    public void onLoadMoreRequested() {
        pageNo++;
        search(searchKey, pageNo);
    }
}
