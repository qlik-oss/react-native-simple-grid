package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

public class ImageShare {

  public void share(Bitmap bitmap, Context context) {
    Uri uri = getImageUri(bitmap, context);
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("image/png");
    intent.putExtra(Intent.EXTRA_STREAM, uri);
    try {
      context.startActivity(Intent.createChooser(intent, null));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private Uri getImageUri(Bitmap bitmap, Context context) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
    return Uri.parse(path);
  }
}
