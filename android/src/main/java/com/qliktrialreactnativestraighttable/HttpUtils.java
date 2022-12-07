package com.qliktrialreactnativestraighttable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {
  private static final OkHttpClient httpClient = new OkHttpClient();

  public static void get(String uri, Callback callback) {
    Request fetchImage = new Request.Builder()
      .url(uri)
      .build();
    Call call = httpClient.newCall(fetchImage);
    call.enqueue(callback);
  }
}
