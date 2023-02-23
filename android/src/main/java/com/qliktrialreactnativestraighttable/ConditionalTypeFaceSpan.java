package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

import androidx.annotation.NonNull;

public class ConditionalTypeFaceSpan extends TypefaceSpan {
  private final Typeface newType;
  private int textColor = TableTheme.defaultTextColor;

  public ConditionalTypeFaceSpan(Typeface typeface) {
    super(typeface);
    newType = typeface;
  }
  public ConditionalTypeFaceSpan(Typeface typeface, int textColor) {
    super(typeface);
    newType = typeface;
    this.textColor = textColor;
  }

  public ConditionalTypeFaceSpan(String family, Typeface typeface) {
    super(family);
    newType = typeface;
  }

  public ConditionalTypeFaceSpan(String family, Typeface typeface, int color) {
    super(family);
    newType = typeface;
    textColor = color;
  }

  @Override
  public void updateDrawState(TextPaint tp) {
    apply(tp, newType );
  }

  @Override
  public void updateMeasureState(@NonNull TextPaint paint) {
    apply(paint, newType);
  }

  @SuppressLint("WrongConstant")
  private void apply(Paint paint, Typeface tp) {
    int oldStyle;
    Typeface old = paint.getTypeface();
    if (old == null) {
      oldStyle = 0;
    } else  {
      oldStyle = old.getStyle();
    }

    int fake = oldStyle & ~tp.getStyle();
    if ((fake & Typeface.BOLD) != 0) {
      paint.setFakeBoldText(true);
    }

    paint.setColor(textColor);
    paint.setTypeface(tp);

  }
}
