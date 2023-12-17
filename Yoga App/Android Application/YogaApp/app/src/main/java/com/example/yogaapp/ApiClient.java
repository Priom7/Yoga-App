package com.example.yogaapp;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    public interface ApiCallback {
        void onSuccess(String result);

        void onFailure(Exception e);
    }

    public static void postData(String apiUrl, String requestBody, ApiCallback callback) {
        new ApiPostTask(apiUrl, requestBody, callback).execute();
    }

    private static class ApiPostTask extends AsyncTask<Void, Void, String> {
        private final String apiUrl;
        private final String requestBody;
        private final ApiCallback callback;
        private Exception exception;

        public ApiPostTask(String apiUrl, String requestBody, ApiCallback callback) {
            this.apiUrl = apiUrl;
            this.requestBody = requestBody;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(apiUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // Write the request body
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(requestBody.getBytes());
                outputStream.flush();
                outputStream.close();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                return buffer.toString();
            } catch (IOException e) {
                this.exception = e;
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException ignored) {
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                callback.onSuccess(result);
            } else {
                callback.onFailure(exception);
            }
        }
    }
}
