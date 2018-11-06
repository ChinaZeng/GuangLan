package com.zzw.guanglan.ui.guangland;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dl7.tag.TagLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseFragment;
import com.zzw.guanglan.bean.GradeBean;
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.SingleChooseBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.manager.LocationManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.ui.guangland.add.GuangLanDAddActivitty;
import com.zzw.guanglan.ui.qianxin.QianXinListActivity;
import com.zzw.guanglan.utils.RequestBodyUtils;
import com.zzw.guanglan.utils.ToastUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;


public class GuangLanDuanListFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener,
        TextView.OnEditorActionListener, LocationManager.OnLocationListener {

    @BindView(R.id.recy)
    RecyclerView recy;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.et_param)
    EditText etParam;
    @BindView(R.id.tv_sel)
    TextView tvSel;
    @BindView(R.id.root)
    View root;


    private GuangLanDListAdapter adapter;
    private String searchKey;

    private String searchJuli;
    private String searchJibie;
    private String searchLontude;
    private String searchLatude;
    private int searchFlog = 0;

    private int tempFlog = 0;

    private int pageNo = 1;

    private LocationManager locationManager;


    public static GuangLanDuanListFragment newInstance() {
        return new GuangLanDuanListFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_guang_lan_d_list;
    }


    @Override
    protected void initData() {
        super.initData();

        etParam.setOnEditorActionListener(this);

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

    private void initLoca() {
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
            refreshLayout.setRefreshing(false);
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


            initLoca();
            startLocation();

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLocation();
                }
            });

            v.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
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
                    List<String> juliselList = juli.getCheckedTags();
                    String juliStr = null;
                    if (juliselList.size() > 0) {
                        String j = juliselList.get(0);
                        for (SingleChooseBean juli : juliS) {
                            if (TextUtils.equals(j, juli.getName())) {
                                juliStr = String.valueOf(juli.getFloatValue());
                            }
                        }
                    }

                    List<String> jibieList = jibie.getCheckedTags();
                    String jibieStr = null;
                    if (jibieList.size() > 0) {
                        String j = jibieList.get(0);
                        for (GradeBean jibie : jibieS) {
                            if (TextUtils.equals(j, jibie.getDescChina())) {
                                jibieStr = jibie.getDescChina();
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
                    TypedValue.COMPLEX_UNIT_DIP, 100, getContext().getResources().getDisplayMetrics())));
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    animPop(false);
                }
            });

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
        if (locationBean != null) {
            searchLontude = String.valueOf(locationBean.longitude);
            searchLatude = String.valueOf(locationBean.latitude);
        }

        searchJibie = jibieStr;
        searchJuli = juliStr;

        pageNo = 1;

        refreshLayout.setRefreshing(true);
        search(nameStr, searchFlog, pageNo);
    }


    @OnClick({R.id.add, R.id.sel, R.id.search, R.id.tv_sel,})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add:
                GuangLanDAddActivitty.open(getContext());
                break;
            case R.id.sel:
                showSel();
                break;
            case R.id.search:
                hideKeyWordSearch();
                break;
            case R.id.tv_sel:
                showListPopupWindow(tvSel);
                break;
        }
    }

    public void showListPopupWindow(View view) {
        final String items[] = {"光缆端编码", "光缆段名称"};
        final ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());

        // ListView适配器
        listPopupWindow.setAdapter(
                new ArrayAdapter<String>(getContext().getApplicationContext(), android.R.layout.simple_list_item_1, items));

        // 选择item的监听事件
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                tempFlog = pos;
                tvSel.setText(items[pos]);
                listPopupWindow.dismiss();
            }
        });

        // 对话框的宽高
        listPopupWindow.setWidth(500);
        listPopupWindow.setHeight(400);

        // ListPopupWindow的锚,弹出框的位置是相对当前View的位置
        listPopupWindow.setAnchorView(view);

        // ListPopupWindow 距锚view的距离
//        listPopupWindow.setHorizontalOffset(50);
//        listPopupWindow.setVerticalOffset(100);

        listPopupWindow.setModal(true);

        listPopupWindow.show();
    }


    @Override
    public void onLoadMoreRequested() {
        pageNo++;
        search(searchKey, searchFlog, pageNo);
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        search(searchKey, searchFlog, pageNo);
    }

    void hideKeyWordSearch() {
        // 当按了搜索之后关闭软键盘
        ((InputMethodManager) etParam.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        String searchKey = etParam.getText().toString().trim();

        pageNo = 1;
        refreshLayout.setRefreshing(true);

        search(searchKey, tempFlog, pageNo);

    }

    void search(final String key, final int flog, final int page) {
        searchKey = key;
        searchFlog = flog;

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppListDuanByPage(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("model.cabelOpCode", GuangLanDuanListFragment.this.searchFlog == 0 ? GuangLanDuanListFragment.this.searchKey : "");
                        put("model.cabelOpName", GuangLanDuanListFragment.this.searchFlog == 0 ? "" : GuangLanDuanListFragment.this.searchKey);
                        put("pageNum", String.valueOf(page));

                        put("model.dd", GuangLanDuanListFragment.this.searchJuli);
                        put("model.descChina", GuangLanDuanListFragment.this.searchJibie);
                        put("model.lontude", GuangLanDuanListFragment.this.searchLontude);
                        put("model.latude", GuangLanDuanListFragment.this.searchLatude);
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

    @SuppressLint("CheckResult")
    private void startLocation() {
        location.setText("定位中...");
        new RxPermissions(getActivity())
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            stopLocation();
                            locationManager = new LocationManager(getContext(),
                                    GuangLanDuanListFragment.this);
                            locationManager.start();
                        } else {
                            ToastUtils.showToast("请开启定位权限");
                            location.setText("定位失败，点击重新定位");
                        }
                    }
                });
    }

    private void stopLocation() {
        if (locationManager != null) {
            locationManager.stop();
            locationManager = null;
        }
    }


    private LocationManager.LocationBean locationBean;

    @Override
    public void onSuccess(LocationManager.LocationBean bean) {
        location.setText("定位地址: " + bean.addrss);
        this.locationBean = bean;
    }

    @Override
    public void onError(int code, String msg) {
        location.setText("定位地址: 定位失败，点击重新定位");
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLocation();
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = GuangLanSearchActivity.TAG_GUANG_LAN_D_NAME)
    public void name(GuangLanDItemBean bean) {
        if (bean != null && name != null) {
            name.setText(bean.getCabelOpName());
        }
    }


}
