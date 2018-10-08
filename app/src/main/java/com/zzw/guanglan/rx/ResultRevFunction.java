package com.zzw.guanglan.rx;

import com.zzw.guanglan.bean.ResultBean;

import io.reactivex.functions.Function;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class ResultRevFunction<T> implements Function<ResultBean<T>, T> {

    @Override
    public T apply(ResultBean<T> tResultBean) throws Exception {
        if (tResultBean.getCode() == 0) {
            return tResultBean.getData();
        }
        throw new CodeException(tResultBean.getCode(),
                tResultBean.getMsg());
    }
}
