package raghav.resources.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by raghav on 31/8/16.
 */

public class NetworkUtil {
    private final InternetListener listener;
    private final Activity activity;
    private static NetworkUtil networkUtil;

    private final String TAG = NetworkUtil.class.getName();

    public static NetworkUtil getInstance(@NonNull Activity activity, @NonNull InternetListener listener)
    {
        if(networkUtil==null)
            networkUtil = new NetworkUtil(activity,listener);

        return networkUtil;
    }

    private NetworkUtil(@NonNull Activity activity, @NonNull InternetListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void isConnectedToInternet() {
        new SyncTask().execute();

    }

    public boolean isConnectedToNetwork() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private class SyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (!isConnectedToNetwork()) {
                return false;
            }
            String command = "ping -c 1 google.com";
            try {
                return run(command, 10000);
            } catch (InterruptedException | IOException e) {
                AppLog.log(AppLog.D, TAG, e.getMessage());
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            listener.isConnected(isConnected);
        }
    }

    private static class Worker extends Thread {
        private final Process process;
        private Integer exit;

        private Worker(Process process) {
            this.process = process;
        }

        public void run() {
            try {
                exit = process.waitFor();
            } catch (InterruptedException ignore) {
            }
        }
    }

    private boolean run(String command, long replyTimeout) throws IOException, TimeoutException, InterruptedException {

        Process p = Runtime.getRuntime().exec(command);

        Worker worker = new Worker(p);
        worker.start();

        try {
            worker.join(replyTimeout);


            if (worker.exit != null) {
                if (worker.exit <= 0) {
                    return true;
                }
            } else {
                worker.interrupt();
                Thread.currentThread().interrupt();
                throw new TimeoutException();
            }
        } catch (InterruptedException ex) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            p.destroy();
        }
        return false;
    }

}
