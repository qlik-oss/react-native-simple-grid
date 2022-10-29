package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderViewFactory {
  List<DataColumn> dataColumns = new ArrayList<>();
  List<TotalsCell> totalsCells = new ArrayList<>();
  HeaderView headerView = null;
  AutoLinearLayout footerView = null;
  TableView tableView;
  public AutoLinearLayout getFooterView() {
    return footerView;
  }

  public HeaderView getHeaderView() {
    return headerView;
  }

  public List<DataColumn> getDataColumns() {
    return dataColumns;
  }


  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public HeaderViewFactory(List<DataColumn> dataColumns, TableView tableView,  Context context) {
    this.tableView = tableView;
    this.dataColumns = dataColumns;
    buildHeader(context);
  }

  public static HeaderCell buildFixedColumnCell(FrameLayout rootView, DataColumn column, TableView tableView) {
    int padding = (int) PixelUtils.dpToPx(16);

    HeaderCell fixedFirstHeaderCell = new HeaderCell(rootView.getContext(), column, tableView);
    fixedFirstHeaderCell.setMaxLines(1);
    fixedFirstHeaderCell.setTypeface(fixedFirstHeaderCell.getTypeface(), Typeface.BOLD);
    fixedFirstHeaderCell.setEllipsize(TextUtils.TruncateAt.END);
    fixedFirstHeaderCell.setTextColor(Color.BLACK);
    fixedFirstHeaderCell.setText(column.label);
    fixedFirstHeaderCell.setPadding(padding, 0, padding, 0);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)column.width, TableTheme.headerHeight);
    fixedFirstHeaderCell.setTop(0);
    fixedFirstHeaderCell.setLeft(0);
    fixedFirstHeaderCell.setLayoutParams(layoutParams);
    fixedFirstHeaderCell.setGravity(Gravity.CENTER_VERTICAL);
    fixedFirstHeaderCell.setBackgroundColor(TableTheme.headerBackgroundColor);
    fixedFirstHeaderCell.setElevation((int)PixelUtils.dpToPx(4));

    return fixedFirstHeaderCell;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void buildHeader(Context context) {
    int padding = (int) PixelUtils.dpToPx(16);
    int headerHeight = TableTheme.headerHeight;
    headerView = new HeaderView(context);
    headerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight));
    headerView.setOrientation(LinearLayout.HORIZONTAL);
    headerView.setElevation((int)PixelUtils.dpToPx(4));
    headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    for(int i = 0; i < dataColumns.size(); i++) {

      DataColumn column = dataColumns.get(i);

      TextView text = new HeaderCell(headerView.getContext(), column, this.tableView);
      text.setMaxLines(1);
      text.setTypeface(text.getTypeface(), Typeface.BOLD);
      text.setEllipsize(TextUtils.TruncateAt.END);
      text.setTextColor(Color.BLACK);
      text.setText(column.label);
      text.setPadding(padding, 0, padding, 0);
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)column.width, ViewGroup.LayoutParams.MATCH_PARENT);
      text.setLayoutParams(layoutParams);
      text.setGravity(Gravity.CENTER_VERTICAL);
      headerView.addView(text);
    }
    headerView.setDataColumns(dataColumns);

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
      text.setLayoutParams(new LinearLayout.LayoutParams((int)column.width, TableTheme.headerHeight));
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
      TextView text = new HeaderCell(context, column, this.tableView);
      int padding = (int) PixelUtils.dpToPx(16);
      text.setMaxLines(1);
      text.setTypeface(text.getTypeface(), Typeface.BOLD);
      text.setEllipsize(TextUtils.TruncateAt.END);
      text.setText(column.label);
      text.setPadding(padding, padding, padding, padding);
      text.setLayoutParams(new LinearLayout.LayoutParams((int)column.width, TableTheme.headerHeight));
      headerView.addView(text);
    }
    headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    headerView.invalidate();
  }
}
