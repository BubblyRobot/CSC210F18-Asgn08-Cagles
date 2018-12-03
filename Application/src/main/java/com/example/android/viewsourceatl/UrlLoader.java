package com.example.android.viewsourceatl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;

public class UrlLoader {

    private class DownloadTask extends AsyncTaskLoader<String> {

        /**
         * Wrapper class that serves as a union of a result value and an exception. When the
         * download task has completed, either the result value or exception can be a non-null
         * value. This allows you to pass exceptions to the UI thread that were thrown during
         * doInBackground().
         */
        private String mUrlString;

        public DownloadTask(@NonNull Context context, String urlString) {
            super(context);
            mUrlString = urlString;

        }
        class Result {
            public String mResultValue;
            public Exception mException;
            public Result(String resultValue) {
                mResultValue = resultValue;
            }
            public Result(Exception exception) {
                mException = exception;
            }
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            if (mCallback != null) {
                NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() ||
                        (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    // If no connectivity, cancel task and update Callback with null data.
                    mCallback.updateFromDownload(null);
                    cancel(true);
                }
            }
        }

        /**
         * Defines work to perform on the background thread.
         */
        @Nullable
        @Override
        public String loadInBackground() {
            NetworkFragment.DownloadTask.Result result = null;
            if (!isCancelled() && mUrlString != null && mUrlString.length > 0) {
                String urlString = mUrlString;
                try {
                    URL url = new URL(urlString);
                    String resultString = downloadUrl(url);
                    if (resultString != null) {
                        result = new NetworkFragment.DownloadTask.Result(resultString);
                    } else {
                        throw new IOException("No response received.");
                    }
                } catch(Exception e) {
                    result = new NetworkFragment.DownloadTask.Result(e);
                }
            }
            return result;
        }
        @Override
        protected void onStartLoading(){
            super.onStartLoading();
            forceLoad();
        }
}
