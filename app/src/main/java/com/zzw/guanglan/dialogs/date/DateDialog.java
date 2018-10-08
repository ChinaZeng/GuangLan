package com.zzw.guanglan.dialogs.date;



import com.zzw.guanglan.dialogs.multilevel.IDataSet;
import com.zzw.guanglan.dialogs.multilevel.MultilLevelDialog;
import com.zzw.guanglan.dialogs.multilevel.OnConfirmCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;


public class DateDialog {
    public static MultilLevelDialog createDateTimeDialog(String title, Calendar maxDate, OnConfirmCallback<DateTimeBean> confirmCallback) {
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.YEAR, 1970);
        return _createDateTimeDialog(false, title, minDate, maxDate, confirmCallback);
    }


    public static MultilLevelDialog createDateTimeDialog(String title, Calendar minDate, Calendar maxDate, OnConfirmCallback<DateTimeBean> confirmCallback) {
        return _createDateTimeDialog(false, title, minDate, maxDate, confirmCallback);
    }


    public static MultilLevelDialog createDateTimeDialog(boolean desc, String title, Calendar minDate, Calendar maxDate, OnConfirmCallback<DateTimeBean> confirmCallback) {
        return _createDateTimeDialog(desc, title, minDate, maxDate, confirmCallback);
    }

    public static MultilLevelDialog createDateTimeDialog(String title, OnConfirmCallback <DateTimeBean>confirmCallback) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, currentYear + 100);
        calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        String date = calendar.getTime().toLocaleString();
        return createDateTimeDialog(title, calendar, confirmCallback);
    }

    private static MultilLevelDialog _createDateTimeDialog(boolean desc, String title, Calendar minDate, final Calendar maxDate, OnConfirmCallback confirmCallback) {
        final List<DateTimeBean> dateTimeBeans = new ArrayList<>();
        Calendar temp = Calendar.getInstance();
        //添加年

        if (desc) {
            //降序
            for (int year = maxDate.get(Calendar.YEAR); year >= minDate.get(Calendar.YEAR); year--) {
                temp.clear();
                temp.set(Calendar.YEAR, year);
                DateTimeBean yearBean = new DateTimeBean().setValue(year).setUnit("年").setType(DateTimeBean.Type.YEAR);
                dateTimeBeans.add(yearBean);
            }
        } else {
            for (int year = minDate.get(Calendar.YEAR); year <= maxDate.get(Calendar.YEAR); year++) {
                temp.clear();
                temp.set(Calendar.YEAR, year);
                DateTimeBean yearBean = new DateTimeBean().setValue(year).setUnit("年").setType(DateTimeBean.Type.YEAR);
                dateTimeBeans.add(yearBean);
            }
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        return MultilLevelDialog.newInstance(new IDataSet<DateTimeBean>() {
            @Override
            public Observable<List<DateTimeBean>> provideFirstLevel() {
                Object object = dateTimeBeans;
                List<DateTimeBean> data;
                if (object != null) {
                    data = (List<DateTimeBean>) object;
                } else {
                    data = new ArrayList<>();
                }
                //noinspection unchecked
                return Observable.just(data);
            }

            @Override
            public Observable<List<DateTimeBean>> provideChildren(List<DateTimeBean> parents) {
                Calendar temp = Calendar.getInstance();
                temp.clear();
                DateTimeBean parentBean = (DateTimeBean) parents.get(parents.size() - 1);
                if (parentBean.getType() == DateTimeBean.Type.YEAR) {
                    calendar.set(Calendar.YEAR, parentBean.getValue());
                    temp.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                    List<DateTimeBean> data = new ArrayList<>();
                    for (int i = 1; i <= 12; i++) {
                        temp.set(Calendar.MONTH, i - 1);
                        if (temp.compareTo(maxDate) > 0) {
                            break;
                        }
                        data.add(new DateTimeBean().setType(DateTimeBean.Type.MONTH).setValue(i).setUnit("月"));
                    }
                    return Observable.just(data);
                } else if ((parentBean.getType() == DateTimeBean.Type.MONTH)) {
                    calendar.set(Calendar.MONTH, parentBean.getValue() - 1);
                    temp.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                    temp.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                    List<DateTimeBean> data = new ArrayList<>();
                    int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    int min = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
                    for (int i = min; i <= max; i++) {
                        temp.set(Calendar.DAY_OF_MONTH, i);
                        if (temp.compareTo(maxDate) > 0) {
                            break;
                        }
                        data.add(new DateTimeBean().setType(DateTimeBean.Type.DAY).setValue(i).setUnit("日"));
                    }
                    return Observable.just(data);
                } else {
                    List<DateTimeBean> data = new ArrayList<>();
                    return Observable.just(data);
                }

            }
        }, title).setConfirmCallback(confirmCallback);
    }


    private static MultilLevelDialog _createDateTimeDialog(String title, final Calendar maxDate, OnConfirmCallback confirmCallback) {
        final List<DateTimeBean> dateTimeBeans = new ArrayList<>();
        Calendar temp = Calendar.getInstance();
        //添加年
        for (int year = 1970; year <= 2200; year++) {
            temp.clear();
            temp.set(Calendar.YEAR, year);
            if (temp.compareTo(maxDate) > 0) {
                break;
            }
            DateTimeBean yearBean = new DateTimeBean().setValue(year).setUnit("年").setType(DateTimeBean.Type.YEAR);
            dateTimeBeans.add(yearBean);
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        return MultilLevelDialog.newInstance(new IDataSet<DateTimeBean>() {
            @Override
            public Observable<List<DateTimeBean>> provideFirstLevel() {
                Object object = dateTimeBeans;
                List<DateTimeBean> data;
                if (object != null) {
                    data = (List<DateTimeBean>) object;
                } else {
                    data = new ArrayList<>();
                }
                //noinspection unchecked
                return Observable.just(data);
            }

            @Override
            public Observable<List<DateTimeBean>> provideChildren(List<DateTimeBean> parents) {
                Calendar temp = Calendar.getInstance();
                temp.clear();
                DateTimeBean parentBean = (DateTimeBean) parents.get(parents.size() - 1);
                if (parentBean.getType() == DateTimeBean.Type.YEAR) {
                    calendar.set(Calendar.YEAR, parentBean.getValue());
                    temp.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                    List<DateTimeBean> data = new ArrayList<>();
                    for (int i = 1; i <= 12; i++) {
                        temp.set(Calendar.MONTH, i - 1);
                        if (temp.compareTo(maxDate) > 0) {
                            break;
                        }
                        data.add(new DateTimeBean().setType(DateTimeBean.Type.MONTH).setValue(i).setUnit("月"));
                    }
                    return Observable.just(data);
                } else if ((parentBean.getType() == DateTimeBean.Type.MONTH)) {
                    calendar.set(Calendar.MONTH, parentBean.getValue() - 1);
                    temp.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                    temp.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                    List<DateTimeBean> data = new ArrayList<>();
                    int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    int min = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
                    for (int i = min; i <= max; i++) {
                        temp.set(Calendar.DAY_OF_MONTH, i);
                        if (temp.compareTo(maxDate) > 0) {
                            break;
                        }
                        data.add(new DateTimeBean().setType(DateTimeBean.Type.DAY).setValue(i).setUnit("日"));
                    }
                    return Observable.just(data);
                } else {
                    List<DateTimeBean> data = new ArrayList<>();
                    return Observable.just(data);
                }

            }
        }, title).setConfirmCallback(confirmCallback);
    }
}
