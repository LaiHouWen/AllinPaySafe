package com.pax.ipp.tools.http.base;

import rx.Observer;

/**
 * Created by Administrator on 2017/9/18.
 */

public interface AbsObserver<T> extends Observer<T> {

    /**
     * 准备工作
     */
    void onStart();

    void onProgressUpdate(Object... params);

}
