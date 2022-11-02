package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
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
  List<TotalsCell> totalsCells = new ArrayList<>();
  List<DataColumn> dataColumns;
  final HeaderContentStyle headerContentStyle;
  HeaderViewFactory headerViewFactory;
  String totalsLabel;
  boolean topPosition;
  HeaderView headerView = null;
  AutoLinearLayout totalsView = null;
  TableView tableView;

  public AutoLinearLayout getTotalsView() {
    return totalsView;
  }

  public HeaderView getHeaderView() {
    return headerView;
  }

  public List<DataColumn> getDataColumns() {
    return dataColumns;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public HeaderViewFactory(List<DataColumn> dataColumns, List<TotalsCell> totalsCells, String totalsLabel, boolean topPosition, TableView tableView, HeaderContentStyle contentStyle, Context context) {
    this.tableView = tableView;
    this.dataColumns = dataColumns;
    this.totalsCells = totalsCells;
    this.totalsLabel = totalsLabel;
    this.topPosition = topPosition;
    this.headerContentStyle = contentStyle;
    buildHeader(context);
    if (totalsCells != null) {
      buildTotals(context);
    }
  }

  public static HeaderCell buildFixedColumnCell(FrameLayout rootView, DataColumn column, TableView tableView, boolean topPosition) {
    int headerHeight = tableView.tableViewFactory.headerView.getMeasuredHeight();
    int padding = (int) PixelUtils.dpToPx(16);

    HeaderCell fixedFirstHeaderCell = new HeaderCell(rootView.getContext(), column, tableView);
    fixedFirstHeaderCell.setMaxLines(1);
    fixedFirstHeaderCell.setTypeface(fixedFirstHeaderCell.getTypeface(), Typeface.BOLD);
    fixedFirstHeaderCell.setEllipsize(TextUtils.TruncateAt.END);
    fixedFirstHeaderCell.setTextColor(tableView.headerContentStyle.color);
    fixedFirstHeaderCell.setText(column.label);
    fixedFirstHeaderCell.setPadding(padding, 0, padding, 0);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(column.width, headerHeight);
    fixedFirstHeaderCell.setTop(0);
    fixedFirstHeaderCell.setLeft(0);
    fixedFirstHeaderCell.setZ(PixelUtils.dpToPx(headerZ));
    fixedFirstHeaderCell.setLayoutParams(layoutParams);
    fixedFirstHeaderCell.setGravity(Gravity.CENTER_VERTICAL);
    fixedFirstHeaderCell.setBackgroundColor(TableTheme.headerBackgroundColor);
    fixedFirstHeaderCell.setElevation((int) PixelUtils.dpToPx(4));
    if (topPosition) {
      fixedFirstHeaderCell.setOutlineProvider(null);
    }
    return fixedFirstHeaderCell;
  }

  public static TextView buildFixedTotalsCell(TableView tableView, DataColumn column, TotalsCell totalsCell, boolean topPosition) {
    int headerHeight = tableView.tableViewFactory.headerView.getMeasuredHeight();
    int padding = (int) PixelUtils.dpToPx(16);
    TextView text = new TextView(tableView.getContext());
    text.setMaxLines(1);
    text.setTypeface(text.getTypeface(), Typeface.BOLD);
    text.setEllipsize(TextUtils.TruncateAt.END);
    if (column.isDim) {
      text.setText(tableView.totalsLabel);
      text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    } else {
      text.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
      text.setText(totalsCell.qText);
    }
    text.setPadding(padding, 0, padding, 0);
    text.setLayoutParams(new FrameLayout.LayoutParams(column.width, TableTheme.rowHeightFactor));
    text.setBackgroundColor(Color.WHITE);
    text.setZ((int) PixelUtils.dpToPx(headerZ));
    int y = topPosition ? headerHeight : tableView.getMeasuredHeight() - TableTheme.rowHeightFactor * 2;
    text.setY(y);
    if (!topPosition) {
      text.setOutlineProvider(null);
    }

    return text;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void buildHeader(Context context) {
    int padding = (int) PixelUtils.dpToPx(16);
    int headerHeight = TableTheme.rowHeightFactor;
    headerView = new HeaderView(context);
    headerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight));
    headerView.setOrientation(LinearLayout.HORIZONTAL);
    headerView.setElevation((int) PixelUtils.dpToPx(4));
    headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    for (int i = 0; i < dataColumns.size(); i++) {
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
      text.setZ(PixelUtils.dpToPx(headerZ));
      text.setBackgroundColor(headerContentStyle.backgroundColor);
      headerView.addView(text);
    }
    headerView.setDataColumns(dataColumns);
  }

  private void buildTotals(Context context) {
    totalsView = new AutoLinearLayout(context);

    totalsView.post(() -> {
      int headerHeight = headerView.getMeasuredHeight();
      int y = topPosition ? headerHeight : tableView.getMeasuredHeight() - TableTheme.rowHeightFactor * 2;
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, TableTheme.rowHeightFactor);
      params.topMargin = tableView.headerHeight;
      totalsView.setLayoutParams(params);
      totalsView.setOrientation(LinearLayout.HORIZONTAL);
      totalsView.setElevation((int) PixelUtils.dpToPx(4));
      totalsView.setZ((int) PixelUtils.dpToPx(headerZ));
      totalsView.setY(y);
      totalsView.setBackgroundColor(Color.WHITE);

      // first add all fillers, then populate with the data
      int j = 0;
      for (int i = 0; i < dataColumns.size(); i++) {
        DataColumn column = dataColumns.get(i);
        TextView text = new TextView(context);
        int padding = (int) PixelUtils.dpToPx(16);
        text.setMaxLines(1);
        text.setTypeface(text.getTypeface(), Typeface.BOLD);
        text.setEllipsize(TextUtils.TruncateAt.END);
        if (column.isDim && i == 0) {
          text.setText(totalsLabel);
          text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
        if (!column.isDim) {
          if (j < totalsCells.size()) {
            text.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            text.setText(totalsCells.get(j++).qText);
          }
        }
        text.setPadding(padding, 0, padding, 0);
        text.setLayoutParams(new LinearLayout.LayoutParams(column.width, TableTheme.rowHeightFactor));
        totalsView.addView(text);
      }
    });

  }

  public static List<TotalsCell> getTotalsCellList(ReadableArray source) {
    List<TotalsCell> totalsCells = new ArrayList<>();
    for (int i = 0; i < source.size(); i++) {
      TotalsCell cell = new TotalsCell(source.getMap(i));
      totalsCells.add(cell);
    }
    return totalsCells;
  }
}
