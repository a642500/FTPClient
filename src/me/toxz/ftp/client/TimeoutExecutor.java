package me.toxz.ftp.client;

import javafx.concurrent.Task;
import javafx.util.Pair;
import me.toxz.ftp.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.StreamSupport;

/**
 * Created by Carlos on 2015/10/27.
 */
public class TimeoutExecutor {
    private static final String TAG = "TimeoutExecutor";
    private static ExecutorService mExecutor;
    private static Thread mTimeoutThread;
    private static List<Pair<Long, Future>> mTaskList;

    private TimeoutExecutor() {

    }

    public static void exit() {
        mTimeoutThread.interrupt();
    }

    private static TimeoutCallback mCallback;

    public static void setTimeoutCallback(TimeoutCallback callback) {
        mCallback = callback;
    }

    private static void onTimeout(Future future) {
        if (mCallback != null) {
            mCallback.onTimeout(future);
        }
    }


    public static <V> void submit(Task<V> task, long timeoutMills) {
        Log.i(TAG, "submit task: " + task + ", time: " + timeoutMills);
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadExecutor();
            mTimeoutThread = new TimeoutCheckThread();
            mTimeoutThread.setDaemon(true);
            mTimeoutThread.start();
            mTaskList = new ArrayList<>();
        }
        Future future = mExecutor.submit(task);
        mTaskList.add(new Pair<>(timeoutMills + System.currentTimeMillis(), future));
    }

    private static class TimeoutCheckThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                if (mTaskList != null) {
                    long now = System.currentTimeMillis();
                    mTaskList.stream().filter(pair -> pair.getKey() < now && !pair.getValue().isDone()).forEach(pair -> {
                        onTimeout(pair.getValue());
                        mTaskList.remove(pair);
                    });
                    try {
                        sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }

        }
    }


    public interface TimeoutCallback {
        void onTimeout(Future future);
    }

}
