package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.text.LineBreaker;
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
  private static final float headerZ = 4;
  List<DataColumn> dataColumns;
  final HeaderContentStyle headerContentStyle;
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
  public HeaderViewFactory(List<DataColumn> dataColumns, TableView tableView,  Context context, HeaderContentStyle contentStyle) {
    this.tableView = tableView;
    this.dataColumns = dataColumns;
    this.headerContentStyle = contentStyle;
    buildHeader(context);
  }

  public static HeaderCell buildFixedColumnCell(FrameLayout rootView, DataColumn column, TableView tableView) {
    int padding = (int) PixelUtils.dpToPx(16);

    HeaderCell fixedFirstHeaderCell = new HeaderCell(rootView.getContext(), column, tableView);
    fixedFirstHeaderCell.setMaxLines(1);
    fixedFirstHeaderCell.setTypeface(fixedFirstHeaderCell.getTypeface(), Typeface.BOLD);
    fixedFirstHeaderCell.setEllipsize(TextUtils.TruncateAt.END);
    fixedFirstHeaderCell.setTextColor(tableView.headerContentStyle.color);
    fixedFirstHeaderCell.setText(column.label);
    fixedFirstHeaderCell.setPadding(padding, 0, padding, 0);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(column.width, TableTheme.rowHeightFactor);
    fixedFirstHeaderCell.setTop(0);
    fixedFirstHeaderCell.setLeft(0);
    fixedFirstHeaderCell.setZ(headerZ);
    fixedFirstHeaderCell.setLayoutParams(layoutParams);
    fixedFirstHeaderCell.setGravity(Gravity.CENTER_VERTICAL);
    fixedFirstHeaderCell.setBackgroundColor(TableTheme.headerBackgroundColor);
    fixedFirstHeaderCell.setElevation((int)PixelUtils.dpToPx(4));

    return fixedFirstHeaderCell;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void buildHeader(Context context) {
    int padding = (int) PixelUtils.dpToPx(16);
    int headerHeight = TableTheme.rowHeightFactor;
    headerView = new HeaderView(context);
    headerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight));
    headerView.setOrientation(LinearLayout.HORIZONTAL);
    headerView.setElevation((int)PixelUtils.dpToPx(4));
    headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    for(int i = 0; i < dataColumns.size(); i++) {

      DataColumn column = dataColumns.get(i);

      HeaderCell text = new HeaderCell(headerView.getContext(), column, this.tableView);
      text.setTypeface(text.getTypeface(), Typeface.BOLD);
      text.setEllipsize(TextUtils.TruncateAt.END);
      text.setTextColor(headerContentStyle.color);
      text.setText(column.label);
      text.setMaxLines(1);
      text.setPadding(padding, 0, padding, 0);
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(column.width, ViewGroup.LayoutParams.MATCH_PARENT);
      text.setLayoutParams(layoutParams);
      text.setGravity(Gravity.CENTER_VERTICAL);
      text.setZ(headerZ);
      text.setBackgroundColor(headerContentStyle.backgroundColor);
      headerView.addView(text);
    }
    headerView.setDataColumns(dataColumns);
  }

}
