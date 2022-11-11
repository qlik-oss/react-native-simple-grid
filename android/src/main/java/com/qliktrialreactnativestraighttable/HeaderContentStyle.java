package com.qliktrialreactnativestraighttable;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.text.TextPaint;

import com.facebook.react.bridge.ReadableMap;

public class HeaderContentStyle {
  int backgroundColor;
  int color;
  String fontFamily;
  int fontSize;
  boolean wrap = true;
  int lineHeight = 1;

  HeaderContentStyle (ReadableMap data) {
    String bgColor = JsonUtils.getString(data, "backgroundColor");
    backgroundColor = bgColor != null ? Color.parseColor(bgColor) : TableTheme.headerBackgroundColor;

    String fgColor = JsonUtils.getString(data, "color");
    color = fgColor == null ? TableTheme.defaultTextColor : Color.parseColor(fgColor);

    fontFamily = JsonUtils.getString(data, "fontFamily");
    fontSize = JsonUtils.getInt(data, "fontSize", 14);
    wrap = JsonUtils.getBoolean(data, "wrap", true);
  }

  public int getLineHeight() {
    Typeface typeFace = Typeface.create(null, 700, false);
    TextPaint textPaint = new TextPaint();
    textPaint.setTypeface(typeFace);
    textPaint.setTextSize(this.fontSize);
    Paint.FontMetrics fm = textPaint.getFontMetrics();
    lineHeight = (int)Math.ceil(fm.bottom - fm.top + textPaint.getFontSpacing());
    return lineHeight;
  }
}
