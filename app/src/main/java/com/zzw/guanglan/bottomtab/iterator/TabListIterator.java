package com.zzw.guanglan.bottomtab.iterator;


import com.zzw.guanglan.bottomtab.BottomTabItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzw on 2017/10/22.
 */

public class TabListIterator<T extends BottomTabItem> implements TabIterator {
    private List<T> mTabItems;
    private int index;

    public TabListIterator(){
        mTabItems = new ArrayList<>();
    }

    public void addItem(T item){
        mTabItems.add(item);
    }

    @Override
    public BottomTabItem next() {
        return mTabItems.get(index++);
    }

    @Override
    public boolean hashNext() {
        return index < mTabItems.size();
    }
}
