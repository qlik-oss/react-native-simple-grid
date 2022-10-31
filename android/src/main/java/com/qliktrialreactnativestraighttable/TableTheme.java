package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;

import com.facebook.react.bridge.ReadableMap;

public class TableTheme {
  static int headerHeight = (int)PixelUtils.dpToPx(54);
  static int headerBackgroundColor = Color.parseColor("#F0F0F0");
  static int defaultTextColor = Color.parseColor("#404040");
  @SuppressLint("NewApi")
  static int borderBackgroundColor = Color.argb(0.1f, 0, 0, 0);
  static int selectedBackground = Color.parseColor("#009845");
  static Typeface iconFonts = null;
  static int rowHeightFactor =(int)PixelUtils.dpToPx(48);
}
