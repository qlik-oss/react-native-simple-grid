package com.qliktrialreactnativestraighttable;

import android.graphics.Color;

import com.facebook.react.bridge.ReadableMap;

public class HeaderContentStyle {
  int backgroundColor;
  int color;
  String fontFamily;
  int fontSize;
  boolean wrap = true;

  HeaderContentStyle (ReadableMap data) {
    String bgColor = JsonUtils.getString(data, "backgroundColor");
    backgroundColor = bgColor != null ? Color.parseColor(bgColor) : TableTheme.headerBackgroundColor;

    String fgColor = JsonUtils.getString(data, "color");
    color = fgColor == null ? TableTheme.defaultTextColor : Color.parseColor(fgColor);

    fontFamily = JsonUtils.getString(data, "fontFamily");
    fontSize = JsonUtils.getInt(data, "fontSize", (int)PixelUtils.dpToPx(16));
    wrap = JsonUtils.getBoolean(data, "wrap", true);
  }
}
