package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.facebook.react.bridge.ReadableMap;

public class TableTheme {
  static int rowHeight =  (int)PixelUtils.dpToPx(48);
  static int headerHeight = (int)PixelUtils.dpToPx(54);
  static int headerBackgroundColor = Color.parseColor("#F0F0F0");
  @SuppressLint("NewApi")
  static int borderBackgroundColor = Color.argb(0.1f, 0, 0, 0);
  static int borderRadius = 8;
  static public void from(ReadableMap source){
    rowHeight =  (int)PixelUtils.dpToPx(source.getInt("rowHeight"));
    headerHeight = (int) PixelUtils.dpToPx(source.getInt("headerHeight"));
    headerBackgroundColor = Color.parseColor(source.getString("headerBackgroundColor"));
    borderRadius = (int)PixelUtils.dpToPx(source.getInt("borderRadius"));
    borderBackgroundColor = Color.parseColor(source.getString("borderBackgroundColor"));
  }
}
