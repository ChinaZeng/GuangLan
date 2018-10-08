package com.zzw.socketdemo.dialogs.multilevel;

import java.util.List;

public interface OnConfirmCallback<T extends INamedEntity> {
    void onConfirm(List<T> selectedEntities);
}