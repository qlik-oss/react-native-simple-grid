package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class TableViewFactory {
  public CustomHorizontalScrollView scrollView;
  public RootLayout rootLayout;
  public HeaderView headerView;
  public CustomRecyclerView firstColumnRecyclerView;
  public CustomRecyclerView coupledRecyclerView;
  public List<GrabberView> grabbers = null;
  public HeaderCell firstColumnHeaderCell;
  public ScreenGuideView screenGuideView;
  private List<DataColumn> dataColumns;
  private final Context context;
  private final ColumnWidths columnWidths;
  private final DataProvider dataProvider;
  private final TableView tableView;

  public TableViewFactory(TableView tableView, ColumnWidths columnWidths, DataProvider dataProvider) {
    this.tableView = tableView;
    this.columnWidths = columnWidths;
    this.dataProvider = dataProvider;
    this.context = tableView.getContext();
  }

  public void createAll() {
    this.dataColumns = dataProvider.dataColumns;
    createScrollView();
    tableView.addView(scrollView);
    scrollView.addView(rootLayout);
  }

  protected void createScrollView() {
    this.scrollView = new CustomHorizontalScrollView(context);
    this.scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,  ScrollView.LayoutParams.MATCH_PARENT));
    this.scrollView.setFillViewport(true);

    createRootLayout();
  }

  protected void createRootLayout() {
    FrameLayout.LayoutParams frameLayout = new FrameLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT);
    this.rootLayout = new RootLayout(context, columnWidths);
    this.rootLayout.setPadding(0, 0, (int)PixelUtils.dpToPx(25), 0);
    this.rootLayout.setLayoutParams(frameLayout);

    createHeaderView();
  }

  protected void createHeaderView() {
      HeaderViewFactory headerViewFactory = new HeaderViewFactory(dataColumns,  tableView, context );
      headerView = headerViewFactory.getHeaderView();
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, TableTheme.headerHeight);
      params.gravity = Gravity.TOP;
      rootLayout.addView(headerView);

      createRecyclerViews();
  }

  protected void createRecyclerViews() {
    LinearLayoutManager linearLayout = new LinearLayoutManager(context);
    coupledRecyclerView = new CustomRecyclerView(context, false, dataProvider, tableView, linearLayout);
    FrameLayout.LayoutParams recyclerViewLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    recyclerViewLayoutParams.topMargin = TableTheme.headerHeight;
    rootLayout.addView(coupledRecyclerView, recyclerViewLayoutParams);

    LinearLayoutManager firstColumnLinearLayout = new LinearLayoutManager(context);
    firstColumnRecyclerView = new CustomRecyclerView(context, true, dataProvider, tableView, firstColumnLinearLayout);
    FrameLayout.LayoutParams firstColumnViewLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    firstColumnViewLayoutParams.topMargin = TableTheme.headerHeight;
    if(tableView.isFirstColumnFrozen) {
      firstColumnHeaderCell = HeaderViewFactory.buildFixedColumnCell(rootLayout, dataColumns.get(0), tableView);
      dataProvider.setFirstColumnFrozen(true);
      coupledRecyclerView.setViewToScrollCouple(firstColumnRecyclerView);
      firstColumnRecyclerView.setViewToScrollCouple(coupledRecyclerView);
      firstColumnRecyclerView.setElevation(PixelUtils.dpToPx(3));
      firstColumnHeaderCell.setElevation(PixelUtils.dpToPx(4));
      tableView.addView(firstColumnRecyclerView, firstColumnViewLayoutParams);
      tableView.addView(firstColumnHeaderCell);
    }

    createGrabbers();
  }

  protected void createGrabbers() {
    if(grabbers == null) {
      grabbers = new ArrayList<>();
      List<DataColumn> dataColumns = dataProvider.getDataColumns();
      int dragWidth = (int) PixelUtils.dpToPx(40);
      int offset = dragWidth / 2;
      int startOffset = 0;

      for (int i =0; i < dataColumns.size(); i++) {
        GrabberView grabberView = new GrabberView(i, context, scrollView);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dragWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        startOffset += (dataColumns.get(i).width) - offset;
        offset = 0;

        grabberView.setLayoutParams(layoutParams);
        grabberView.setBackgroundColor(Color.TRANSPARENT);
        grabberView.setTranslationX(startOffset);
        grabbers.add(grabberView);
        if(tableView.isFirstColumnFrozen && i == 0) {
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
    for(GrabberView view : grabbers) {
      view.setDataProvider(dataProvider);
      view.setHeaderView(headerView);
      view.setGrabbers(grabbers);
      view.setRecyclerView(coupledRecyclerView);
      view.rootLayout = this.rootLayout;
      view.setFirstColumnRecyclerView(firstColumnRecyclerView);
      view.setFirstColumnHeader(firstColumnHeaderCell);
      view.updateLayout();
    }

    createScreenGuide(tableView.getWidth());
  }

  protected void createScreenGuide(int width) {
    if (screenGuideView == null && grabbers != null) {
      screenGuideView = new ScreenGuideView(context);
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
      params.gravity = Gravity.CENTER_VERTICAL;
      rootLayout.addView(screenGuideView, params);
      for (GrabberView grabberView : grabbers) {
        grabberView.setGreenGuideView(screenGuideView);
      }
    }
  }

  void invalidateLayout() {
    if(this.coupledRecyclerView != null) {

      if(this.firstColumnHeaderCell != null) {
        int width = dataColumns.get(0).width;
        ViewGroup.LayoutParams params = firstColumnHeaderCell.getLayoutParams();
        params.width = width;
        firstColumnHeaderCell.setLayoutParams(params);
        firstColumnHeaderCell.requestLayout();
      }

      this.headerView.updateLayout();
      this.headerView.requestLayout();

      this.dataProvider.invalidateLayout();
      if(this.firstColumnRecyclerView != null) {
        this.firstColumnRecyclerView.requestLayout();
      }
      this.coupledRecyclerView.requestLayout();

      updateGrabbers();
      
      this.rootLayout.requestLayout();
      this.scrollView.requestLayout();
    }
  }

  private void updateGrabbers() {
    if (grabbers != null) {
      List<DataColumn> dataColumns = dataProvider.getDataColumns();
      int dragWidth = (int) PixelUtils.dpToPx(40);
      int offset = dragWidth / 2;
      int startOffset = 0;

      for (int i = 0; i < dataColumns.size(); i++) {
        GrabberView grabberView = grabbers.get(i);
        startOffset += (dataColumns.get(i).width) - offset;
        offset = 0;
        grabberView.setTranslationX(startOffset);
      }
    }
  }
}
