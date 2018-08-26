package com.zzw.socketdemo.bottomtab.iterator;


import com.zzw.socketdemo.bottomtab.BottomTabItem;

/**
 * Created by zzw on 2017/10/22.
 */

public interface TabIterator {
    BottomTabItem next();

    boolean hashNext();
}
