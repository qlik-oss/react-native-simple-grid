package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReadableArray;

import java.util.ArrayList;
import java.util.List;

public class HeaderViewFactory {
  private static final float headerZ = 2;
  List<TotalsCell> totalsCells = new ArrayList<>();
  List<DataColumn> dataColumns;
  final HeaderContentStyle headerContentStyle;
  HeaderViewFactory headerViewFactory;
  String totalsLabel;
  boolean topPosition;
  boolean bottomPosition;
  HeaderView headerView = null;
  TotalsView totalsView = null;
  TableView tableView;

  public TotalsView getTotalsView() {
    return totalsView;
  }

  public HeaderView getHeaderView() {
    return headerView;
  }

  public List<DataColumn> getDataColumns() {
    return dataColumns;
  }

  public HeaderViewFactory(List<DataColumn> dataColumns, List<TotalsCell> totalsCells, String totalsLabel, String totalsPosition, TableView tableView, HeaderContentStyle contentStyle, Context context) {
    this.tableView = tableView;
    this.dataColumns = dataColumns;
    this.totalsCells = totalsCells;
    this.totalsLabel = totalsLabel;
    this.topPosition = totalsPosition.equals("top");
    this.bottomPosition = totalsPosition.equals("bottom");
    this.headerContentStyle = contentStyle;
    buildHeader(context);
    if (totalsCells != null) {
      buildTotals(context);
    }
  }

  public static HeaderCell buildFixedColumnCell(FrameLayout rootView, DataColumn column, TableView tableView, boolean topPosition) {
    int headerHeight = tableView.headerHeight;
    int padding = TableTheme.CellPadding;

    HeaderCell fixedFirstHeaderCell = new HeaderCell(rootView.getContext(), column, tableView);
    TextView textView = fixedFirstHeaderCell.cell;
    textView.setMaxLines(1);
    textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
    textView.setTextSize(tableView.headerContentStyle.fontSize);
    textView.setEllipsize(TextUtils.TruncateAt.END);
    textView.setTextColor(tableView.headerContentStyle.color);
    textView.setText(column.label);
    textView.setPadding(padding, 0, 0, 0);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(column.width, headerHeight);
    fixedFirstHeaderCell.setTop(0);
    fixedFirstHeaderCell.setLeft(0);
    fixedFirstHeaderCell.setZ(PixelUtils.dpToPx(headerZ));
    fixedFirstHeaderCell.setLayoutParams(layoutParams);
    fixedFirstHeaderCell.setGravity(column.textAlignment | Gravity.CENTER_VERTICAL);
    fixedFirstHeaderCell.setBackgroundColor(TableTheme.headerBackgroundColor);

    if (topPosition) {
      fixedFirstHeaderCell.setOutlineProvider(null);
    }
    return fixedFirstHeaderCell;
  }

  public static TotalsViewCell buildFixedTotalsCell(TableView tableView, DataColumn column, TotalsCell totalsCell, boolean topPosition) {
    int padding = TableTheme.CellPadding;
    TotalsViewCell text = new TotalsViewCell(tableView.getContext(), column, tableView);
    text.setTypeface(text.getTypeface(), Typeface.BOLD);
    text.setEllipsize(TextUtils.TruncateAt.END);
    if (column.isDim) {
      text.setText(tableView.totalsLabel);
      text.setGravity(column.textAlignment | Gravity.CENTER_VERTICAL);
    } else {
      text.setGravity(column.textAlignment | Gravity.CENTER_VERTICAL);
      text.setText(totalsCell.qText);
    }
    text.setPadding(padding, 0, padding, 0);
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(column.width, tableView.totalsHeight);
    if (topPosition) {
      params.gravity = Gravity.TOP;
      params.topMargin = tableView.headerHeight;
    } else {
      params.gravity = Gravity.BOTTOM;
      params.bottomMargin = TableTheme.DefaultRowHeight;
    }
    text.setLayoutParams(params);
    text.setTextColor(tableView.cellContentStyle.color);
    if(!tableView.isDataView) {
      text.setBackgroundColor(TableTheme.backgroundColor);
    }
    text.setZ((int) PixelUtils.dpToPx(headerZ));
    text.setTextSize(tableView.cellContentStyle.fontSize);
    text.setMaxLines(1);
    if (!topPosition) {
      text.setOutlineProvider(null);
    }

    return text;
  }

