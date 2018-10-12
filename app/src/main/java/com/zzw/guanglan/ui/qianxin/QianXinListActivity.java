package com.zzw.guanglan.ui.qianxin;

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
import com.zzw.guanglan.bean.QianXinItemBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.utils.RequestBodyUtils;
import com.zzw.guanglan.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class QianXinListActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recy)
    RecyclerView recy;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private final static int PAGE_SIZE = 10;
    private int pageNo = 1;

    private final static String ITEM = "item";


    private QianXinListAdapter adapter;
    private GuanLanItemBean bean;

    public static void open(Context context, GuanLanItemBean bean) {
        context.startActivity(new Intent(context, QianXinListActivity.class).putExtra(ITEM, bean));
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_qian_xin_list;
    }

    @Override
    protected void initData() {
        super.initData();
        bean = (GuanLanItemBean) getIntent().getSerializableExtra(ITEM);

        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QianXinListAdapter(new ArrayList<QianXinItemBean>());
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        recy.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this);

        onRefresh();
    }

    void getData() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppListByPage(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("model.cblOpName", bean.getCabelOpName());
                        put("model.cblOpCode", bean.getCabelOpCode());
                        put("pageSize", String.valueOf(PAGE_SIZE));
                        put("pageNo", String.valueOf(pageNo));
                    }
                }))
                .compose(LifeObservableTransformer.<ListDataBean<QianXinItemBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<QianXinItemBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<QianXinItemBean> qianXinItemBeans) {
                        if (qianXinItemBeans.getList() != null && qianXinItemBeans.getList().size() > 0) {
                            setData(qianXinItemBeans.getList());
                            if (adapter.getData().size() >= qianXinItemBeans.getTotal()) {
                                adapter.loadMoreEnd();
                            } else {
                                adapter.loadMoreComplete();
                            }
                        }
                    }
                });
    }


    void setData(List<QianXinItemBean> datas) {
        if (pageNo == 1) {
            adapter.replaceData(datas);
            refreshLayout.setRefreshing(false);
        } else {
            adapter.addData(datas);
        }
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
