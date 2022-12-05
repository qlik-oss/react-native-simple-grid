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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TableViewFactory {
  public int extraTopMargin = 0;
  public int extraBottomMargin = 0;
  public HeaderViewFactory headerViewFactory = null;
  public CustomHorizontalScrollView scrollView = null;
  public MockVerticalScrollView verticalScrollBar = null;
  public MockHorizontalScrollView horizontalScrollView = null;
  public RootLayout rootLayout = null;
  public HeaderView headerView = null;
  public RowCountView rowCountView = null;
  public TotalsView totalsView = null;
  public CustomRecyclerView firstColumnRecyclerView = null;
  public CustomRecyclerView coupledRecyclerView = null;
  public List<GrabberView> grabbers = null;
  public List<TotalsCell> totalsCells = null;
  public HeaderCell firstColumnHeaderCell = null;
  public TotalsViewCell firstColumnTotalsCell = null;
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
    createMockScrollBar();

    tableView.addView(scrollView);
    scrollView.addView(rootLayout);
    tableView.addView(verticalScrollBar);
    tableView.addView(horizontalScrollView);

    if(tableView.headerContentStyle.wrap) {
      if(totalsView != null) {
        totalsView.testTextWrap();
      }
      if(firstColumnTotalsCell != null) {
        firstColumnTotalsCell.testTextWrap();
      }
      headerView.testTextWrap();
    }

    updateFirstColumnHeaderHeight();
    updateTotalsViewHeight();

    dataProvider.notifyDataSetChanged();

    tableView.post(new Runnable() {
      @Override
      public void run() {
        updateGrabbers();
      }
    });
  }

  private void setMockScrollLayouts() {
    FrameLayout.LayoutParams verticalFrameLayout = new FrameLayout.LayoutParams((int) PixelUtils.dpToPx(5), FrameLayout.LayoutParams.MATCH_PARENT);
    verticalFrameLayout.gravity = Gravity.RIGHT;
    int headerHeight = headerView.getMeasuredHeight() > 0 ? headerView.getMeasuredHeight() : tableView.headerHeight;
    verticalFrameLayout.topMargin = headerHeight + extraTopMargin;
    verticalFrameLayout.bottomMargin = TableTheme.DefaultRowHeight + extraBottomMargin;
    verticalScrollBar.setLayoutParams(verticalFrameLayout);

    FrameLayout.LayoutParams horizontalFrameLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) PixelUtils.dpToPx(5));
    horizontalFrameLayout.gravity = Gravity.BOTTOM;
    horizontalFrameLayout.bottomMargin = TableTheme.DefaultRowHeight;
    horizontalScrollView.setLayoutParams(horizontalFrameLayout);
  }

  protected void updateScrollbarBounds() {
    setMockScrollLayouts();
    this.verticalScrollBar.requestLayout();
    this.horizontalScrollView.requestLayout();
  }

  protected void updateRowHeights() {
    tableView.cellContentStyle.themedRowHeight = (tableView.cellContentStyle.getLineHeight() * tableView.cellContentStyle.lineCount) + CellView.PADDING_X_2;
    tableView.rowHeight = tableView.cellContentStyle.themedRowHeight;
    tableView.headerHeight = tableView.headerContentStyle.getLineHeight() + CellView.PADDING_X_2;
    tableView.totalsHeight = tableView.cellContentStyle.getLineHeight() + CellView.PADDING_X_2;
  }

  protected void createMockScrollBar() {
    verticalScrollBar = new MockVerticalScrollView(context, tableView);
    horizontalScrollView = new MockHorizontalScrollView(context, tableView);

    scrollView.setScrollbar(horizontalScrollView);
    coupledRecyclerView.setScrollbar(verticalScrollBar);
  }

  protected void createScrollView() {
    this.scrollView = new CustomHorizontalScrollView(context);
    LinearLayout.LayoutParams scrollLayoutParams = new LinearLayout.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
    scrollLayoutParams.bottomMargin = TableTheme.DefaultRowHeight;

    this.scrollView.setLayoutParams(scrollLayoutParams);
    this.scrollView.setFillViewport(true);

    createRootLayout();
  }

  protected void createRootLayout() {
    this.rootLayout = new RootLayout(context, columnWidths);
    this.rootLayout.setPadding(0,0, (int) PixelUtils.dpToPx(25),0);
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

    int headerHeight = tableView.headerHeight;
    int marginTop = headerHeight + extraTopMargin;

    linearLayout.recyclerView = coupledRecyclerView;
    coupledRecyclerView.setAdapter(dataProvider);

    recyclerViewLayoutParams.topMargin = marginTop;
    rootLayout.addView(coupledRecyclerView, recyclerViewLayoutParams);

    firstColumnLinearLayout.recyclerView = firstColumnRecyclerView;
    firstColumnRecyclerView.setAdapter(dataProvider);
    FrameLayout.LayoutParams firstColumnViewLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    firstColumnViewLayoutParams.topMargin = marginTop;
    firstColumnViewLayoutParams.bottomMargin = TableTheme.DefaultRowHeight;
    firstColumnRecyclerView.setLayoutParams(firstColumnViewLayoutParams);

    if(tableView.isFirstColumnFrozen) {
      firstColumnHeaderCell = HeaderViewFactory.buildFixedColumnCell(rootLayout, dataColumns.get(0), tableView, headerViewFactory.topPosition);
      dataProvider.setFirstColumnFrozen(true);
      coupledRecyclerView.setViewToScrollCouple(firstColumnRecyclerView);
      firstColumnRecyclerView.setViewToScrollCouple(coupledRecyclerView);
      firstColumnRecyclerView.setZ(1);
      if(totalsCells != null && totalsCells.size() > 0) {
        firstColumnTotalsCell = HeaderViewFactory.buildFixedTotalsCell(tableView, dataColumns.get(0), totalsCells.get(0), headerViewFactory.topPosition);
        tableView.addView(firstColumnTotalsCell);
      }
      tableView.addView(firstColumnRecyclerView);
      tableView.addView(firstColumnHeaderCell);
      tableView.addView(firstColumnDragBox);
    }

    createRowCount();
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

        grabberView.setBackgroundColor(Color.TRANSPARENT);
        grabberView.setTranslationX(startOffset);
        grabbers.add(grabberView);
        if (tableView.isFirstColumnFrozen && i == 0) {
          layoutParams.bottomMargin = TableTheme.DefaultRowHeight;
          tableView.addView(grabberView, layoutParams);
          grabberView.setElevation(PixelUtils.dpToPx(19));
        } else {
          rootLayout.addView(grabberView, layoutParams);
        }
      }
    }

    setupGrabbers();
  }

  protected void createMoreGrabbers(int n) {
      List<DataColumn> dataColumns = dataProvider.getDataColumns();
      int dragWidth = (int) PixelUtils.dpToPx(40);
      int startOffset = (int) grabbers.get(grabbers.size() - 1).getTranslationX();

      for (int i = 0; i < n; i++) {
        DataColumn column = dataColumns.get(grabbers.size() + i);
        GrabberView grabberView = new GrabberView(column.columnIndex, context, scrollView, tableView);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dragWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        startOffset += column.width;

        grabberView.setLayoutParams(layoutParams);
        grabberView.setBackgroundColor(Color.TRANSPARENT);
        grabberView.setTranslationX(startOffset);
        grabbers.add(grabberView);
        grabberView.setGrabbers(grabbers);
        rootLayout.addView(grabberView);
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

  void invalidateLayout() {
    if (this.coupledRecyclerView != null) {
      int width = dataColumns.get(0).width;
      int marginTop = tableView.headerHeight + extraTopMargin;

      FrameLayout.LayoutParams recyclerViewLayoutParams = (FrameLayout.LayoutParams) coupledRecyclerView.getLayoutParams();
      recyclerViewLayoutParams.topMargin = marginTop;
      coupledRecyclerView.setLayoutParams(recyclerViewLayoutParams);

      FrameLayout.LayoutParams firstColumnRecyclerViewLayoutParams = (FrameLayout.LayoutParams) firstColumnRecyclerView.getLayoutParams();
      firstColumnRecyclerViewLayoutParams.topMargin = marginTop;
      firstColumnRecyclerView.setLayoutParams(firstColumnRecyclerViewLayoutParams);

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

      updateScrollbarBounds();

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
          extraBottomMargin = tableView.totalsHeight;
          break;
        case "noTotals":
        case "top":
        default:
          topPosition = true;
          extraTopMargin = tableView.totalsHeight;
          break;
      }

    }
    this.headerViewFactory = new HeaderViewFactory(dataColumns, totalsCells, dataProvider.totalsLabel, topPosition, tableView, tableView.headerContentStyle, context);
  }

  public void updateGrabbers() {
    if (grabbers != null) {
      int maxLineHeight = headerView.getMaxLineCount();
      int headerHeight = maxLineHeight * tableView.headerContentStyle.lineHeight + CellView.PADDING_X_2;
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

  public void updateHeaderViewLineCount() {
    int maxLineCount = headerView.getMaxLineCount();
    int headerHeight = (maxLineCount * tableView.headerContentStyle.lineHeight) + (CellView.PADDING_X_2);
    tableView.headerHeight = headerHeight;

    updateTotalsViewHeight();
    updateFirstColumnsHeights();

    ViewGroup.LayoutParams params = headerView.getLayoutParams();
    params.height = headerHeight;
    headerView.setLayoutParams(params);
    FrameLayout.LayoutParams recyclerParams = (FrameLayout.LayoutParams) coupledRecyclerView.getLayoutParams();
    recyclerParams.topMargin = headerHeight;
    if(headerViewFactory.topPosition && totalsView != null) {
      recyclerParams.topMargin += tableView.totalsHeight;
    }
    coupledRecyclerView.setLayoutParams(recyclerParams);

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
        dd.topMargin = tableView.headerHeight ;
        if(headerViewFactory.topPosition && totalsView != null) {
          dd.topMargin += tableView.totalsHeight;
        }
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
      if(tableView.headerContentStyle.wrap) {
        firstColumnHeaderCell.setMaxLines(tableView.headerHeight / tableView.headerContentStyle.lineHeight);
      }
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
      int maxLineCount = totalsView.getMaxLineCount();
      int totalsViewHeight = (maxLineCount * tableView.cellContentStyle.lineHeight) + (CellView.PADDING_X_2);
      tableView.totalsHeight = totalsViewHeight;

      FrameLayout.LayoutParams pp = (FrameLayout.LayoutParams)totalsView.getLayoutParams();
      if(headerViewFactory.topPosition) {
        pp.topMargin = tableView.headerHeight;
      }
      pp.height = totalsViewHeight;
      totalsView.setLayoutParams(pp);

      if(firstColumnTotalsCell != null) {
        FrameLayout.LayoutParams fp = (FrameLayout.LayoutParams) firstColumnTotalsCell.getLayoutParams();
        fp.height = totalsViewHeight;
        if(headerViewFactory.topPosition) {
          fp.topMargin = tableView.headerHeight;
        } else {
          fp.bottomMargin = TableTheme.DefaultRowHeight;
        }

        firstColumnTotalsCell.setLayoutParams(fp);
        if(tableView.headerContentStyle.wrap) {
          firstColumnTotalsCell.setMaxLines(totalsViewHeight / tableView.cellContentStyle.lineHeight);
        }
      }

      tableView.post(new Runnable() {
        @Override
        public void run() {
          totalsView.requestLayout();
          if(firstColumnTotalsCell != null) {
            firstColumnTotalsCell.requestLayout();
          }
        }
      });
    }
  }
}
