package net.kjulio.rxlocation;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;

import java.util.concurrent.TimeUnit;


class MockPendingResult<R extends Result> extends PendingResult<R> {

    MockPendingResult() {}

    @NonNull
    @Override
    public R await() {
        throw new UnsupportedOperationException("Stub!");
    }

    @NonNull
    @Override
    public R await(long l, @NonNull TimeUnit timeUnit) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public boolean isCanceled() {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void setResultCallback(@NonNull ResultCallback<? super R> resultCallback) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void setResultCallback(@NonNull ResultCallback<? super R> resultCallback, long l, @NonNull TimeUnit timeUnit) {
        throw new UnsupportedOperationException("Stub!");
    }
}
