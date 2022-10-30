package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.GestureDetector;
import android.view.MotionEvent;

@SuppressLint("ViewConstructor")
public class ClickableTextView extends androidx.appcompat.widget.AppCompatTextView implements Content {
  DataCell cell = null;
  boolean selected = false;
  int defaultTextColor = Color.BLACK;
  final SelectionsEngine selectionsEngine;
  GestureDetector gestureDetector;
  final TableView tableView;
  ClickableTextView(Context context, SelectionsEngine selectionsEngine, TableView tableView) {
    super(context);
    this.tableView = tableView;
    this.selectionsEngine = selectionsEngine;
    defaultTextColor = getCurrentTextColor();
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent e) {
    gestureDetector.onTouchEvent(e);
    return true;
  }

  public void updateBackgroundColor() {
    int color = selected ? TableTheme.selectedBackground : Color.TRANSPARENT;
    int textColor = selected ? Color.WHITE : defaultTextColor;
    setBackgroundColor(color);
    setTextColor(textColor);
    postInvalidate();
  }

  @Override
  public void setText(CharSequence text, BufferType type) {
    super.setText(text, type);
  }

  @Override
  public void setGestureDetector(GestureDetector gestureDetector) {
    this.gestureDetector = gestureDetector;
  }

  @Override
  public void toggleSelected() {
    this.selected = !selected;
  }

  @Override
  public void setCell(DataCell cell) {
    this.cell = cell;
    if(cell.indicator != null) {
      buildSpannableText();
    } else {
      setText(cell.qText);
    }
  }

  private void buildSpannableText() {
    int textColor = cell.indicator.applySegmentColors ? cell.indicator.color : TableTheme.defaultTextColor;
    StringBuilder builder = new StringBuilder(cell.qText);
    Spannable spannable;
    if(cell.indicator.hasIcon) {
      if(cell.indicator.position.equals("right")) {
        builder.append(" ");
        builder.append(cell.indicator.icon);
        spannable = new SpannableString(builder.toString());
        spannable.setSpan(new ConditionalTypeFaceSpan(this.getTypeface(), textColor), 0, cell.qText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ConditionalTypeFaceSpan(TableTheme.iconFonts, cell.indicator.color), cell.qText.length(), cell.qText.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      } else {
        builder.insert(0, cell.indicator.icon);
        builder.insert(1, " ");
        spannable = new SpannableString(builder.toString());
        spannable.setSpan(new ConditionalTypeFaceSpan(TableTheme.iconFonts, cell.indicator.color), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ConditionalTypeFaceSpan(this.getTypeface(), textColor), 2, cell.qText.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      }
    } else {
      spannable = new SpannableString(builder.toString());
      spannable.setSpan(new ConditionalTypeFaceSpan(this.getTypeface(), textColor), 0, cell.qText.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    setText(spannable);
  }

  @Override
  public DataCell getCell() {
    return this.cell;
  }

  @Override
  public void setSelected(boolean value) {
    selected = value;
  }
}
