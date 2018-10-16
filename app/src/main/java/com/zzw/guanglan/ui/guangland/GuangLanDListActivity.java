package com.zzw.guanglan.ui.guangland;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dl7.tag.TagLayout;
import com.dl7.tag.TagView;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.SingleChooseBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.socket.event.TestArgsAndStartBean;
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

    private List<SingleChooseBean> juliS;
    private List<SingleChooseBean> bochangS;
    private List<SingleChooseBean> maikuanS;
    private List<SingleChooseBean> timeS;
    private List<SingleChooseBean> zheshelvS;
    private List<SingleChooseBean> modeS;

    private TestArgsAndStartBean testBean;

    private final static int PAGE_SIZE = 10;
    private int pageNo = 1;

    public static void open(Context context) {
        context.startActivity(new Intent(context, GuangLanDListActivity.class));
    }


    void initArgsData() {
        testBean = new TestArgsAndStartBean();
        juliS = new ArrayList<>();

        juliS.add(new SingleChooseBean(0, "300m", 300));
        juliS.add(new SingleChooseBean(1, "1km", 1000));
        juliS.add(new SingleChooseBean(2, "5km", 5000));
        juliS.add(new SingleChooseBean(3, "10km", 10000));
        juliS.add(new SingleChooseBean(4, "30km", 30000));
        juliS.add(new SingleChooseBean(5, "60km", 60000));
        juliS.add(new SingleChooseBean(6, "100km", 100000));
        juliS.add(new SingleChooseBean(7, "180km", 180000));
        testBean.rang = juliS.get(0).getValue();

        bochangS = new ArrayList<>();
        bochangS.add(new SingleChooseBean(0, "1550nm", 1550));
        testBean.wl = bochangS.get(0).getValue();

        maikuanS = new ArrayList<>();
        maikuanS.add(new SingleChooseBean(0, "10ns", 10));
        maikuanS.add(new SingleChooseBean(1, "20ns", 20));
        maikuanS.add(new SingleChooseBean(2, "30ns", 30));
        maikuanS.add(new SingleChooseBean(3, "40ns", 40));
        maikuanS.add(new SingleChooseBean(4, "80ns", 80));
        maikuanS.add(new SingleChooseBean(5, "160ns", 160));
        maikuanS.add(new SingleChooseBean(6, "640ns", 640));
        maikuanS.add(new SingleChooseBean(7, "2.56us", 2560));
        testBean.pw = maikuanS.get(0).getValue();


        timeS = new ArrayList<>();
        timeS.add(new SingleChooseBean(0, "10s", 10));
        timeS.add(new SingleChooseBean(1, "15s", 15));
        timeS.add(new SingleChooseBean(2, "30s", 30));
        timeS.add(new SingleChooseBean(3, "1min", 60));
        testBean.time = timeS.get(0).getValue();

        modeS = new ArrayList<>();
        modeS.add(new SingleChooseBean(0, "平均", 1));
        modeS.add(new SingleChooseBean(1, "实时", 2));
        testBean.mode = modeS.get(0).getValue();

        zheshelvS = new ArrayList<>();
        zheshelvS.add(new SingleChooseBean(0, "146850", 146850));
        testBean.gi = zheshelvS.get(0).getValue();
    }


    private TagLayout juli, bochang, maikuan, time, zheshelv, mode;

    View headerView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_header, recy, false);
        final View content = view.findViewById(R.id.content);
        final TextView head_click = view.findViewById(R.id.head_click);
        view.findViewById(R.id.head_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.setVisibility(content.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                if (content.getVisibility() == View.VISIBLE) {
                    head_click.setText("点此收起参数配置");
                } else {
                    head_click.setText("点此展开参数配置");
                }
            }
        });
        juli = view.findViewById(R.id.juli);
        for (SingleChooseBean singleChooseBean : juliS) {
            juli.addTag(singleChooseBean.getName());
        }
        juli.setCheckTag(0);
        juli.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testBean.rang = juliS.get(i).getValue();
                }
            }
        });

        bochang = view.findViewById(R.id.bochang);
        for (SingleChooseBean singleChooseBean : bochangS) {
            bochang.addTag(singleChooseBean.getName());
        }
        bochang.setCheckTag(0);
        bochang.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testBean.wl = bochangS.get(i).getValue();
                }
            }
        });

        maikuan = view.findViewById(R.id.maikuan);
        for (SingleChooseBean singleChooseBean : maikuanS) {
            maikuan.addTag(singleChooseBean.getName());
        }
        maikuan.setCheckTag(0);
        maikuan.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testBean.pw = maikuanS.get(i).getValue();
                }
            }
        });

        time = view.findViewById(R.id.time);
        for (SingleChooseBean singleChooseBean : timeS) {
            time.addTag(singleChooseBean.getName());
        }
        time.setCheckTag(0);
        time.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testBean.time = timeS.get(i).getValue();
                }
            }
        });

        mode = view.findViewById(R.id.mode);
        for (SingleChooseBean singleChooseBean : modeS) {
            mode.addTag(singleChooseBean.getName());
        }
        mode.setCheckTag(0);
        mode.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testBean.mode = modeS.get(i).getValue();
                }
            }
        });

        zheshelv = view.findViewById(R.id.zheshelv);
        for (SingleChooseBean singleChooseBean : zheshelvS) {
            zheshelv.addTag(singleChooseBean.getName());
        }
        zheshelv.setCheckTag(0);
        zheshelv.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testBean.gi = zheshelvS.get(i).getValue();
                }
            }
        });
        return view;
    }

    void checkInit() {
        for (int i = 0; i < juliS.size(); i++) {
            if (testBean.rang == juliS.get(i).getValue()) {
                juli.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < bochangS.size(); i++) {
            if (testBean.wl == bochangS.get(i).getValue()) {
                bochang.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < maikuanS.size(); i++) {
            if (testBean.pw == maikuanS.get(i).getValue()) {
                maikuan.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < timeS.size(); i++) {
            if (testBean.time == timeS.get(i).getValue()) {
                time.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < modeS.size(); i++) {
            if (testBean.mode == modeS.get(i).getValue()) {
                mode.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < zheshelvS.size(); i++) {
            if (testBean.gi == zheshelvS.get(i).getValue()) {
                zheshelv.setCheckTag(i);
                break;
            }
        }
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_guang_lan_d_list;
    }

    @Override
    protected void initData() {
        super.initData();
        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuangLanDListAdapter(new ArrayList<GuangLanDItemBean>());
        adapter.setOnItemClickListener(this);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        recy.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);

        initArgsData();
        adapter.addHeaderView(headerView());

        onRefresh();
    }

    void getData() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppListDuanByPage(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("model.cabelOpCode", "");
                        put("model.cabelOpName", "");
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

    void setData(List<GuangLanDItemBean> datas) {
        if (pageNo == 1) {
            adapter.replaceData(datas);
            refreshLayout.setRefreshing(false);
        } else {
            adapter.addData(datas);
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        QianXinListActivity.open(this, (GuangLanDItemBean) adapter.getData().get(position), testBean);
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
