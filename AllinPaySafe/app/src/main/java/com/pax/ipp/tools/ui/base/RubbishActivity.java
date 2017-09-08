package com.pax.ipp.tools.ui.base;

import android.app.ActivityManager;
import android.content.pm.PackageManager;

import com.pax.ipp.tools.model.AppProcessInfo;
import com.pax.ipp.tools.mvp.impl.RubbishCleanView;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by  on 2017/9/5.
 */

public abstract class RubbishActivity extends BaseActivity implements RubbishCleanView {

    public ActivityManager activityManager = null;
    public List<AppProcessInfo> appList = null;
    public PackageManager packageManager = null;


    /**
     *
     */
    public void scanChache(){
        Observable<AppProcessInfo> appProcessInfoObservable = Observable.create(
                new Observable.OnSubscribe<AppProcessInfo>() {
            @Override
            public void call(Subscriber<? super AppProcessInfo> subscriber) {
//                subscriber.onNext();
            }
        });
        Observer<AppProcessInfo> observer = new Observer<AppProcessInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(AppProcessInfo appProcessInfo) {

            }
        };

        appProcessInfoObservable.subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                observer );
    }


}
