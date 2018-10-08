package com.zzw.socketdemo.dialogs.multilevel;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


import com.zzw.socketdemo.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuoHaifeng on 2018/3/16 0016.
 * Email:496349136@qq.com
 */

public class MultiLevelListAdapter extends RecyclerView.Adapter<MultiLevelListAdapter.ViewHolder> {
    private int selectedIndex = -1;
    private List<INamedEntity> data = new ArrayList<>();
    private OnSelectedCallBack<INamedEntity> selectedCallBack;

    public MultiLevelListAdapter setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        return this;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public MultiLevelListAdapter setData(List<INamedEntity> data) {
        this.data = data;
        this.selectedIndex = -1;
        return this;
    }

    public OnSelectedCallBack<INamedEntity> getSelectedCallBack() {
        return selectedCallBack;
    }

    public MultiLevelListAdapter setSelectedCallBack(OnSelectedCallBack<INamedEntity> selectedCallBack) {
        this.selectedCallBack = selectedCallBack;
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_dialog_multilevel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final INamedEntity entity = data.get(position);
        holder.cbName.setText(entity._getDisplayName_());
        holder.cbName.setChecked(selectedIndex == position);
        holder.selectedTag.setVisibility(selectedIndex == position ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedIndex != position) {
                    selectedIndex = position;
                    notifyDataSetChanged();
                }
                if (selectedCallBack != null) {
                    selectedCallBack.onSelected(position, entity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbName;
        View selectedTag;

        public ViewHolder(View itemView) {
            super(itemView);
            cbName = itemView.findViewById(R.id.cb_name);
            selectedTag = itemView.findViewById(R.id.iv_selected_tag);
        }
    }

    public INamedEntity getSelectedItem() {
        if (selectedIndex >= getItemCount()) {
            return null;
        }
        return data.get(selectedIndex);
    }

    public interface OnSelectedCallBack<T extends INamedEntity> extends Serializable {
        void onSelected(int position, T data);
    }
}
