package com.zzw.socketdemo.dialogs.multilevel;

import java.io.Serializable;
import java.util.List;

import io.reactivex.Observable;

public interface IDataSet <T extends INamedEntity>extends Serializable {
    Observable<List<T>> provideFirstLevel();

    Observable<List<T>> provideChildren(List<T> parents);
}