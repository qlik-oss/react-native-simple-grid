package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TableViewFactory {
  public int extraTopMargin = 0;
  public HeaderViewFactory headerViewFactory = null;
  public CustomHorizontalScrollView scrollView = null;
  public RootLayout rootLayout = null;
  public HeaderView headerView = null;
  public RowCountView rowCountView = null;
  public TotalsView totalsView = null;
  public CustomRecyclerView firstColumnRecyclerView = null;
  public CustomRecyclerView coupledRecyclerView = null;
  public List<GrabberView> grabbers = null;
  public List<TotalsCell> totalsCells = null;
  public HeaderCell firstColumnHeaderCell = null;
  public TextView firstColumnTotalsCell = null;
  public ScreenGuideView screenGuideView = null;
  private List<DataColumn> dataColumns = null;
  public String totalsPosition;
  private final Context context;
  private final ColumnWidths columnWidths;
  private final DataProvider dataProvider;
  private final TableView tableView;
  private final DragBox dragBox;
  private final DragBox firstColumnDragBox;

  public TableViewFactory(TableView tableView, ColumnWidths columnWidths, DataProvider dataProvider, DragBox dragBox, DragBox firstColumnDragBox) {
    this.tableView = tableView;
    this.columnWidths = columnWidths;
    this.dataProvider = dataProvider;
    this.context = tableView.getContext();
    this.dragBox = dragBox;
    this.firstColumnDragBox = firstColumnDragBox;
  }

  public void createAll() {
    this.dataColumns = dataProvider.dataColumns;
    this.totalsCells = dataProvider.totalsCells;
    this.totalsPosition = dataProvider.totalsPosition;

    updateRowHeights();
    createHeaderFactory();
    createScrollView();

    tableView.addView(scrollView);
    scrollView.addView(rootLayout);

    if(tableView.headerContentStyle.wrap) {
      headerView.testTextWrap();
      updateFirstColumnHeaderHeight();
      updateTotalsViewHeight();
    }
    tableView.post(new Runnable() {
      @Override
      public void run() {
        updateGrabbers();
      }
    });
  }

  protected void updateRowHeights() {
    tableView.themedRowHeight = tableView.cellContentStyle.rowHeight * TableTheme.rowHeightFactor;
    tableView.rowHeight = tableView.themedRowHeight;
  }

  protected void createScrollView() {
    this.scrollView = new CustomHorizontalScrollView(context);
    this.scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT));
    this.scrollView.setFillViewport(true);

    createRootLayout();
  }

  protected void createRootLayout() {
    FrameLayout.LayoutParams frameLayout = new FrameLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT);
    this.rootLayout = new RootLayout(context, columnWidths);
    this.rootLayout.setPadding(0, 0, (int) PixelUtils.dpToPx(25), 0);
    this.rootLayout.setLayoutParams(frameLayout);
    this.rootLayout.addView(dragBox);
    this.rootLayout.setZ(PixelUtils.dpToPx(1));

    createHeaderView();
  }

  protected void createHeaderView() {
    headerView = headerViewFactory.getHeaderView();
    rootLayout.addView(headerView);

    createTotalsView();
  }

  protected void createTotalsView() {
    totalsView = headerViewFactory.getTotalsView();
    if(totalsView != null) {
      rootLayout.addView(totalsView);
    }

    createRecyclerViews();
  }

  protected void createRecyclerViews() {
    CustomLinearLayoutManger linearLayout = new CustomLinearLayoutManger(context);
    coupledRecyclerView = new CustomRecyclerView(context, false, dataProvider, tableView, linearLayout, dragBox, firstColumnDragBox);
    FrameLayout.LayoutParams recyclerViewLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    coupledRecyclerView.setLayoutParams(recyclerViewLayoutParams);
    CustomLinearLayoutManger firstColumnLinearLayout = new CustomLinearLayoutManger(context);
    firstColumnRecyclerView = new CustomRecyclerView(context, true, dataProvider, tableView, firstColumnLinearLayout, dragBox, firstColumnDragBox);
    coupledRecyclerView.setZ(0);
    coupledRecyclerView.setElevation(0);

    headerView.post(() -> {
      int headerHeight = headerView.getMeasuredHeight();
      int marginTop = headerHeight + extraTopMargin;
      int marginBottom = TableTheme.rowHeightFactor;

      linearLayout.recyclerView = coupledRecyclerView;
      coupledRecyclerView.setAdapter(dataProvider);

      recyclerViewLayoutParams.topMargin = marginTop;
      recyclerViewLayoutParams.bottomMargin = marginBottom;
      rootLayout.addView(coupledRecyclerView, recyclerViewLayoutParams);

      firstColumnLinearLayout.recyclerView = firstColumnRecyclerView;
      firstColumnRecyclerView.setAdapter(dataProvider);
      FrameLayout.LayoutParams firstColumnViewLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
      firstColumnViewLayoutParams.topMargin = marginTop;
      firstColumnViewLayoutParams.bottomMargin = marginBottom;

      if(tableView.isFirstColumnFrozen) {
        firstColumnHeaderCell = HeaderViewFactory.buildFixedColumnCell(rootLayout, dataColumns.get(0), tableView, headerViewFactory.topPosition);
        dataProvider.setFirstColumnFrozen(true);
        coupledRecyclerView.setViewToScrollCouple(firstColumnRecyclerView);
        firstColumnRecyclerView.setViewToScrollCouple(coupledRecyclerView);

        if(totalsCells != null) {
          firstColumnTotalsCell = HeaderViewFactory.buildFixedTotalsCell(tableView, dataColumns.get(0), totalsCells.get(0), headerViewFactory.topPosition);
          tableView.addView(firstColumnTotalsCell);
        }
        tableView.addView(firstColumnRecyclerView, firstColumnViewLayoutParams);
        tableView.addView(firstColumnHeaderCell);
        tableView.addView(firstColumnDragBox);
      }

      createRowCount();
    });

  }

  protected void createRowCount() {
    rowCountView = new RowCountView(context, tableView);
    coupledRecyclerView.setRowCountView(rowCountView);
    tableView.addView(rowCountView);

    createGrabbers();
  }

  protected void createGrabbers() {
    if (grabbers == null) {
      grabbers = new ArrayList<>();
      List<DataColumn> dataColumns = dataProvider.getDataColumns();
      int dragWidth = (int) PixelUtils.dpToPx(40);
      int offset = dragWidth / 2;
      int startOffset = 0;

      for (int i = 0; i < dataColumns.size(); i++) {
        GrabberView grabberView = new GrabberView(i, context, scrollView, tableView);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dragWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        startOffset += (dataColumns.get(i).width) - offset;
        offset = 0;

        grabberView.setLayoutParams(layoutParams);
        grabberView.setBackgroundColor(Color.TRANSPARENT);
        grabberView.setTranslationX(startOffset);
        grabbers.add(grabberView);
        if (tableView.isFirstColumnFrozen && i == 0) {
          tableView.addView(grabberView);
          grabberView.setElevation(PixelUtils.dpToPx(19));
        } else {
          rootLayout.addView(grabberView);
        }
      }
    }

    setupGrabbers();
  }

  protected void setupGrabbers() {
    for (GrabberView view : grabbers) {
      view.setDataProvider(dataProvider);
      view.setHeaderView(headerView);
      view.setGrabbers(grabbers);
      view.setRecyclerView(coupledRecyclerView);
      view.rootLayout = this.rootLayout;
      view.setFirstColumnRecyclerView(firstColumnRecyclerView);
      view.setFirstColumnHeader(firstColumnHeaderCell);
      view.setFixedTotalsCell(firstColumnTotalsCell);
      view.updateLayout();
    }
  }

  protected void createScreenGuide(int width) {
    if (screenGuideView == null && grabbers != null) {
      screenGuideView = new ScreenGuideView(context);
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
      params.gravity = Gravity.CENTER_VERTICAL;
      rootLayout.addView(screenGuideView, params);
      for (GrabberView grabberView : grabbers) {
        grabberView.setScreenGuideView(screenGuideView);
      }
    }
  }

  void invalidateLayout() {
    if (this.coupledRecyclerView != null) {
      int width = dataColumns.get(0).width;

      if (this.firstColumnHeaderCell != null) {
        ViewGroup.LayoutParams params = firstColumnHeaderCell.getLayoutParams();
        params.width = width;
        firstColumnHeaderCell.setLayoutParams(params);
        firstColumnHeaderCell.requestLayout();
      }
      if(this.firstColumnTotalsCell != null) {
        ViewGroup.LayoutParams params = firstColumnTotalsCell.getLayoutParams();
        params.width = width;
        firstColumnTotalsCell.setLayoutParams(params);
        firstColumnTotalsCell.requestLayout();
      }
      if(this.totalsView != null) {
        this.totalsView.updateLayout();
        this.totalsView.requestLayout();
      }

      this.headerView.updateLayout();
      this.headerView.requestLayout();

      this.dataProvider.invalidateLayout();
      if (this.firstColumnRecyclerView != null) {
        this.firstColumnRecyclerView.requestLayout();
      }
      this.coupledRecyclerView.requestLayout();

      updateGrabbers();

      this.rootLayout.requestLayout();
      this.scrollView.requestLayout();
    }
  }

  private void createHeaderFactory() {
    boolean topPosition = false;
    if(this.totalsCells != null) {
      switch(this.totalsPosition) {
        case "bottom":
          topPosition = false;
          break;
        case "noTotals":
        case "top":
        default:
          topPosition = true;
          extraTopMargin = TableTheme.rowHeightFactor;
          break;
      }

    }
    this.headerViewFactory = new HeaderViewFactory(dataColumns, totalsCells, dataProvider.totalsLabel, topPosition, tableView, tableView.headerContentStyle, context);
  }

  public void updateGrabbers() {
    if (grabbers != null) {
      int maxLineHeight = headerView.getMaxLineCount();
      int headerHeight = maxLineHeight * TableTheme.rowHeightFactor;
      List<DataColumn> dataColumns = dataProvider.getDataColumns();
      int dragWidth = (int) PixelUtils.dpToPx(40);
      int offset = dragWidth / 2;
      int startOffset = 0;

      for (int i = 0; i < dataColumns.size(); i++) {
        GrabberView grabberView = grabbers.get(i);
        startOffset += (dataColumns.get(i).width) - offset;
        offset = 0;
        grabberView.setTranslationX(startOffset);
        grabberView.setHeaderHeight(headerHeight);
        grabberView.postInvalidate();
      }
    }
  }

  private void updateScreenGuide() {
    if(screenGuideView == null) {
      return;
    }
    ViewGroup.LayoutParams params = screenGuideView.getLayoutParams();
    params.width = tableView.getWidth();
    screenGuideView.setLayoutParams(params);
  }

  public void updateHeaderViewLineCount() {
    int maxLineCount = headerView.getMaxLineCount();
    int headerHeight = maxLineCount * TableTheme.rowHeightFactor;
    tableView.headerHeight = headerHeight;

    ViewGroup.LayoutParams params = headerView.getLayoutParams();
    params.height = headerHeight;
    headerView.setLayoutParams(params);
    FrameLayout.LayoutParams recyclerParams = (FrameLayout.LayoutParams) coupledRecyclerView.getLayoutParams();
    recyclerParams.topMargin = headerHeight + TableTheme.rowHeightFactor;
    coupledRecyclerView.setLayoutParams(recyclerParams);

    updateTotalsViewHeight();
    updateFirstColumnsHeights();

    tableView.post(new Runnable() {
      @Override
      public void run() {
        if(firstColumnRecyclerView != null) {
          firstColumnRecyclerView.requestLayout();
        }
        if(firstColumnHeaderCell != null) {
          firstColumnHeaderCell.requestLayout();
        }

        if(totalsView != null) {
          totalsView.requestLayout();
        }

        coupledRecyclerView.requestLayout();
        rootLayout.requestLayout();
        headerView.requestLayout();
        tableView.requestLayout();
      }
    });
  }

  public void updateFirstColumnsHeights() {
    if(firstColumnRecyclerView != null && tableView != null) {
      FrameLayout.LayoutParams dd = (FrameLayout.LayoutParams) firstColumnRecyclerView.getLayoutParams();
      if(dd != null) {
        dd.topMargin = tableView.headerHeight;
        firstColumnRecyclerView.setLayoutParams(dd);
        updateFirstColumnHeaderHeight();
      }
    }
  }

  public void updateFirstColumnHeaderHeight() {
    if(firstColumnHeaderCell != null) {
      ViewGroup.LayoutParams params = firstColumnHeaderCell.getLayoutParams();
      params.height = tableView.headerHeight;
      firstColumnHeaderCell.setLayoutParams(params);
    }
  }

  public void updateRecyclerViewLineCount(DataColumn column) {
    tableView.post(new Runnable() {
      @Override
      public void run() {
        coupledRecyclerView.updateLineHeight(column);
        coupledRecyclerView.requestLayout();
        rootLayout.requestLayout();
      }
    });
  }

  public void updateTotalsViewHeight() {
    if(totalsView != null) {
      FrameLayout.LayoutParams pp = (FrameLayout.LayoutParams)totalsView.getLayoutParams();
      pp.topMargin = tableView.headerHeight;
      totalsView.setLayoutParams(pp);
    }
  }
}
