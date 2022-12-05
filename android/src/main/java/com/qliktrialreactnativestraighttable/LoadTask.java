package com.qliktrialreactnativestraighttable;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoadTask extends AsyncTask<Void, Void, Map<String, Bitmap>> {
  ImageLoader loaderRef;
  public LoadTask(ImageLoader loaderRef) {
    this.loaderRef = loaderRef;
  }

  public Map<String, Bitmap> fetchImages(Set<String> imagePaths, Map<String, Bitmap> imageData) {
    final CountDownLatch latch = new CountDownLatch(imagePaths.size());
    Iterator<String> iterator = imagePaths.iterator();
    while (iterator.hasNext()) {
      String imageUrl = iterator.next();
      boolean isDuplicateImageUrl = imageData.containsKey(imageUrl);
      if(isDuplicateImageUrl || !URLUtil.isValidUrl(imageUrl)) {
        latch.countDown();
        continue;
      }
      imageData.put(imageUrl, null);
      try {
        HttpUtils.get(imageUrl, new Callback() {
          public void onResponse(@NonNull Call call, @NonNull Response response) {
            ResponseBody body = response.body();
            if (body != null) {
              InputStream inputStream = body.byteStream();
              Bitmap bitmap = PixelUtils.byteStreamToBitmap(inputStream);
              imageData.replace(imageUrl, bitmap);
            }
            latch.countDown();
          }

          public void onFailure(@NonNull Call call, IOException e) {
            latch.countDown();
          }
        });
      } catch(Exception e) {
        latch.countDown();
        imageData.remove(imageUrl);
        e.printStackTrace();
      }
    }
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return imageData;
  }

  @Override
  protected Map<String, Bitmap> doInBackground(Void... voids) {
    return fetchImages(loaderRef.imagePaths, loaderRef.imageData);
  }

  @Override
  protected void onPostExecute(Map<String, Bitmap> result) {
    super.onPostExecute(result);
    loaderRef.setImageData(result);
  }
}
