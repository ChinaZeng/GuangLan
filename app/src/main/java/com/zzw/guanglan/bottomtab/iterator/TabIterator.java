package com.zzw.guanglan.bottomtab.iterator;


import com.zzw.guanglan.bottomtab.BottomTabItem;

/**
 * Created by zzw on 2017/10/22.
 */

public interface TabIterator {
    BottomTabItem next();

    boolean hashNext();
}
