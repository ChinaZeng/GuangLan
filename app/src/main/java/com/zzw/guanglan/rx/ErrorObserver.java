package com.zzw.guanglan.rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class ErrorObserver<T> implements Observer<T> {

    private IError error;

    public ErrorObserver(IError error) {
        this.error = error;
    }


    @Override
    public void onSubscribe(Disposable d) {

    }


    @Override
    public void onError(Throwable e) {
        if (error != null) {
            error.showError(e);
        }
    }

    @Override
    public void onComplete() {

    }

}
