package com.zzw.socketdemo.ui.workorder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zzw.socketdemo.R;
import com.zzw.socketdemo.base.BaseFragment;


public class WorkOrderListFragment extends BaseFragment {

    public static WorkOrderListFragment newInstance() {
        WorkOrderListFragment fragment = new WorkOrderListFragment();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_work_order_list, container, false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_work_order_list;
    }


}
