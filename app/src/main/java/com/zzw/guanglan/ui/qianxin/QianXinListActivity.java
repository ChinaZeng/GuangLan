package com.zzw.guanglan.ui.qianxin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dl7.tag.TagLayout;
import com.dl7.tag.TagView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.QianXinItemBean;
import com.zzw.guanglan.bean.SingleChooseBean;
import com.zzw.guanglan.bean.StatusInfoBean;
import com.zzw.guanglan.dialogs.BottomListDialog;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.manager.LocationManager;
import com.zzw.guanglan.manager.UserManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.rx.ResultBooleanFunction;
import com.zzw.guanglan.service.SocketService;
import com.zzw.guanglan.socket.CMD;
import com.zzw.guanglan.socket.EventBusTag;
import com.zzw.guanglan.socket.event.SorFileBean;
import com.zzw.guanglan.socket.event.TestArgsAndStartBean;
import com.zzw.guanglan.socket.resolve.Packet;
import com.zzw.guanglan.socket.utils.ByteUtil;
import com.zzw.guanglan.socket.utils.FileHelper;
import com.zzw.guanglan.socket.utils.MyLog;
import com.zzw.guanglan.ui.HotConnActivity;
import com.zzw.guanglan.utils.RequestBodyUtils;
import com.zzw.guanglan.utils.SPUtil;
import com.zzw.guanglan.utils.ToastUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class QianXinListActivity extends BaseActivity implements
        BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        QianXinListAdapter.OnTestListener,
        QianXinListAdapter.OnUploadListener,
        QianXinListAdapter.OnStatusListener,
        LocationManager.OnLocationListener {
    @BindView(R.id.recy)
    RecyclerView recy;

    @BindView(R.id.ll)
    LinearLayout ll;


    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;


    private LocationManager locationManager;

    private int pageNo = 1;

    private final static String ITEM = "item";


    private QianXinListAdapter adapter;
    private GuangLanDItemBean bean;

    private List<SingleChooseBean> juliS;
    private List<SingleChooseBean> bochangS;
    private List<SingleChooseBean> timeS;
    private List<SingleChooseBean> zheshelvS;
    private List<SingleChooseBean> modeS;
    private List<SingleChooseBean> maiKuanS;

    private TestArgsAndStartBean testArgsCustomModeBean;
    private TestArgsAndStartBean testArgsLastModeBean;
    private TestArgsAndStartBean testArgsAutoModeBean;


    public static void open(Context context, GuangLanDItemBean bean) {
        context.startActivity(new Intent(context, QianXinListActivity.class)
                .putExtra(ITEM, bean)
        );
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_qian_xin_list;
    }

    @Override
    protected void initData() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.WRITE_SETTINGS,}, 5);

        super.initData();
        bean = (GuangLanDItemBean) getIntent().getSerializableExtra(ITEM);

        headerView();

        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QianXinListAdapter(new ArrayList<QianXinItemBean>());
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        adapter.setOnTestListener(this);
        adapter.setOnUploadListener(this);
        adapter.setOnStatusListener(this);
        recy.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this);

        startLocation();
        onRefresh();
    }

    void getData() {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .getAppListByPage(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("model.cblOpName", bean.getCabelOpName());
                        put("model.cblOpCode", bean.getCabelOpCode());
                        put("pageNum", String.valueOf(pageNo));
                    }
                }))
                .compose(LifeObservableTransformer.<ListDataBean<QianXinItemBean>>create(this))
                .subscribe(new ErrorObserver<ListDataBean<QianXinItemBean>>(this) {
                    @Override
                    public void onNext(ListDataBean<QianXinItemBean> qianXinItemBeans) {
                        if (qianXinItemBeans.getList() != null && qianXinItemBeans.getList().size() > 0) {
                            setData(qianXinItemBeans.getList());
                            if (adapter.getData().size() >= qianXinItemBeans.getTotal()) {
                                adapter.loadMoreEnd();
                            } else {
                                adapter.loadMoreComplete();
                            }
                        }
                    }
                });
    }


    void setData(List<QianXinItemBean> datas) {
        if (pageNo == 1) {
            adapter.replaceData(datas);
            refreshLayout.setRefreshing(false);
        } else {
            adapter.addData(datas);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        pageNo++;
        getData();
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        getData();
    }


    private QianXinItemBean testBean;

    @Override
    public void onTest(QianXinItemBean bean) {
        TestArgsAndStartBean testArgsAndStartBean;
        if (argsMode == 0) {
            testArgsAndStartBean = testArgsCustomModeBean;
        } else if (argsMode == 1) {
            testArgsAndStartBean = testArgsLastModeBean;
            if (testArgsAndStartBean == null) {
                ToastUtils.showToast("没有上一次测试的数据!");
                return;
            }
        } else {
            testArgsAndStartBean = testArgsAutoModeBean;
        }

        SPUtil.getInstance("testArgs").put("args", testArgsAndStartBean);

        if (!SocketService.isConn()) {
            ToastUtils.showToast("请和设备建立链接");
            HotConnActivity.open(this);
            return;
        }
        this.testBean = bean;

        if (progressDialog == null) {
            initProgress();
        }
        progressDialog.setTitle("正在获取相关参数");
        progressDialog.show();


        EventBus.getDefault().post(testArgsAndStartBean, EventBusTag.SEND_TEST_ARGS_AND_START_TEST);

//        chooseArgs();
    }

    private void initProgress() {
        progressDialog = new ProgressDialog(QianXinListActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Subscriber(tag = EventBusTag.TAG_RECIVE_MSG)
    public void reciverMsg(Packet packet) {

        if (packet.cmd == CMD.RECIVE_SOR_INFO && packet.data.length >= (32 + 16 + 4)) {
            byte[] fileNameB = ByteUtil.subBytes(packet.data, 0, 32);
            byte[] fileLocB = ByteUtil.subBytes(packet.data, 32, 16);
            byte[] fileSizeB = ByteUtil.subBytes(packet.data, 32 + 16, 4);
            fileName = ByteUtil.bytes2Str(fileNameB);
            fileDir = ByteUtil.bytes2Str(fileLocB);
            fileSize = ByteUtil.bytesToInt(fileSizeB);
            MyLog.e("fileName = " + fileName + "  fileLoc = " + fileDir + " fileSize = " + fileSize);
            getSorFilecount = 0;

            getSorFile();
        }
    }

    @Subscriber(tag = EventBusTag.SOR_RECIVE_SUCCESS)
    public void reciveSorSuccess(SorFileBean bean) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        testBean.setTestLocalFilePath(bean.filePath);
        adapter.notifyDataSetChanged();

        testSuccessHint();
    }

    private void testSuccessHint() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("测试完成,是否上传测试文件?");
        builder.setNegativeButton("上传", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onUpload(testBean);
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Subscriber(tag = EventBusTag.SOR_RECIVE_FAIL)
    public void reciveSorFail(SorFileBean bean) {
        getSorFile();
    }

    private AlertDialog chooseArgsDialog;
    private ProgressDialog progressDialog;

    private void chooseArgs() {
        progressDialog = new ProgressDialog(QianXinListActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("正在获取sor文件相关参数信息");
        progressDialog.show();


        if (chooseArgsDialog == null) {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_edit_args, null);
            final EditText range = dialogView.findViewById(R.id.et_range);
            final EditText wl = dialogView.findViewById(R.id.et_wl);
            final EditText pw = dialogView.findViewById(R.id.et_pw);
            final EditText time = dialogView.findViewById(R.id.et_time);
            final EditText mode = dialogView.findViewById(R.id.et_mode);
            final EditText gi = dialogView.findViewById(R.id.et_gi);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            chooseArgsDialog = builder.create();
            chooseArgsDialog.setCanceledOnTouchOutside(false);
            dialogView.findViewById(R.id.start_test).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String rangeStr = range.getText().toString().trim();
                        String wlStr = wl.getText().toString().trim();
                        String pwStr = pw.getText().toString().trim();
                        String timeStr = time.getText().toString().trim();
                        String modeStr = mode.getText().toString().trim();
                        String giStr = gi.getText().toString().trim();
                        if (rangeStr.length() == 0 || wlStr.length() == 0
                                || pwStr.length() == 0 || timeStr.length() == 0
                                || modeStr.length() == 0 || giStr.length() == 0) {
                            ToastUtils.showToast("请先填取参数");
                            return;
                        }
                        int r = Integer.parseInt(rangeStr);
                        int w = Integer.parseInt(wlStr);
                        int p = Integer.parseInt(pwStr);
                        int t = Integer.parseInt(timeStr);
                        int m = Integer.parseInt(modeStr);
                        int g = Integer.parseInt(giStr);

                        TestArgsAndStartBean bean = new TestArgsAndStartBean();
                        bean.rang = r;
                        bean.wl = w;
                        bean.pw = p;
                        bean.time = t;
                        bean.mode = m;
                        bean.gi = g;

                        EventBus.getDefault().post(bean, EventBusTag.SEND_TEST_ARGS_AND_START_TEST);

                        progressDialog = new ProgressDialog(QianXinListActivity.this);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setTitle("正在获取sor文件相关参数信息");
                        progressDialog.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showToast("出现异常了，请填取数值");
                    }
                    chooseArgsDialog.dismiss();
                }
            });
        }
        chooseArgsDialog.show();
    }

    private String fileName;
    private String fileDir;
    private int fileSize;
    private int getSorFilecount = 0;

    private void getSorFile() {
        if (fileName != null && fileDir != null && fileSize != 0) {
            getSorFilecount++;
            if (getSorFilecount > 3) {
                getSorFilecount = 0;
                ToastUtils.showToast("测试失败!");
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                return;
            }

            //这一步很重要，因为协议原因。请求之前必须先删除之前的文件
            String localFileName = FileHelper.SAVE_FILE_DIR + File.separator + fileName;
            File file = new File(localFileName);
            if (file.exists()) {
                file.delete();
            }


            progressDialog.setTitle("正在获取测试文件");
            progressDialog.show();

            SorFileBean bean = new SorFileBean();
            bean.fileDir = fileDir;
            bean.fileName = fileName;
            bean.fileSize = fileSize;
            EventBus.getDefault().post(bean, EventBusTag.GET_SOR_FILE);

        } else {
            ToastUtils.showToast("请先设备向APP反馈sor文件信息");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
        EventBus.getDefault().unregister(this);
    }


    void initArgsData() {
        testArgsCustomModeBean = new TestArgsAndStartBean();
        juliS = new ArrayList<>();

        List<SingleChooseBean> maikuanS1 = new ArrayList<>();
        maikuanS1.add(new SingleChooseBean(0, "5ns", 5));
        maikuanS1.add(new SingleChooseBean(1, "10ns", 10));
        maikuanS1.add(new SingleChooseBean(2, "20ns", 20));
        maikuanS1.add(new SingleChooseBean(3, "40ns", 40));
        juliS.add(new SingleChooseBean(0, "300m", 300, maikuanS1));
        juliS.add(new SingleChooseBean(1, "1km", 1000, maikuanS1));

        List<SingleChooseBean> maikuanS2 = new ArrayList<>(maikuanS1);
        maikuanS2.add(new SingleChooseBean(4, "80ns", 80));
        maikuanS2.add(new SingleChooseBean(5, "160ns", 160));
        juliS.add(new SingleChooseBean(2, "5km", 5000, maikuanS2));


        List<SingleChooseBean> maikuanS3 = new ArrayList<>(maikuanS2);
        maikuanS3.add(new SingleChooseBean(6, "320ns", 320));
        juliS.add(new SingleChooseBean(3, "10km", 10000, maikuanS3));


        List<SingleChooseBean> maikuanS4 = new ArrayList<>(maikuanS3);
        maikuanS4.add(new SingleChooseBean(7, "640ns", 640));
        juliS.add(new SingleChooseBean(4, "30km", 30000, maikuanS4));


        List<SingleChooseBean> maikuanS5 = new ArrayList<>(maikuanS4.subList(4, maikuanS4.size()));
        maikuanS5.add(new SingleChooseBean(8, "1.28us", 1280));
        juliS.add(new SingleChooseBean(5, "60km", 60000, maikuanS5));

        List<SingleChooseBean> maikuanS6 = new ArrayList<>(maikuanS5.subList(1, maikuanS5.size()));
        maikuanS6.add(new SingleChooseBean(9, "2.56us", 2560));
        maikuanS6.add(new SingleChooseBean(10, "5.12us", 5120));
        maikuanS6.add(new SingleChooseBean(11, "10.24us", 10240));
        maikuanS6.add(new SingleChooseBean(12, "20.48us", 20480));
        juliS.add(new SingleChooseBean(6, "100km", 100000, maikuanS6));
        juliS.add(new SingleChooseBean(7, "180km", 180000, maikuanS6));
        testArgsCustomModeBean.rang = juliS.get(0).getValue();

        maiKuanS = juliS.get(0).getNextChooses();
        testArgsCustomModeBean.pw = maiKuanS.get(0).getValue();

        bochangS = new ArrayList<>();
        bochangS.add(new SingleChooseBean(0, "1550nm", 1550));
        testArgsCustomModeBean.wl = bochangS.get(0).getValue();


        timeS = new ArrayList<>();
        timeS.add(new SingleChooseBean(0, "10s", 10));
        timeS.add(new SingleChooseBean(1, "15s", 15));
        timeS.add(new SingleChooseBean(2, "30s", 30));
        timeS.add(new SingleChooseBean(3, "1min", 60));
        testArgsCustomModeBean.time = timeS.get(0).getValue();

        modeS = new ArrayList<>();
        modeS.add(new SingleChooseBean(0, "平均", 1));
        modeS.add(new SingleChooseBean(1, "实时", 2));
        testArgsCustomModeBean.mode = modeS.get(0).getValue();

        zheshelvS = new ArrayList<>();
        zheshelvS.add(new SingleChooseBean(0, "146850", 146850));
        testArgsCustomModeBean.gi = zheshelvS.get(0).getValue();
    }


    private TagLayout juli, bochang, maikuan, time, zheshelv, mode;
    TextView location;
    //0 自定义  1上一次  2自动
    int argsMode = 0;

    View headerView() {
        final View view = LayoutInflater.from(this).inflate(R.layout.layout_qianxin_header, ll, false);
        ll.addView(view, 0);
        location = view.findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocation();
            }
        });
        TextView tv_guanglan_name = view.findViewById(R.id.tv_guanglan_name);
        TextView tv_guanglan_code = view.findViewById(R.id.tv_guanglan_code);

        tv_guanglan_name.setText("光缆名称:" + bean.getCabelOpName());
        tv_guanglan_code.setText("光缆编码:" + bean.getCabelOpCode());

        final View cutomView = view.findViewById(R.id.content);
        final View lastView = view.findViewById(R.id.content2);
        final View autoMode = view.findViewById(R.id.content3);

        final TextView head_click = view.findViewById(R.id.head_click);
        view.findViewById(R.id.head_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View content;
                if (argsMode == 0) {
                    content = cutomView;
                } else if (argsMode == 1) {
                    content = lastView;
                    initLastMode(view);
                } else {
                    content = autoMode;
                }
                content.setVisibility(content.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                if (content.getVisibility() == View.VISIBLE) {
                    head_click.setText("关闭");
                    if (argsMode == 1) {
                        initLastMode(view);
                    }
                } else {
                    head_click.setText("展开");
                }
            }
        });

        initCustomMode(view);
        initLastMode(view);
        initAutoMode(view);

        final TagLayout tagLayout = view.findViewById(R.id.sel_mode);
        tagLayout.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    head_click.setText("关闭");
                    argsMode = i;
                    //自定义
                    if (i == 0) {
                        cutomView.setVisibility(View.VISIBLE);
                        lastView.setVisibility(View.GONE);
                        autoMode.setVisibility(View.GONE);
                        //上一次
                    } else if (i == 1) {
                        initLastMode(view);
                        cutomView.setVisibility(View.GONE);
                        lastView.setVisibility(View.VISIBLE);
                        autoMode.setVisibility(View.GONE);
                        //自动配置
                    } else {
                        cutomView.setVisibility(View.GONE);
                        lastView.setVisibility(View.GONE);
                        autoMode.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        tagLayout.addTags("自定义", "上一次配置", "自动配置");
        tagLayout.setCheckTag(0);

        return view;
    }

    private void initAutoMode(View view) {
        TagLayout juli = view.findViewById(R.id.content3_juli);
        juli.addTags("300m", "1km", "5km", "10km", "30km", "60km", "100km", "180km");
        juli.setCheckTag(0);
        juli.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (!b) {
                    return;
                }
                TestArgsAndStartBean bean = new TestArgsAndStartBean();
                bean.mode = 1;
                bean.gi = 146850;
                bean.wl = 1550;
                if (i == 0) {
                    bean.rang = 300;
                    bean.pw = 10;
                    bean.time = 10;
                } else if (i == 1) {
                    bean.rang = 1000;
                    bean.pw = 20;
                    bean.time = 10;
                } else if (i == 2) {
                    bean.rang = 5000;
                    bean.pw = 40;
                    bean.time = 15;
                } else if (i == 3) {
                    bean.rang = 10000;
                    bean.pw = 80;
                    bean.time = 15;
                } else if (i == 4) {
                    bean.rang = 30000;
                    bean.pw = 160;
                    bean.time = 15;
                } else if (i == 5) {
                    bean.rang = 60000;
                    bean.pw = 320;
                    bean.time = 30;
                } else if (i == 6) {
                    bean.rang = 100000;
                    bean.pw = 640;
                    bean.time = 30;
                } else if (i == 7) {
                    bean.rang = 180000;
                    bean.pw = 2560;
                    bean.time = 60;
                }
                testArgsAutoModeBean = bean;
            }
        });
        TestArgsAndStartBean bean = new TestArgsAndStartBean();
        bean.mode = 1;
        bean.gi = 146850;
        bean.wl = 1550;
        bean.rang = 300;
        bean.pw = 10;
        bean.time = 10;
        testArgsAutoModeBean = bean;
    }

    private void initLastMode(View view) {
        TestArgsAndStartBean bean = SPUtil.getInstance("testArgs").getSerializable("args", null);
        TextView juli = view.findViewById(R.id.content2_juli);
        TextView bochang = view.findViewById(R.id.content2_bochang);
        TextView maikuan = view.findViewById(R.id.content2_maikuan);
        TextView time = view.findViewById(R.id.content2_time);
        TextView mode = view.findViewById(R.id.content2_mode);
        TextView zheshelv = view.findViewById(R.id.content2_zheshelv);

        if (bean != null) {
            juli.setText("测试距离:" + bean.rang);
            bochang.setText("测试波长:" + bean.wl);
            maikuan.setText("测试脉宽:" + bean.pw);
            time.setText("测试时间:" + bean.time);
            mode.setText("测试模式:" + (bean.mode == 1 ? "平均" : "实时"));
            zheshelv.setText("折射率:" + bean.gi);
        } else {
            juli.setText("测试距离: 无");
            bochang.setText("测试波长: 无");
            maikuan.setText("测试脉宽: 无");
            time.setText("测试时间: 无");
            mode.setText("测试模式: 无");
            zheshelv.setText("折射率: 无");
        }

        testArgsLastModeBean = bean;
    }

    private void initCustomMode(View view) {
        initArgsData();

        juli = view.findViewById(R.id.juli);
        for (SingleChooseBean singleChooseBean : juliS) {
            juli.addTag(singleChooseBean.getName());
        }
        juli.setCheckTag(0);
        juli.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testArgsCustomModeBean.rang = juliS.get(i).getValue();
                    maiKuanS = juliS.get(i).getNextChooses();
                    changeTag(maikuan, maiKuanS);
                    testArgsCustomModeBean.pw = maiKuanS.get(0).getValue();
                }
            }
        });

        bochang = view.findViewById(R.id.bochang);
        for (SingleChooseBean singleChooseBean : bochangS) {
            bochang.addTag(singleChooseBean.getName());
        }
        bochang.setCheckTag(0);
        bochang.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testArgsCustomModeBean.wl = bochangS.get(i).getValue();
                }
            }
        });

        maikuan = view.findViewById(R.id.maikuan);
        for (SingleChooseBean singleChooseBean : maiKuanS) {
            maikuan.addTag(singleChooseBean.getName());
        }
        maikuan.setCheckTag(0);
        maikuan.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testArgsCustomModeBean.pw = maiKuanS.get(i).getValue();
                }
            }
        });

        time = view.findViewById(R.id.time);
        for (SingleChooseBean singleChooseBean : timeS) {
            time.addTag(singleChooseBean.getName());
        }
        time.setCheckTag(0);
        time.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testArgsCustomModeBean.time = timeS.get(i).getValue();
                }
            }
        });

        mode = view.findViewById(R.id.mode);
        for (SingleChooseBean singleChooseBean : modeS) {
            mode.addTag(singleChooseBean.getName());
        }
        mode.setCheckTag(0);
        mode.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testArgsCustomModeBean.mode = modeS.get(i).getValue();
                }
            }
        });

        zheshelv = view.findViewById(R.id.zheshelv);
        for (SingleChooseBean singleChooseBean : zheshelvS) {
            zheshelv.addTag(singleChooseBean.getName());
        }
        zheshelv.setCheckTag(0);
        zheshelv.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testArgsCustomModeBean.gi = zheshelvS.get(i).getValue();
                }
            }
        });
    }

    void changeTag(TagLayout tagLayout, List<SingleChooseBean> newBeans) {
        tagLayout.cleanTags();
        for (SingleChooseBean newBean : newBeans) {
            tagLayout.addTag(newBean.getName());
        }
        tagLayout.setCheckTag(0);
    }

    void checkInit() {
        for (int i = 0; i < juliS.size(); i++) {
            if (testArgsCustomModeBean.rang == juliS.get(i).getValue()) {
                juli.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < bochangS.size(); i++) {
            if (testArgsCustomModeBean.wl == bochangS.get(i).getValue()) {
                bochang.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < maiKuanS.size(); i++) {
            if (testArgsCustomModeBean.pw == maiKuanS.get(i).getValue()) {
                maikuan.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < timeS.size(); i++) {
            if (testArgsCustomModeBean.time == timeS.get(i).getValue()) {
                time.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < modeS.size(); i++) {
            if (testArgsCustomModeBean.mode == modeS.get(i).getValue()) {
                mode.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < zheshelvS.size(); i++) {
            if (testArgsCustomModeBean.gi == zheshelvS.get(i).getValue()) {
                zheshelv.setCheckTag(i);
                break;
            }
        }
    }


    @Override
    public void onUpload(final QianXinItemBean bean) {

        if (locationBean == null) {
            ToastUtils.showToast("请先获取定位!");
            return;
        }

        if (TextUtils.isEmpty(bean.getTestLocalFilePath())) {
            ToastUtils.showToast("请先进行测试!");
            return;
        }
        if (!new File(bean.getTestLocalFilePath()).exists()) {
            ToastUtils.showToast("测试文件已失效，请重新测试!");
            bean.setTestLocalFilePath(null);
            adapter.notifyDataSetChanged();
            return;
        }

        if (progressDialog == null) {
            initProgress();
        }
        progressDialog.setTitle("正在上传sor文件");
        progressDialog.show();

        final File file = new File(bean.getTestLocalFilePath());
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        String name = file.getName();
        MultipartBody.Part fileBody =
                MultipartBody.Part.createFormData("file", name, requestFile);

        String struffix = null;
        if (name.contains(".")) {
            String[] split = name.split("\\.");
            struffix = split[split.length - 1];
        }

        final String finalStruffix = struffix;
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .saveFiberFile(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("struffix", finalStruffix);
                        put("fiberId", bean.getFiberId());
                        put("userId", UserManager.getInstance().getUserId());
                        put("geny", String.valueOf(locationBean.latitude));
                        put("genx", String.valueOf(locationBean.longitude));
                    }
                }), fileBody)
                .map(ResultBooleanFunction.create())
                .compose(LifeObservableTransformer.<Boolean>create(this))
                .subscribe(new ErrorObserver<Boolean>(this) {
                    @Override
                    public void onNext(Boolean bo) {
                        if (bo) {
                            bean.setUpload(true);
                            bean.setModifyDate(String.valueOf(new Date().getTime()));
                            adapter.notifyDataSetChanged();
                            ToastUtils.showToast("上传成功");
                        } else {
                            ToastUtils.showToast("上传失败");
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onStatus(final QianXinItemBean bean) {
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
                                    changeStatus(bean, data);
                                    return true;
                                }
                            }).show(getSupportFragmentManager(), "state");
                        } else {
                            ToastUtils.showToast("没有查到状态信息");
                        }
                    }
                });
    }


    void changeStatus(final QianXinItemBean qianXinItemBean, final StatusInfoBean statusInfoBean) {
//        if (TextUtils.equals(qianXinItemBean.getStateId(), statusInfoBean.getStateId())){
//            return ;
//        }

        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .updateFiberState(qianXinItemBean.getFiberId(), statusInfoBean.getStateId())
                .map(new ResultBooleanFunction<>())
                .compose(LifeObservableTransformer.<Boolean>create(this))
                .subscribe(new ErrorObserver<Boolean>(this) {
                    @Override
                    public void onNext(Boolean b) {
                        if (b) {
                            qianXinItemBean.setStateId(statusInfoBean.getStateId());
                            qianXinItemBean.setStateName(statusInfoBean.getName());
                            int pos = adapter.getData().indexOf(qianXinItemBean);
                            if (pos != -1) {
                                //1 是header
                                adapter.notifyItemChanged(pos);
                            } else {
                                adapter.notifyDataSetChanged();
                            }

                            ToastUtils.showToast("修改状态成功");
                        } else {
                            ToastUtils.showToast("修改状态失败");
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void startLocation() {
        location.setText("定位中...");
        new RxPermissions(this)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            stopLocation();
                            locationManager = new LocationManager(QianXinListActivity.this,
                                    QianXinListActivity.this);
                            locationManager.start();
                        } else {
                            ToastUtils.showToast("请开启定位权限");
                            location.setText("定位失败，点击重新定位");
                        }
                    }
                });
    }


    private void stopLocation() {
        if (locationManager != null) {
            locationManager.stop();
            locationManager = null;
        }
    }


    private LocationManager.LocationBean locationBean;

    @Override
    public void onSuccess(LocationManager.LocationBean bean) {
        location.setText(bean.addrss);
        this.locationBean = bean;
    }

    @Override
    public void onError(int code, String msg) {
        location.setText("定位失败，点击重新定位");
    }
}
