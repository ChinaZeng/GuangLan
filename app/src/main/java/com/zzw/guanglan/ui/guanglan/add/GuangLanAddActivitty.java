package com.zzw.guanglan.ui.guanglan.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.AreaBean;
import com.zzw.guanglan.bean.BseRoomBean;
import com.zzw.guanglan.bean.GradeBean;
import com.zzw.guanglan.bean.GuangLanItemBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.StationBean;
import com.zzw.guanglan.bean.StatusInfoBean;
import com.zzw.guanglan.bean.TeamInfoBean;
import com.zzw.guanglan.dialogs.BottomListDialog;
import com.zzw.guanglan.dialogs.area.AreaDialog;
import com.zzw.guanglan.dialogs.multilevel.OnConfirmCallback;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.manager.UserManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.rx.ResultBooleanFunction;
import com.zzw.guanglan.utils.RequestBodyUtils;
import com.zzw.guanglan.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Function;

/**
 * Created by zzw on 2018/10/13.
 * 描述:
 */
public class GuangLanAddActivitty extends BaseActivity {


    @BindView(R.id.cabel_name)
    EditText cabelName;
    @BindView(R.id.area_id)
    TextView areaId;
    @BindView(R.id.cabel_code)
    EditText cabelCode;
    @BindView(R.id.capaticy)
    EditText capaticy;
    @BindView(R.id.op_long)
    EditText opLong;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.grade_id)
    TextView gradeId;
    @BindView(R.id.remark)
    EditText remark;
    @BindView(R.id.state)
    TextView state;


    public static void  open(Context context) {
        context.startActivity(new Intent(context, GuangLanAddActivitty.class));
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_guang_lan_add;
    }

    private String areaIdStr;
    private String stateIdS;
    private String gradeIdS;

    public void submit() {
        final String cabelOpNameS = cabelName.getText().toString().trim();
        final String cabelOpCodeS = cabelCode.getText().toString().trim();
        final String capaticyS = capaticy.getText().toString().trim();
        final String opLongS = opLong.getText().toString().trim();
        final String remarkS = remark.getText().toString().trim();
        final String addressS = address.getText().toString().trim();

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .appAdd(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("userId", UserManager.getInstance().getUserId());
                        put("cableName", cabelOpNameS);
                        put("cableNo", cabelOpCodeS);
                        put("areaId", areaIdStr);
                        put("capacity", capaticyS);
                        put("length", opLongS);
                        put("notes", remarkS);
                        put("stateId", stateIdS);
                        put("address", addressS);
                        put("gradeId", gradeIdS);
                    }
                }))
                .map(ResultBooleanFunction.create())
                .compose(LifeObservableTransformer.<Boolean>create(this))
                .subscribe(new ErrorObserver<Boolean>(this) {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            ToastUtils.showToast("新增成功");
                            finish();
                        }
                    }
                });
    }


    private void area() {
        AreaDialog.createCityDialog(this, "选择地区", new OnConfirmCallback<AreaBean>() {
            @Override
            public void onConfirm(List<AreaBean> selectedEntities) {
                if (selectedEntities.size() > 0) {
                    AreaBean bean = selectedEntities.get(selectedEntities.size() - 1);
                    areaId.setText(bean.getText());
                    areaIdStr = bean.getId();
                }
            }
        }).show(getSupportFragmentManager(), "area");
    }


    private void state() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .quertstatuslistinfo()
                .compose(LifeObservableTransformer.<List<StatusInfoBean>>create(this))
                .subscribe(new ErrorObserver<List<StatusInfoBean>>(this) {
                    @Override
                    public void onNext(List<StatusInfoBean> data) {
                        if (data != null && data.size() > 0) {
                            BottomListDialog.newInstance(data, new BottomListDialog.Convert<StatusInfoBean>() {
                                @Override
                                public String convert(StatusInfoBean data) {
                                    return data.getName();
                                }
                            }).setCallback(new BottomListDialog.Callback<StatusInfoBean>() {
                                @Override
                                public boolean onSelected(StatusInfoBean data, int position) {
                                    state.setText(data.getName());
                                    stateIdS = data.getStateId();
                                    return true;
                                }
                            }).show(getSupportFragmentManager(), "state");
                        } else {
                            ToastUtils.showToast("没有查到状态信息");
                        }
                    }
                });
    }

    private void gradle() {

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .quertListInfo()
                .compose(LifeObservableTransformer.<List<GradeBean>>create(this))
                .subscribe(new ErrorObserver<List<GradeBean>>(this) {
                    @Override
                    public void onNext(List<GradeBean> data) {
                        if (data != null && data.size() > 0) {
                            BottomListDialog.newInstance(data, new BottomListDialog.Convert<GradeBean>() {
                                @Override
                                public String convert(GradeBean data) {
                                    return data.getDescChina();
                                }
                            }).setCallback(new BottomListDialog.Callback<GradeBean>() {
                                @Override
                                public boolean onSelected(GradeBean data, int position) {
                                    gradeId.setText(data.getName());
                                    gradeIdS = data.getSerialNo();
                                    return true;
                                }
                            }).show(getSupportFragmentManager(), "gradle");
                        } else {
                            ToastUtils.showToast("没有查到级别信息");
                        }
                    }
                });
    }

    @OnClick({R.id.area_id, R.id.grade_id, R.id.state, R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.area_id:
                area();
                break;
            case R.id.grade_id:
                gradle();
                break;
            case R.id.state:
                state();
                break;
            case R.id.add:
                submit();
                break;
        }
    }


}
