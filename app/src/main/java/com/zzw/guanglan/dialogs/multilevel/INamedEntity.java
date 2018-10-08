package com.zzw.guanglan.dialogs.multilevel;

import android.support.annotation.Keep;

import java.io.Serializable;


@Keep
public interface INamedEntity extends Serializable {
    String _getDisplayName_();
}
