package com.qliktrialreactnativestraighttable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PixelUtils {
  public static float dpToPx(float dp) {
    return  (dp * Resources.getSystem().getDisplayMetrics().density);
  }

  public static float pxToDp(float px) {
    return (px / Resources.getSystem().getDisplayMetrics().density);
  }

  public static Bitmap byteStreamToBitmap(InputStream inputStream) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    int bufferSize = 2048;
    byte[] buffer = new byte[bufferSize];
    int length = 0;
    try {
      while ((length = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, length);
      }
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    byte[] byteArray = outputStream.toByteArray();
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
  }
}
