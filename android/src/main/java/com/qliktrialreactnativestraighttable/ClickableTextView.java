package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

@SuppressLint("ViewConstructor")
public class ClickableTextView extends androidx.appcompat.widget.AppCompatTextView implements Content {
  DataCell cell = null;
  final CellView cellView;
  boolean selected = false;
  int defaultTextColor = Color.BLACK;
  final SelectionsEngine selectionsEngine;
  GestureDetector gestureDetector;
  final TableView tableView;
  Animation fadeIn;
  ClickableTextWrapper textWrapper;
  ClickableTextView(Context context, SelectionsEngine selectionsEngine, TableView tableView, CellView cellView) {
    super(context);
    this.tableView = tableView;
    this.selectionsEngine = selectionsEngine;
    this.cellView = cellView;
    defaultTextColor = getCurrentTextColor();
    fadeIn = AnimationUtils.loadAnimation(context, R.anim.catalyst_fade_in);
    textWrapper = new ClickableTextWrapper(tableView, this);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent e) {
    // Checks to see if there's a link, if there is and the user
    // tapped on the link text, then forward the event to the movementMethod.
    // otherwise forward the event to the gesture detector
    MovementMethod movementMethod = this.getMovementMethod();
    if(movementMethod != null ) {
      // first check to see if use tapped on text if this is a link
      Rect bounds = getMeasuredTextBounds();
      int x = (int)e.getX();
      int y = (int)e.getY();
      boolean insideText = bounds.contains(x, y);
      // if user is inside, then forward to the link listener
      if(insideText) {
        return movementMethod.onTouchEvent(this, new SpannableString(this.getText()), e);
      }
    }
    gestureDetector.onTouchEvent(e);
    return true;
  }

  private Rect getMeasuredTextBounds() {
    String s = this.getText().toString();

    Rect bounds = new Rect();
    TextPaint textPaint = this.getPaint();

    textPaint.getTextBounds(s, 0, s.length(), bounds);
    int baseline = this.getBaseline();
    bounds.top = baseline + bounds.top;
    bounds.bottom = bounds.top + this.getMeasuredHeight() ;
    int startPadding = this.getPaddingStart();
    bounds.left += startPadding;

    bounds.right = (int) textPaint.measureText(s, 0, s.length()) + startPadding;
    return bounds;
  }

  public void updateBackgroundColor() {
    int bgColor = cell.cellBackgroundColorValid ? cell.cellBackgroundColor : Color.TRANSPARENT;
    int fgColor = cell.cellForegroundColorValid ? cell.cellForegroundColor : tableView.cellContentStyle.color;
    int color = selected ? TableTheme.selectedBackground : bgColor ;
    int textColor = selected ? Color.WHITE : fgColor ;
    cellView.setBackgroundColor(color);
    setBackgroundColor(color);
    setTextColor(textColor);
    postInvalidate();
    if(shouldAnimate) {
      startAnimation(fadeIn);
    }
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
      setTextColor(cell.cellForegroundColorValid ? cell.cellForegroundColor : tableView.cellContentStyle.color);
      setBackgroundColor(cell.cellBackgroundColorValid ? cell.cellBackgroundColor : Color.TRANSPARENT);
      setText(cell.qText);
      textWrapper.countWords(cell.qText);
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
    textWrapper.countWords(spannable.toString());
  }

  @Override
  public DataCell getCell() {
    return this.cell;
  }

  @Override
  public void setSelected(boolean value) {
    selected = value;
  }

  public boolean isSelected(){
    return selected;
  }

  @Override
  public void setMaxLines(int maxLines) {
    maxLines = textWrapper.setMaxLines(maxLines);
    super.setMaxLines(maxLines);
  }

  public void testTextWrap(DataColumn dataColumn) {
    if(tableView.cellContentStyle.wrap) {
      textWrapper.testTextWrap(dataColumn);
    }
  }

  public int getMeasuredLineCount() {
    return textWrapper.getMeasuredLinedCount();
  }

  public int measureLines(DataColumn dataColumn) {
    return textWrapper.getMeasureLinedCount(dataColumn);
  }
}
