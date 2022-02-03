package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReadableArray;

import java.util.ArrayList;
import java.util.List;

public class HeaderViewFactory {
  List<DataColumn> dataColumns = new ArrayList<>();
  List<TotalsCell> totalsCells = new ArrayList<>();
  AutoLinearLayout headerView = null ;
  AutoLinearLayout footerView = null;

  public AutoLinearLayout getFooterView() {
    return footerView;
  }

  public AutoLinearLayout getHeaderView() {
    return headerView;
  }

  public List<DataColumn> getDataColumns() {
    return dataColumns;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public HeaderViewFactory(ReadableArray readableArray, ReadableArray footerArray, Context context) {
    if(readableArray != null) {
      buildHeader(readableArray, context);
    }
    if(footerArray != null) {
      buildFooter(footerArray, context);
    }
  }

  public HeaderViewFactory(AutoLinearLayout headerView) {
    this.headerView = headerView;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void buildHeader(ReadableArray readableArray, Context context) {
    headerView = new AutoLinearLayout(context);
    headerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableTheme.headerHeight));
    headerView.setOrientation(LinearLayout.HORIZONTAL);
    headerView.setElevation((int)PixelUtils.dpToPx(4));
    headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    for(int i = 0; i < readableArray.size(); i++) {
      DataColumn column = new DataColumn(readableArray.getMap(i));
      dataColumns.add(column);
      TextView text = new TextView(context);
      int padding = (int) PixelUtils.dpToPx(16);
      text.setMaxLines(1);
      text.setTypeface(text.getTypeface(), Typeface.BOLD);
      text.setEllipsize(TextUtils.TruncateAt.END);
      text.setText(column.label);
      text.setPadding(padding, padding, padding, padding);
      text.setLayoutParams(new LinearLayout.LayoutParams(column.width, TableTheme.headerHeight));
      headerView.addView(text);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void buildFooter(ReadableArray readableArray, Context context) {

    buildTotalCells(readableArray);

    footerView = new AutoLinearLayout(context);
    footerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableTheme.headerHeight));
    footerView.setOrientation(LinearLayout.HORIZONTAL);
    footerView.setElevation((int)PixelUtils.dpToPx(4));
    footerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    // first add all fillers, then populate with the data
    int j = 0;
    for(int i = 0; i < dataColumns.size(); i++ ) {
      DataColumn column = dataColumns.get(i);
      TextView text = new TextView(context);
      int padding = (int) PixelUtils.dpToPx(16);
      text.setMaxLines(1);
      text.setTypeface(text.getTypeface(), Typeface.BOLD);
      text.setEllipsize(TextUtils.TruncateAt.END);
      if(column.isDim && i == 0) {
        text.setText("Totals");
        text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
      }
      if (!column.isDim) {
        if (j < totalsCells.size()) {
          text.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
          text.setText(totalsCells.get(j++).qText);
        }
      }
      text.setPadding(padding, padding, padding, padding);
      text.setLayoutParams(new LinearLayout.LayoutParams(column.width, TableTheme.headerHeight));
      footerView.addView(text);
    }
  }

  void buildTotalCells(ReadableArray source) {
    for(int i = 0; i < source.size(); i++) {
      TotalsCell cell = new TotalsCell(source.getMap(i));
      totalsCells.add(cell);
    }
  }

  void readjustLayout(List<DataColumn> dataColumns, Context context) {
    headerView.removeAllViews();
    for(DataColumn column: dataColumns) {
      TextView text = new TextView(context);
      int padding = (int) PixelUtils.dpToPx(16);
      text.setMaxLines(1);
      text.setTypeface(text.getTypeface(), Typeface.BOLD);
      text.setEllipsize(TextUtils.TruncateAt.END);
      text.setText(column.label);
      text.setPadding(padding, padding, padding, padding);
      text.setLayoutParams(new LinearLayout.LayoutParams(column.width, TableTheme.headerHeight));
      headerView.addView(text);
    }

  }
}
