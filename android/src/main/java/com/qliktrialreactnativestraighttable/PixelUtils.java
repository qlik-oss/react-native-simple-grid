package com.qliktrialreactnativestraighttable;

import android.content.res.Resources;

public class PixelUtils {
  public static float dpToPx(float dp) {
    return  (dp * Resources.getSystem().getDisplayMetrics().density);
  }
}