  public static HeaderCell createHeaderCell(Context context, DataColumn column, HeaderContentStyle headerContentStyle, TableView tableView) {
    int padding = TableTheme.CellPadding;
    HeaderCell headerCell = new HeaderCell(context, column, tableView);
    int leftPadding = column.textAlignment == Gravity.LEFT ? padding : 0;
    int rightPadding  = column.textAlignment == Gravity.RIGHT ? padding : 0;
    headerCell.setPadding(leftPadding, 0, rightPadding, 0);
    TextView text = headerCell.cell;
    text.setTypeface(text.getTypeface(), Typeface.BOLD);
    text.setEllipsize(TextUtils.TruncateAt.END);
    text.setTextColor(headerContentStyle.color);
    text.setText(column.label);
    text.setMaxLines(1);
    text.setTextSize(headerContentStyle.fontSize);
    text.setBackgroundColor(headerContentStyle.backgroundColor);
    text.setGravity(column.textAlignment | Gravity.CENTER_VERTICAL);
    return headerCell;
  }

  private void buildHeader(Context context) {
    int headerHeight = tableView.headerHeight;
    headerView = new HeaderView(context, tableView);
    headerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight));
    headerView.setOrientation(LinearLayout.HORIZONTAL);
    headerView.setElevation((int) PixelUtils.dpToPx(headerZ));
    headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    for (int i = 0; i < dataColumns.size(); i++) {
      DataColumn column = dataColumns.get(i);
      HeaderCell headerCell = createHeaderCell(headerView.getContext(), column, headerContentStyle, this.tableView);
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(column.width, ViewGroup.LayoutParams.MATCH_PARENT);
      headerView.addView(headerCell, layoutParams);
    }
    headerView.setDataColumns(dataColumns);
  }

  public static TotalsViewCell createTotalsCell(Context context, DataColumn column, TableView tableView) {
    TotalsViewCell text = new TotalsViewCell(context, column, tableView);
    int padding = TableTheme.CellPadding;
    text.setTypeface(text.getTypeface(), Typeface.BOLD);
    text.setEllipsize(TextUtils.TruncateAt.END);
    text.setTextSize(tableView.cellContentStyle.fontSize);
    text.setGravity(column.textAlignment | Gravity.CENTER_VERTICAL);
    text.setPadding(padding, 0, padding, 0);
    text.setMaxLines(1);
    return text;
  }

  private void buildTotals(Context context) {
    totalsView = new TotalsView(context, tableView);
    totalsView.setDataColumns(dataColumns);
    if(!tableView.isDataView) {
      totalsView.setBackgroundColor(TableTheme.backgroundColor);
    }

    int headerHeight = tableView.headerHeight;
    totalsView.setOrientation(LinearLayout.HORIZONTAL);
    totalsView.setElevation((int) PixelUtils.dpToPx(headerZ));
    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, tableView.totalsHeight);
    layoutParams.topMargin = headerHeight;
    layoutParams.gravity = topPosition ? Gravity.TOP : Gravity.BOTTOM;
    totalsView.setLayoutParams(layoutParams);
    if (!topPosition) {
      totalsView.setOutlineProvider(null);
    }
    // first add all fillers, then populate with the data
    int j = 0;
    for (int i = 0; i < dataColumns.size(); i++) {
      DataColumn column = dataColumns.get(i);
      TotalsViewCell text = createTotalsCell(context, column, tableView);
      if (column.isDim && i == 0) {
        text.setText(totalsLabel);
      }
      if (!column.isDim) {
        if (j < totalsCells.size()) {
          text.setGravity(column.textAlignment | Gravity.CENTER_VERTICAL);
          text.setText(totalsCells.get(j++).qText);
        }
      }
      LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(column.width, ViewGroup.LayoutParams.MATCH_PARENT);
      totalsView.addView(text, textParams);
    }

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
