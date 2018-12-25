package com.zzw.guanglan.rx;

import com.zzw.guanglan.bean.ResultBean;

import io.reactivex.functions.Function;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class ResultBooleanFunction<T> implements Function<ResultBean<T>, Boolean> {

    @Override
    public Boolean apply(ResultBean<T> tResultBean) throws Exception {
        if (tResultBean.getCode() == 0) {
            return true;
        }
        throw new CodeException(tResultBean.getCode(), tResultBean.getMsg());
    }

    public static <T> ResultBooleanFunction<T> create() {
        return new ResultBooleanFunction<>();
    }
}