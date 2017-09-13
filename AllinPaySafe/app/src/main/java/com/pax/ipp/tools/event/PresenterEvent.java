package com.pax.ipp.tools.event;

import com.pax.ipp.tools.mvp.presenter.RubbishCleanPresenter;

/**
 * Created by Administrator on 2017/9/13.
 */

public class PresenterEvent {

    private RubbishCleanPresenter presenter;

    private long cacheSize;

    public PresenterEvent(RubbishCleanPresenter presenter) {
        this.presenter = presenter;
    }

    public RubbishCleanPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(RubbishCleanPresenter presenter) {
        this.presenter = presenter;
    }

    public PresenterEvent(RubbishCleanPresenter presenter, long cacheSize) {
        this.presenter = presenter;
        this.cacheSize = cacheSize;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }
}
