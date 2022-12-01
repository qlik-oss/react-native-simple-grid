package com.qliktrialreactnativestraighttable;

import android.graphics.Bitmap;
import android.webkit.URLUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ImageLoader {
  public Set<String> imagePaths = new HashSet<>();
  public Map<String, Bitmap> imageData = new HashMap<>();
  public Bitmap getImageData(String url) {
    return imageData.get(url);
  }

  public void addImagePath(String imageUrl) {
    if(!URLUtil.isValidUrl(imageUrl)) {
      return;
    }
    imagePaths.add(imageUrl);
  }

  public void fetchImages() {
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
          public void onResponse(Call call, Response response) {
            InputStream inputStream = response.body().byteStream();
            Bitmap bitmap = PixelUtils.byteStreamToBitmap(inputStream);
            imageData.replace(imageUrl, bitmap);
            latch.countDown();
          }

          public void onFailure(Call call, IOException e) {
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
  }
}
