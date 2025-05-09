package com.qliktrialreactnativestraighttable;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class ClickableTextView extends androidx.appcompat.widget.AppCompatTextView implements Content {
  DataColumn column;
  final CellView cellView;
  final SelectionsEngine selectionsEngine;
  final TableView tableView;
  String linkUrl = null;
  String linkLabel = null;
  DataCell cell = null;
  boolean isDataView = false;
  boolean selected = false;
  int defaultTextColor = Color.BLACK;
  Animation fadeIn;
  ClickableTextWrapper textWrapper;
  ClickableTextView(Context context, SelectionsEngine selectionsEngine, TableView tableView, CellView cellView, DataColumn column) {
    super(context);
    this.tableView = tableView;
    this.selectionsEngine = selectionsEngine;
    this.cellView = cellView;
    this.column = column;
    defaultTextColor = getCurrentTextColor();
    fadeIn = AnimationUtils.loadAnimation(context, R.anim.catalyst_fade_in);
    textWrapper = new ClickableTextWrapper(tableView, this);
  }

  public void setIsDataView(boolean isDataView) {
    this.isDataView = isDataView;
    if(cell != null) {
      cell.setIsDataView(isDataView);
    }
  }


  @Override
  public boolean handleTouch(MotionEvent e) {
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

  public void updateBackgroundColor(boolean shouldAnimate) {
    int bgColor = cell.showBackground ? cell.cellBackgroundColor : Color.TRANSPARENT;
    int fgColor = cell.showForeground ? cell.cellForegroundColor : tableView.cellContentStyle.color;
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
  public void toggleSelected() {
    this.selected = !selected;
  }

  private void setupUrl() {
    if(column.representation == null || column.stylingInfo == null) {
      return;
    }
    int urlLabelIndex = column.stylingInfo.indexOf("urlLabel");
    String urlLabel = "";
    if(urlLabelIndex != -1) {
      String qText = cell.qAttrExpValues.get(urlLabelIndex).qText;
      urlLabel = qText != null ? qText : "";
    } else {
      if(column.representation.urlPosition != null) {
        urlLabel = column.representation.urlPosition.equals("dimension") ? (column.representation.linkUrl != null ? column.representation.linkUrl : "") : (cell.qText != null ? cell.qText : "");
      } else {
        urlLabel = cell.qText == null ? "" : cell.qText;
      }
    }
    String urlText = cell.qText;
    int attrIndex = column.stylingInfo.indexOf("url");
    if(attrIndex != -1) {
      urlText = cell.qAttrExpValues.get(attrIndex).qText;
    }
    if(urlLabel.isEmpty()) {
      urlLabel = cell.qText != null ? cell.qText : "link";
    }
    if(urlText != null) {
      String spaceEncodedUrl = urlText.replaceAll(" ", "%20");
      this.linkUrl = spaceEncodedUrl;
    } else {
      this.linkUrl = "";
    }
    this.linkLabel = Html.escapeHtml(urlLabel);
  }

  @Override
  public void setCellData(DataCell cell, DataRow row, DataColumn column) {
    this.column = column;
    this.cell = cell;
    if(isDataView) {
      cell.cellForegroundColor = Color.BLACK;
      tableView.cellContentStyle.color = Color.BLACK;
    }
    
   
    cell.setIsDataView(isDataView);
    if(cell.indicator != null && cell.type.compareTo("miniChart") != 0) {
      buildSpannableText();
    } else {
      if(column.representation != null && column.representation.type.compareTo("image") == 0) {
        cell.qText = getImageLabel(cell, row, column); 
      }
      setText(cell.qText);
      textWrapper.countWords(cell.qText);
    }

    if(!isDataView) {
      setTextColor(cell.showForeground ? cell.cellForegroundColor : tableView.cellContentStyle.color);
      setBackgroundColor(cell.showBackground ? cell.cellBackgroundColor : Color.TRANSPARENT);
    }

    if(cell.type.equals("url")) {
      setupUrl();
    }
  }
  
  private String getImageLabel(DataCell cell, DataRow row, DataColumn column) {
    if(column.representation != null && column.stylingInfo != null) {
      int index = column.stylingInfo.indexOf("imageLabel");
      if(index == -1) {
        index = column.stylingInfo.indexOf("imageUrl");
      }
      if(index != -1 && cell.qAttrExpValues != null) {
        qValue value = cell.qAttrExpValues.values.get(index);
        if(value != null) {
          return value.qText;
        }
      }
    }
    return "";
  }

  private void buildSpannableText() {
    int textColor = cell.indicator.applySegmentColors ? cell.indicator.color : TableTheme.defaultTextColor;
    if(isDataView) {
      textColor = Color.BLACK;
      cell.indicator.color = Color.BLACK;
    }
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

  public void setMaxLines(int maxLines, DataColumn column) {
    maxLines = textWrapper.setMaxLines(maxLines, column);
    super.setMaxLines(maxLines);
  }

  public void testTextWrap(DataColumn dataColumn) {
    if(tableView.cellContentStyle.wrap) {
      textWrapper.testTextWrap(dataColumn);
    }
  }

  public int getMeasuredLineCount() {
    if(column != null && !column.isDim) {
      return 1;
    }
    return textWrapper.getMeasuredLinedCount();
  }

  public int measureLines(DataColumn dataColumn) {
    return textWrapper.getMeasureLinedCount(dataColumn);
  }

  public void copyToClipBoard() {
    String text = cell != null ? cell.qText : "";
    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText(text, text);
    clipboard.setPrimaryClip(clip);
  }

  public String getCopyMenuString() {
    return "copy";
  }

}
