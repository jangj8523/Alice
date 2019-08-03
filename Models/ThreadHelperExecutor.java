package com.v1.avatar.v1.Models;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class ThreadHelperExecutor implements Executor {
    @Override
    public void execute(@NonNull Runnable command) {
        new Thread(command).start();
    }

    public ThreadHelperExecutor() {

    }

    public void unSynchronizedExecute (Runnable command) {
        new Thread(command).start();
    }

    public void synchronizedExecute (Runnable command) {
        command.run();
    }
}
