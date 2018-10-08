package com.zzw.guanglan.dialogs.multilevel;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MultiLevelPagerAdapter<T extends INamedEntity> extends PagerAdapter {
    private Queue<RecyclerView> pool = new LinkedBlockingQueue<>(5);
    private List<List<T>> entities = new ArrayList<>();
    private ViewPager pager;
    private List<T> selectedItems = new ArrayList<>();
    private List<Integer> selectedIndexs = new ArrayList<>();
    private IDataSet dataSet;
    private OnConfirmCallback onConfirmCallback;
    private FixPagerSlidingTabStrip pagerSlidingTabStrip;
    private CompositeDisposable compositeDisposable;

    private boolean offsetLimitOne = false;
    private boolean isUseViewPool = true;


    public MultiLevelPagerAdapter(CompositeDisposable compositeDisposable, IDataSet dataSet, ViewPager pager, FixPagerSlidingTabStrip pagerSlidingTabStrip) {
        this.compositeDisposable = compositeDisposable;
        this.dataSet = dataSet;
        this.pager = pager;
        this.pagerSlidingTabStrip = pagerSlidingTabStrip;
        init();
    }

    private void init() {
        Disposable req = dataSet.provideFirstLevel()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<T>>() {
                    @Override
                    public void accept(List<T> iNamedEntities) throws Exception {
                        if (iNamedEntities != null && iNamedEntities.size() > 0) {
                            addPage(iNamedEntities);
                        } else {
                            onSelectedLastLevel();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
        compositeDisposable.add(req);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (offsetLimitOne) {
            pager.setOffscreenPageLimit(1);
        } else {
            pager.setOffscreenPageLimit(entities.size());
        }
    }

    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        RecyclerView recyclerView = null;
        if (isUseViewPool) {
            recyclerView = pool.poll();
        }

        if (recyclerView == null) {
            recyclerView = new RecyclerView(container.getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        }
        MultiLevelListAdapter adapter = (MultiLevelListAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new MultiLevelListAdapter();
        }

        List<T> data = entities.get(position);
        adapter.setData(data);

        if (selectedIndexs.size() > position) {
            adapter.setSelectedIndex(selectedIndexs.get(position));
        }
        adapter.setSelectedCallBack(new MultiLevelListAdapter.OnSelectedCallBack<T>() {
            @Override
            public void onSelected(int listPosition, T data1) {
                clearChildLevelData(position, listPosition, data1);
                tryUpdateChildren(data1);
            }
        });
        recyclerView.setAdapter(adapter);
        container.addView(recyclerView);
        return recyclerView;
    }

    private void clearChildLevelData(int pagePosition, int listPosition, T data) {
        entities = entities.subList(0, pagePosition + 1);
        selectedItems = selectedItems.subList(0, Math.max(0, pagePosition));
        selectedIndexs = selectedIndexs.subList(0, Math.max(0, pagePosition));

        selectedItems.add(data);
        selectedIndexs.add(listPosition);
        notifyDataSetChanged();
    }

    private void tryUpdateChildren(T parent) {
        Disposable req = dataSet.provideChildren(selectedItems)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<T>>() {
                    @Override
                    public void accept(List<T> iNamedEntities) throws Exception {
                        if (iNamedEntities != null && iNamedEntities.size() > 0) {
                            addPage(iNamedEntities);
                        } else {
                            onSelectedLastLevel();
                        }
                        pagerSlidingTabStrip.notifyDataSetChanged();
                    }
                });
        compositeDisposable.add(req);
    }

    private void onSelectedLastLevel() {
        if (onConfirmCallback != null) {
            onConfirmCallback.onConfirm(selectedItems);
        }
    }

    private void addPage(List<T> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        entities.add(datas);
        notifyDataSetChanged();
        pager.setCurrentItem(pager.getCurrentItem() + 1);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        RecyclerView recyclerView = (RecyclerView) object;
        container.removeView(recyclerView);
        MultiLevelListAdapter adapter = (MultiLevelListAdapter) recyclerView.getAdapter();
        adapter.setData(null);
        recyclerView.removeAllViews();
        if (isUseViewPool) {
            pool.offer(recyclerView);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (selectedItems.size() > position) {
            return selectedItems.get(position)._getDisplayName_();
        }
        return "请选择";
    }

    public OnConfirmCallback getOnConfirmCallback() {
        return onConfirmCallback;
    }

    public MultiLevelPagerAdapter setOnConfirmCallback(OnConfirmCallback onConfirmCallback) {
        this.onConfirmCallback = onConfirmCallback;
        return this;
    }
}
