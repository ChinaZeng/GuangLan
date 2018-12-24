package com.zzw.guanglan.utils;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

/**
 * Create by zzw on 2018/12/24
 */
public class PopWindowUtils {

    public static void showListPop(Context context, View view, String[] data, final AdapterView.OnItemClickListener listener) {
        final ListPopupWindow popupWindow = new ListPopupWindow(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1
                , data);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onItemClick(parent, view, position, id);
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(view.getWidth());
        popupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);

        // ListPopupWindow的锚,弹出框的位置是相对当前View的位置
        popupWindow.setAnchorView(view);

        // ListPopupWindow 距锚view的距离
//                listPopupWindow.setHorizontalOffset(50);
//                listPopupWindow.setVerticalOffset(100);

        popupWindow.setModal(true);
        popupWindow.show();
    }
}
