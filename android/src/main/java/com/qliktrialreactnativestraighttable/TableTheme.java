package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;

import com.facebook.react.bridge.ReadableMap;

public class TableTheme {
  static int headerBackgroundColor = Color.parseColor("#F0F0F0");
  static int defaultTextColor = Color.parseColor("#404040");
  @SuppressLint("NewApi")
  static int borderBackgroundColor = Color.argb(0.1f, 0, 0, 0);
  static int selectedBackground = Color.parseColor("#009845");
  static Typeface iconFonts = null;
  static int CellPadding = (int)PixelUtils.dpToPx(8);
  static int DefaultRowHeight = (int)PixelUtils.dpToPx(40);

  static int backgroundColor = Color.WHITE;


  static void updateFrom(ReadableMap theme) {
    String headerBackgroundColorString = JsonUtils.getString(theme, "headerBackgroundColor", "#404040");
    String defaultTextColorString =  JsonUtils.getString(theme, "defaultTextColor", "#404040");
    String borderBackgroundColorString =  JsonUtils.getString(theme, "borderBackgroundColor", "#404040");
    String selectedBackgroundString =  JsonUtils.getString(theme, "selectedBackground", "#009845");
    String backgroundColorString = JsonUtils.getString(theme, "backgroundColor", "#FFFFFF");

    headerBackgroundColor = Color.parseColor(headerBackgroundColorString);
    defaultTextColor = Color.parseColor(defaultTextColorString);
    selectedBackground = Color.parseColor(selectedBackgroundString);
    borderBackgroundColor = Color.parseColor(borderBackgroundColorString);
    backgroundColor = Color.parseColor(backgroundColorString);

  }
}
