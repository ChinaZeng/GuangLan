package com.zzw.guanglan.ui.workorder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class WorkOrderListFragment extends BaseFragment {

    @BindView(R.id.recy)
    RecyclerView recy;

    private WorkOrderListAdapter adapter;

    private final static String FLOG = "flog";
    private int flog = 0;

    public static WorkOrderListFragment newInstance(int flog) {
        WorkOrderListFragment fragment = new WorkOrderListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FLOG, flog);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null)
            flog = arguments.getInt(FLOG, 0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_work_order_list;
    }

    @Override
    protected void initData() {
        super.initData();
        recy.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorkOrderListAdapter(getData());
        recy.setAdapter(adapter);
    }


    private List<String> getData() {
        List<String> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            data.add("21");
        }
        return data;
    }
}
