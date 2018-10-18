package com.zzw.guanglan.ui.qianxin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dl7.tag.TagLayout;
import com.dl7.tag.TagView;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.GuangLanDItemBean;
import com.zzw.guanglan.bean.ListDataBean;
import com.zzw.guanglan.bean.QianXinItemBean;
import com.zzw.guanglan.bean.SingleChooseBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
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
import com.zzw.guanglan.ui.qianxin.test.QianXinTestActivity;
import com.zzw.guanglan.utils.RequestBodyUtils;
import com.zzw.guanglan.utils.ToastUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class QianXinListActivity extends BaseActivity implements
        BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        QianXinListAdapter.OnTestListener, QianXinListAdapter.OnUploadListener {
    @BindView(R.id.recy)
    RecyclerView recy;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;

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

    private TestArgsAndStartBean testArgsBean;


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

        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QianXinListAdapter(new ArrayList<QianXinItemBean>());
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, recy);
        adapter.setOnTestListener(this);
        adapter.setOnUploadListener(this);
        recy.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this);

        initArgsData();
        adapter.addHeaderView(headerView());

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
        if (!SocketService.isConn()) {
            ToastUtils.showToast("请和设备建立链接");
            HotConnActivity.open(this);
            return;
        }
        this.testBean = bean;

        EventBus.getDefault().post(testArgsBean, EventBusTag.SEND_TEST_ARGS_AND_START_TEST);

//        chooseArgs();
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
        filePath = bean.filePath;
        ToastUtils.showToast("接收sor文件成功,请上传");
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

    private String filePath;
    private String fileName;
    private String fileDir;
    private int fileSize;
    private int getSorFilecount = 0;

    private void getSorFile() {
        filePath = null;
        if (fileName != null && fileDir != null && fileSize != 0) {
            getSorFilecount++;
            if (getSorFilecount > 3) {
                getSorFilecount = 0;
                ToastUtils.showToast("文件保存失败!");
                return;
            }

            //这一步很重要，因为协议原因。请求之前必须先删除之前的文件
            String localFileName = FileHelper.SAVE_FILE_DIR + File.separator + fileName;
            File file = new File(localFileName);
            if (file.exists()) {
                file.delete();
            }

            SorFileBean bean = new SorFileBean();
            bean.fileDir = fileDir;
            bean.fileName = fileName;
            bean.fileSize = fileSize;
            EventBus.getDefault().post(bean, EventBusTag.GET_SOR_FILE);

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            progressDialog = new ProgressDialog(QianXinListActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("正在获取sor文件");
            progressDialog.show();
        } else {
            ToastUtils.showToast("请先设备向APP反馈sor文件信息");
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
        EventBus.getDefault().unregister(this);
    }


    void initArgsData() {
        testArgsBean = new TestArgsAndStartBean();
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
        testArgsBean.rang = juliS.get(0).getValue();

        maiKuanS = juliS.get(0).getNextChooses();
        testArgsBean.pw = maiKuanS.get(0).getValue();

        bochangS = new ArrayList<>();
        bochangS.add(new SingleChooseBean(0, "1550nm", 1550));
        testArgsBean.wl = bochangS.get(0).getValue();


        timeS = new ArrayList<>();
        timeS.add(new SingleChooseBean(0, "10s", 10));
        timeS.add(new SingleChooseBean(1, "15s", 15));
        timeS.add(new SingleChooseBean(2, "30s", 30));
        timeS.add(new SingleChooseBean(3, "1min", 60));
        testArgsBean.time = timeS.get(0).getValue();

        modeS = new ArrayList<>();
        modeS.add(new SingleChooseBean(0, "平均", 1));
        modeS.add(new SingleChooseBean(1, "实时", 2));
        testArgsBean.mode = modeS.get(0).getValue();

        zheshelvS = new ArrayList<>();
        zheshelvS.add(new SingleChooseBean(0, "146850", 146850));
        testArgsBean.gi = zheshelvS.get(0).getValue();
    }


    private TagLayout juli, bochang, maikuan, time, zheshelv, mode;

    View headerView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_header, recy, false);
        final View content = view.findViewById(R.id.content);
        final TextView head_click = view.findViewById(R.id.head_click);
        view.findViewById(R.id.head_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.setVisibility(content.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                if (content.getVisibility() == View.VISIBLE) {
                    head_click.setText("关闭");
                } else {
                    head_click.setText("展开");
                }
            }
        });
        juli = view.findViewById(R.id.juli);
        for (SingleChooseBean singleChooseBean : juliS) {
            juli.addTag(singleChooseBean.getName());
        }
        juli.setCheckTag(0);
        juli.setTagCheckListener(new TagView.OnTagCheckListener() {
            @Override
            public void onTagCheck(int i, String s, boolean b) {
                if (b) {
                    testArgsBean.rang = juliS.get(i).getValue();
                    maiKuanS = juliS.get(i).getNextChooses();
                    changeTag(maikuan, maiKuanS);
                    testArgsBean.pw = maiKuanS.get(0).getValue();
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
                    testArgsBean.wl = bochangS.get(i).getValue();
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
                    testArgsBean.pw = maiKuanS.get(i).getValue();
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
                    testArgsBean.time = timeS.get(i).getValue();
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
                    testArgsBean.mode = modeS.get(i).getValue();
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
                    testArgsBean.gi = zheshelvS.get(i).getValue();
                }
            }
        });
        return view;
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
            if (testArgsBean.rang == juliS.get(i).getValue()) {
                juli.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < bochangS.size(); i++) {
            if (testArgsBean.wl == bochangS.get(i).getValue()) {
                bochang.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < maiKuanS.size(); i++) {
            if (testArgsBean.pw == maiKuanS.get(i).getValue()) {
                maikuan.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < timeS.size(); i++) {
            if (testArgsBean.time == timeS.get(i).getValue()) {
                time.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < modeS.size(); i++) {
            if (testArgsBean.mode == modeS.get(i).getValue()) {
                mode.setCheckTag(i);
                break;
            }
        }

        for (int i = 0; i < zheshelvS.size(); i++) {
            if (testArgsBean.gi == zheshelvS.get(i).getValue()) {
                zheshelv.setCheckTag(i);
                break;
            }
        }
    }


    @Override
    public void onUpload(final QianXinItemBean bean) {
        if (TextUtils.isEmpty(filePath) ||
                (testBean != null &&
                        !TextUtils.equals(bean.getFiberId(), testBean.getFiberId()))) {
            ToastUtils.showToast("请先进行测试!");
            return;
        }
        if (TextUtils.isEmpty(filePath)) {
            ToastUtils.showToast("请先进行测试");
            return;
        }

        progressDialog = new ProgressDialog(QianXinListActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("正在上传sor文件");
        progressDialog.show();

        final File file = new File(filePath);
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
                    }
                }), fileBody)
                .map(ResultBooleanFunction.create())
                .compose(LifeObservableTransformer.<Boolean>create(this))
                .subscribe(new ErrorObserver<Boolean>(this) {
                    @Override
                    public void onNext(Boolean bo) {
                        if (bo) {
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
}
