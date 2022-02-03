package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TableView extends FrameLayout {
  // TODO: make updatedaldldld
  RootLayout rootView;
  CustomHorizontalScrollView scrollView;
  AutoLinearLayout headerView = null;
  AutoLinearLayout footerView = null;
  CustomRecyclerView recyclerView = null;
  ScreenGuideView screenGuideView = null;
  SelectionsEngine selectionsEngine = new SelectionsEngine();
  DataProvider dataProvider = new DataProvider();

  TableTheme tableTheme = new TableTheme();
  List<GrabberView> grabbers = null;
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  TableView(Context context, CustomHorizontalScrollView scrollView) {
    super(context);
    this.scrollView = scrollView;
    EventUtils.contextView = scrollView;
    this.rootView = new RootLayout(context);
    dataProvider.selectionsEngine = selectionsEngine;
    decorate();
    this.addView(rootView);
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

  public void setHeaderView(AutoLinearLayout view) {
    if (this.headerView == null) {
      this.headerView = view;
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableTheme.headerHeight);
      params.gravity = Gravity.TOP;
      rootView.addView(this.headerView, params);
    }
  }

  public void setFooterView(AutoLinearLayout view) {
    if (this.footerView == null) {
      this.footerView = view;
      if (this.footerView != null) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableTheme.headerHeight);
        params.gravity = Gravity.BOTTOM;
        rootView.addView(this.footerView, params);
      }
    }
  }

  public void updateTheme() {
    if (headerView != null) {
      headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    }
  }

  public void setDataColumns(List<DataColumn> cols) {
    if (dataProvider.getDataColumns() == null) {
      dataProvider.setDataColumns(cols);
      if (dataProvider.ready()) {
        createRecyclerView();
      }
    }
  }

  public void setRows(List<DataRow> rows, boolean resetData) {
    dataProvider.setRows(rows, resetData);
    if (this.headerView != null && dataProvider.ready()) {
      createRecyclerView();
    }
    if (resetData) {
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
      recyclerView = new CustomRecyclerView(this.getContext());
      recyclerView.setLayoutManager(linearLayout);
      recyclerView.setAdapter(dataProvider);
      FrameLayout.LayoutParams recyclerViewLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      recyclerViewLayoutParams.topMargin = (TableTheme.headerHeight);
      if (footerView != null) {
        recyclerViewLayoutParams.bottomMargin = TableTheme.headerHeight;
      }
      recyclerViewLayoutParams.gravity = Gravity.TOP;

      DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
      recyclerView.addItemDecoration(itemDecorator);
      recyclerView.setHasFixedSize(true);
      recyclerView.addOnScrollListener( new OnScrollListener(linearLayout) );
      rootView.addView(recyclerView, recyclerViewLayoutParams);


      setupGrabbers();
    }
  }

  private void createDataColumnWidths() {
    ColumnWidthFactory columnWidthFactory = new ColumnWidthFactory(dataProvider.dataColumns,
      dataProvider.rows,
      this.getContext(), this.headerView);

    columnWidthFactory.autoSize();
  }

  private void setupGrabbers() {
    for(GrabberView view : grabbers) {
      view.setDataProvider(dataProvider);
      view.setHeaderView(headerView);
      view.setGrabbers(grabbers);
      view.setRecyclerView(recyclerView);
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
      GrabberView grabberView = new GrabberView(i, getContext(), scrollView);
      LayoutParams layoutParams = new LayoutParams(dragWidth, ViewGroup.LayoutParams.MATCH_PARENT);
      startOffset += (dataColumns.get(i).width) - offset;
      offset = 0;

      grabberView.setLayoutParams(layoutParams);
      grabberView.setBackgroundColor(Color.TRANSPARENT);
      grabberView.setTranslationX(startOffset);
      grabbers.add(grabberView);
      this.addView(grabberView);
    }
  }

  public void createScreenGuide(int width) {
    if (screenGuideView == null) {
      screenGuideView = new ScreenGuideView(this.getContext());
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
      params.gravity = Gravity.CENTER_VERTICAL;
      this.addView(screenGuideView, params);
      for (GrabberView grabberView : grabbers) {
        grabberView.setGreenGuideView(screenGuideView);
      }
    }
  }

  class OnScrollListener extends RecyclerView.OnScrollListener {
    LinearLayoutManager linearLayoutManager;
    public OnScrollListener(LinearLayoutManager layoutManager) {
      linearLayoutManager = layoutManager;
    }
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
      if(linearLayoutManager.findLastCompletelyVisibleItemPosition() >= dataProvider.getItemCount() - 50
        && !dataProvider.isLoading()
        && dataProvider.needsMore()) {
        // start the fetch
        dataProvider.setLoading(true);
        EventUtils.sendEventToJSFromView("onEndReached");
      }
    }
  }
}
