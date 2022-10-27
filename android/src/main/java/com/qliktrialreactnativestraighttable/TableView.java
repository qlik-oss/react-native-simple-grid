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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TableView extends FrameLayout {
  public final static int SCROLL_THUMB_HEIGHT = 12;
  final FrameLayout rootView;
  final RootLayout layoutView;
  CustomHorizontalScrollView scrollView;
  HeaderView headerView = null;
  AutoLinearLayout footerView = null;
  CustomRecyclerView recyclerView = null;
  CustomRecyclerView firstColumnView = null;
  HeaderCell firstColumnHeaderCell = null;
  ScreenGuideView screenGuideView = null;
  SelectionsEngine selectionsEngine = new SelectionsEngine();
  DataProvider dataProvider = new DataProvider();
  boolean isFirstColumnFrozen = false;

  TableTheme tableTheme = new TableTheme();
  List<GrabberView> grabbers = null;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  TableView(Context context, CustomHorizontalScrollView scrollView, FrameLayout rootView) {
    super(context);
    this.scrollView = scrollView;
    this.rootView = rootView;
    this.layoutView = new RootLayout(context);
    dataProvider.selectionsEngine = selectionsEngine;
    dataProvider.scrollView = scrollView;
    decorate();
    this.addView(layoutView);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public void decorate() {
    GradientDrawable drawable = new GradientDrawable();
    GradientDrawable border = new GradientDrawable();
    border.setStroke((int)PixelUtils.dpToPx(1), TableTheme.borderBackgroundColor);
    border.setCornerRadius(TableTheme.borderRadius);
    drawable.setCornerRadius(TableTheme.borderRadius);
    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    layoutParams.rightMargin = (int)PixelUtils.dpToPx(50);
    rootView.setLayoutParams(layoutParams);
    rootView.setClipToOutline(true);
    rootView.setBackground(drawable);
    rootView.setForeground(border);
  }

  public void clearSelections() {
    selectionsEngine.clearSelections();
  }

  public void setHeaderView(HeaderView view) {
    if (this.headerView == null) {
      this.headerView = view;
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableTheme.headerHeight);
      params.gravity = Gravity.TOP;
      layoutView.addView(this.headerView, params);
    }
  }

  public void setFirstColumnFrozen(boolean shouldFreeze) {
    isFirstColumnFrozen = shouldFreeze;
    dataProvider.setFirstColumnFrozen(shouldFreeze);
    if (dataProvider.ready()) {
      createRecyclerView();
    }
  }

  public HeaderView getHeaderView() {
    return this.headerView;
  }

  public void setFooterView(AutoLinearLayout view) {
    if (this.footerView == null) {
      this.footerView = view;
      if (this.footerView != null) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableTheme.headerHeight);
        params.gravity = Gravity.BOTTOM;
        layoutView.addView(this.footerView, params);
      }
    }
  }

  public void updateTheme() {
    if (headerView != null) {
      headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    }
  }

  public void setDataColumns(List<DataColumn> cols) {
    dataProvider.setDataColumns(cols);
    if (dataProvider.getDataColumns() == null && dataProvider.ready()) {
      createRecyclerView();
    } else {
      dataProvider.updateRepresentation();
    }
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
      this.firstColumnView.requestLayout();
    }

    if (resetData) {
      if (this.headerView != null && dataProvider.ready()) {
        createRecyclerView();
      }
      selectionsEngine.clearSelections();
    }
  }

  public void setDataSize(DataSize dataSize) {
    dataProvider.setDataSize(dataSize);
    if (this.headerView != null && dataProvider.ready()) {
      createRecyclerView();
    }
  }

  void createRecyclerView() {
    if (recyclerView == null) {
      createDataColumnWidths();
      createGrabbers();

      LinearLayoutManager linearLayout = new LinearLayoutManager(this.getContext());

      recyclerView = new CustomRecyclerView(this.getContext(), false, dataProvider, scrollView, linearLayout);
      LayoutParams recyclerViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      recyclerViewLayoutParams.topMargin = TableTheme.headerHeight;

      if (footerView != null) {
        recyclerViewLayoutParams.bottomMargin = TableTheme.headerHeight;
      }

      LinearLayoutManager firstColumnLinearLayout = new LinearLayoutManager(this.getContext());
      firstColumnView = new CustomRecyclerView(this.getContext(), true, dataProvider, scrollView, firstColumnLinearLayout);
      LayoutParams firstColumnViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
      firstColumnViewLayoutParams.topMargin = TableTheme.headerHeight;
      firstColumnViewLayoutParams.bottomMargin = SCROLL_THUMB_HEIGHT;

      if (footerView != null) {
        firstColumnViewLayoutParams.bottomMargin = SCROLL_THUMB_HEIGHT + TableTheme.headerHeight;
      }

      layoutView.addView(recyclerView, recyclerViewLayoutParams);
      if (rootView != null && isFirstColumnFrozen) {
        firstColumnHeaderCell = HeaderViewFactory.buildFixedColumnCell(rootView, dataProvider.dataColumns.get(0), scrollView);
        recyclerView.setViewToScrollCouple(firstColumnView);
        firstColumnView.setViewToScrollCouple(recyclerView);
        rootView.addView(firstColumnView, firstColumnViewLayoutParams);
      }

      setupGrabbers();
    }
  }

  private void createDataColumnWidths() {
    ColumnWidthFactory columnWidthFactory = new ColumnWidthFactory(dataProvider.dataColumns,
      dataProvider.rows,
      this.getContext(),
      this.headerView,
      this.scrollView,
      this);

    columnWidthFactory.autoSize(scrollView);
  }

  private void setupGrabbers() {
    for(GrabberView view : grabbers) {
      view.setDataProvider(dataProvider);
      view.setHeaderView(headerView);
      view.setGrabbers(grabbers);
      view.setRecyclerView(recyclerView);
      view.setFirstColumnRecyclerView(firstColumnView);
      view.setFirstColumnHeader(firstColumnHeaderCell);
      view.setFooterView(footerView);
    }
  }

  private void createGrabbers() {
    grabbers = new ArrayList<>();
    List<DataColumn> dataColumns = dataProvider.getDataColumns();
    int dragWidth = (int) PixelUtils.dpToPx(40);
    int offset = dragWidth / 2;
    int startOffset = 0;
    for(int i = 0; i < dataColumns.size(); i++) {
      GrabberView grabberView = new GrabberView(i, getContext(), scrollView, rootView);
      LayoutParams layoutParams = new LayoutParams(dragWidth, ViewGroup.LayoutParams.MATCH_PARENT);
      startOffset += (dataColumns.get(i).width) - offset;
      offset = 0;

      grabberView.setLayoutParams(layoutParams);
      grabberView.setBackgroundColor(Color.TRANSPARENT);
      grabberView.setTranslationX(startOffset);
      grabbers.add(grabberView);
      if (i == 0 && isFirstColumnFrozen) {
        rootView.addView(grabberView);
        continue;
      }
      this.addView(grabberView);
    }
  }

  public void createScreenGuide(int width) {
    if (screenGuideView == null && grabbers != null) {
      screenGuideView = new ScreenGuideView(this.getContext());
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
      params.gravity = Gravity.CENTER_VERTICAL;
      rootView.addView(screenGuideView, params);
      for (GrabberView grabberView : grabbers) {
        grabberView.setGreenGuideView(screenGuideView);
      }
    }
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
}
