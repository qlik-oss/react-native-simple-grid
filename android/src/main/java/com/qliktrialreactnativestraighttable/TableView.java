package com.qliktrialreactnativestraighttable;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TableView extends FrameLayout {

  public final static int SCROLL_THUMB_HEIGHT = 12;
//  final FrameLayout rootView;
  RootLayout rootLayout;
  CustomHorizontalScrollView  scrollView;
  HeaderView headerView = null;
  AutoLinearLayout footerView = null;
  CustomRecyclerView recyclerView = null;
  CustomRecyclerView firstColumnView = null;
  HeaderCell firstColumnHeaderCell = null;
  ScreenGuideView screenGuideView = null;
  SelectionsEngine selectionsEngine = new SelectionsEngine();
  ColumnWidths columnWidths = new ColumnWidths();
  DataProvider dataProvider;
  boolean isFirstColumnFrozen = false;

  TableTheme tableTheme = new TableTheme();
  List<GrabberView> grabbers = null;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  TableView(Context context) {
    super(context);
    this.dataProvider = new DataProvider(columnWidths, selectionsEngine, this);
  }

  public void clearSelections() {
    selectionsEngine.clearSelections();
  }

  public void setFirstColumnFrozen(boolean shouldFreeze) {
    isFirstColumnFrozen = shouldFreeze;
    dataProvider.setFirstColumnFrozen(shouldFreeze);
  }

  public void updateTheme() {
    // no up for now
  }

  public void setDataColumns(List<DataColumn> cols) {
    dataProvider.setDataColumns(cols);
    columnWidths.updateWidths(cols);
    dataProvider.updateRepresentation();
  }

  public void setDataView(boolean isDataView) {
    dataProvider.setDataView(isDataView);
  }

  public List<DataColumn> getColumns() {
    return dataProvider.getDataColumns();
  }

  public void setRows(List<DataRow> rows, boolean resetData) {
    dataProvider.setRows(rows, resetData);
    if (this.recyclerView != null) {
      this.requestLayout();
      this.recyclerView.requestLayout();
    }

    if (resetData) {
      selectionsEngine.clearSelections();
    }
  }

  public void setDataSize(DataSize dataSize) {
    dataProvider.setDataSize(dataSize);
  }



  public void createScreenGuide(int width) {
//    if (screenGuideView == null && grabbers != null) {
//      screenGuideView = new ScreenGuideView(this.getContext());
//      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
//      params.gravity = Gravity.CENTER_VERTICAL;
//      for (GrabberView grabberView : grabbers) {
//        grabberView.setGreenGuideView(screenGuideView);
//      }
//    }
  }

  @Override
  public void requestLayout() {
    super.requestLayout();
    post(measureAndLayout);
  }

  private final Runnable measureAndLayout = () -> {
    measure(
      MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
    layout(getLeft(), getTop(), getRight(), getBottom());
  };

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    columnWidths.loadWidths(w, dataProvider.dataColumns);
    createRecyclerView(w, h);
    post(new Runnable() {
      @Override
      public void run() {
        scrollView.requestLayout();
        requestLayout();
      }
    });
  }

  void createRecyclerView(int width, int height) {
    if (recyclerView == null) {
      TableViewFactory tableViewFactory = new TableViewFactory(this, columnWidths, dataProvider);
      this.recyclerView = tableViewFactory.coupledRecyclerView;
      this.grabbers = tableViewFactory.grabbers;
      this.headerView = tableViewFactory.headerView;
      this.rootLayout = tableViewFactory.rootLayout;
      this.scrollView = tableViewFactory.scrollView;
    }
  }
}
