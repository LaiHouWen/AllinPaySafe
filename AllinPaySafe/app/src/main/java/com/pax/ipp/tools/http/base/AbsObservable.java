package com.pax.ipp.tools.http.base;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2017/9/18.
 */

public interface AbsObservable<T> {

    void onStart();

    void onNext(T t);

    void onError(Throwable e);

    void onCompleted();

}
