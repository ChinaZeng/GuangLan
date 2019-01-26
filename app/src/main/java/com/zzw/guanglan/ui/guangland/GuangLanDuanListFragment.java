package com.zzw.guanglan.ui.guangland;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dl7.tag.TagLayout;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseFragment;
import com.zzw.guanglan.bean.GradeBean;
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.SingleChooseBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.location.LocationManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.ui.guanglan.add.GuangLanAddActivitty;
import com.zzw.guanglan.ui.guangland.add.GuangLanDAddActivitty;
import com.zzw.guanglan.ui.qianxin.QianXinListActivity;
import com.zzw.guanglan.ui.resource.ResourceActivity;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class GuangLanDuanListFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recy)
    RecyclerView recy;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.root)
    View root;

    private GuangLanDListAdapter adapter;

    private String searchKey;
    private String searchJuli;
    private String searchJibie;

    private int pageNo = 1;

    private String roomId;
    private LocationManager.LocationBean locationBean;

    public static GuangLanDuanListFragment newInstance(String roomId, LocationManager.LocationBean location) {

        Bundle bundle = new Bundle();
        bundle.putString("roomId", roomId);
        bundle.putSerializable("location", location);

        GuangLanDuanListFragment guangLanDuanListFragment = new GuangLanDuanListFragment();
        guangLanDuanListFragment.setArguments(bundle);
        return guangLanDuanListFragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_guang_lan_d_list;
    }


    @Override
    protected void initData() {
        super.initData();

        roomId = getArguments().getString("roomId");
        locationBean = (LocationManager.LocationBean) getArguments().getSerializable("location");

        recy.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GuangLanDListAdapter(new ArrayList<GuangLanDItemBean>());
        adapter.setOnItemClickListener(this);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        recy.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);

        onRefresh();
    }

    private List<SingleChooseBean> juliS;
    private List<GradeBean> jibieS;

    private void initJuli() {
        juliS = new ArrayList<>();
        juliS.add(new SingleChooseBean(0, "300m", 0.3f));
        juliS.add(new SingleChooseBean(1, "1km", 1.0f));
        juliS.add(new SingleChooseBean(2, "2km", 2.0f));
        juliS.add(new SingleChooseBean(3, "5km", 5.0f));
//        juliS.add(new SingleChooseBean(3, "10km", 10000));
//        juliS.add(new SingleChooseBean(4, "30km", 30000));
//        juliS.add(new SingleChooseBean(5, "60km", 60000));
//        juliS.add(new SingleChooseBean(6, "100km", 100000));
//        juliS.add(new SingleChooseBean(7, "180km", 180000));

        juli.cleanTags();
        for (SingleChooseBean singleChooseBean : juliS) {
            juli.addTags(singleChooseBean.getName());
        }
    }

    private void initJibie() {

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .quertListInfo()
                .compose(LifeObservableTransformer.<List<GradeBean>>create(this))
                .subscribe(new ErrorObserver<List<GradeBean>>(this) {
                    @Override
                    public void onNext(final List<GradeBean> data) {
                        if (data == null) {
                            return;
                        }
                        jibieS = data;
                        jibie.cleanTags();
                        for (GradeBean datum : data) {
                            jibie.addTags(datum.getDescChina());
                        }
                    }
                });
    }


    void setData(List<GuangLanDItemBean> datas) {
        if (pageNo == 1) {
            adapter.replaceData(datas);
        } else {
            adapter.addData(datas);
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        QianXinListActivity.open(getContext(), (GuangLanDItemBean) adapter.getData().get(position));
    }


    private PopupWindow popupWindow;

    private TextView location, name, area;
    private TagLayout juli, jibie;

    private void showSel() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow();
            View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_pop_sel, null);

            location = v.findViewById(R.id.location);
            juli = v.findViewById(R.id.juli);
            jibie = v.findViewById(R.id.jibie);
            name = v.findViewById(R.id.name);
            area = v.findViewById(R.id.area);

            location.setText("定位地址: " + locationBean.addrss);

            v.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            v.findViewById(R.id.clean_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name.setText("");
                }
            });

            v.findViewById(R.id.choose_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GuangLanSearchActivity.open(getContext());
                }
            });
            v.findViewById(R.id.choose_area).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            v.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String juliStr = null;
                    if (juliS != null) {
                        List<String> juliselList = juli.getCheckedTags();
                        if (juliselList.size() > 0) {
                            String j = juliselList.get(0);
                            for (SingleChooseBean juli : juliS) {
                                if (TextUtils.equals(j, juli.getName())) {
                                    juliStr = String.valueOf(juli.getFloatValue());
                                }
                            }
                        }
                    }
                    String jibieStr = null;
                    if (jibieS != null) {
                        List<String> jibieList = jibie.getCheckedTags();
                        if (jibieList.size() > 0) {
                            String j = jibieList.get(0);
                            for (GradeBean jibie : jibieS) {
                                if (TextUtils.equals(j, jibie.getDescChina())) {
                                    jibieStr = jibie.getDescChina();
                                }
                            }
                        }
                    }

                    String nameStr = name.getText().toString().trim();
                    String areaStr = area.getText().toString().trim();

                    selection(nameStr, areaStr, juliStr, jibieStr);
                    popupWindow.dismiss();
                }
            });
            popupWindow.setAnimationStyle(R.style.PopRightEnterAnimStyle);
            popupWindow.setContentView(v);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setWidth((int) (recy.getWidth() - TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 40, getContext().getResources().getDisplayMetrics())));
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    animPop(false);
                }
            });

        }


        if (juliS == null || juliS.size() == 0) {
            initJuli();
        }

        if (jibieS == null || jibieS.size() == 0) {
            initJibie();
        }

        popupWindow.showAtLocation(root, Gravity.RIGHT, 0, 0);
        animPop(true);
    }


    private void animPop(boolean show) {

        float start = 1.0f;
        float end = 0.4f;

        if (!show) {
            start = 0.4f;
            end = 1.0f;
        }


        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(200);
        animator.setFloatValues();

        final WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        animator.setFloatValues(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                lp.alpha = alpha; //0.0-1.0
                getActivity().getWindow().setAttributes(lp);
            }
        });
        animator.start();
    }

    private void selection(String nameStr, String areaStr, String juliStr, String jibieStr) {

        searchJibie = jibieStr;
        searchJuli = juliStr;
        searchKey = nameStr;

        pageNo = 1;

        refreshLayout.setRefreshing(true);
        search();
    }


    @OnClick({R.id.add, R.id.sel,})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add:
                GuangLanAddActivitty.open(getContext());
//                GuangLanDAddActivitty.open(getContext());
                break;
            case R.id.sel:
                showSel();
                break;

        }
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


    void search() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .appGetCblCableOpByJf(new HashMap<String, String>() {
                    {
                        put("roomId", roomId);
                        put("longitude", String.valueOf(locationBean.longitude));
                        put("latitude", String.valueOf(locationBean.latitude));
                        put("distance", searchJuli == null ? "" : searchJuli);
                        put("descChina", searchJibie == null ? "" : searchJibie);
                        put("cabelName",searchKey==null?"": searchKey);
                        put("pageNum", String.valueOf(pageNo));
                    }
                })
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
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        refreshLayout.setRefreshing(false);
                    }
                });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = GuangLanSearchActivity.TAG_GUANG_LAN_D_NAME)
    public void name(GuangLanDItemBean bean) {
        if (bean != null && name != null) {
            name.setText(bean.getCABLE_NAME());
        }
    }


}
