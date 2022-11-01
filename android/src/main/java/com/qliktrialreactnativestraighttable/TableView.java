package com.qliktrialreactnativestraighttable;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class TableView extends FrameLayout {

  public final static int SCROLL_THUMB_HEIGHT = 12;
  RootLayout rootLayout;
  CustomHorizontalScrollView  scrollView;
  final DragBox dragBox;
  final DragBox firstColumnDragBox;
  HeaderView headerView = null;
  AutoLinearLayout footerView = null;
  CustomRecyclerView recyclerView = null;
  CustomRecyclerView firstColumnView = null;
  HeaderCell firstColumnHeaderCell = null;
  ScreenGuideView screenGuideView = null;
  SelectionsEngine selectionsEngine = new SelectionsEngine();
  final  ColumnWidths columnWidths ;
  DataProvider dataProvider;
  boolean isFirstColumnFrozen = false;
  String name = "";

  DragBoxEventHandler dragBoxEventHandler = new DragBoxEventHandler();
  TableTheme tableTheme = new TableTheme();
  List<GrabberView> grabbers = null;
  final TableViewFactory tableViewFactory;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  TableView(Context context) {
    super(context);
    columnWidths = new ColumnWidths(this.getContext());
    dataProvider = new DataProvider(columnWidths, selectionsEngine, this);
    dragBox = new DragBox(context, this, dragBoxEventHandler, false);
    firstColumnDragBox = new DragBox(context, this, dragBoxEventHandler, true);
    dragBoxEventHandler.setDragBoxes(dragBox, firstColumnDragBox);
    tableViewFactory = new TableViewFactory(this, columnWidths, dataProvider, dragBox, firstColumnDragBox);
  }

  public void clearSelections() {
    selectionsEngine.clearSelections();
  }

  public void showDragBox(Rect bounds, int columnId) {
    if(isFirstColumnFrozen && columnId == 0) {
      firstColumnDragBox.show(bounds, columnId);
      dragBox.hide();
      return;
    }
    dragBox.show(bounds, columnId);
    firstColumnDragBox.hide();
  }

  public void hideDragBoxes() {
    firstColumnDragBox.hide();
    dragBox.hide();
  }

  public void setFirstColumnFrozen(boolean shouldFreeze) {
    isFirstColumnFrozen = shouldFreeze;
    dataProvider.setFirstColumnFrozen(shouldFreeze);
  }

  public void setName(String value) {
    this.name = value;
    columnWidths.setName(value);
  }

  public void updateTheme() {
    // no up for now
  }

  public void setDataColumns(List<DataColumn> cols) {
    dataProvider.setDataColumns(cols);
    columnWidths.updateWidths(cols);
    dataProvider.updateRepresentation();
    if(headerView != null) {
      headerView.update(cols);
    }

    if(firstColumnHeaderCell != null && cols.size() > 0) {
      firstColumnHeaderCell.setColumn(cols.get(0));
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
    if (recyclerView != null) {
      recyclerView.requestLayout();
      requestLayout();
    }

    if (resetData) {
      selectionsEngine.clearSelections();
    }
  }

  public void setDataSize(DataSize dataSize) {
    dataProvider.setDataSize(dataSize);
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
    Log.d("FOO", "Size changed!!");
    columnWidths.loadWidths(w, dataProvider.dataColumns, dataProvider.rows);
    if(recyclerView == null) {
      createRecyclerView();
      post(new Runnable() {
        @Override
        public void run() {
          scrollView.requestLayout();
          requestLayout();
        }
      });

    } else {
      post(new Runnable() {
        @Override
        public void run() {
          invalidateLayout();
        }
      });
    }
  }

  void createRecyclerView() {
    if (recyclerView == null) {
      tableViewFactory.createAll();
      recyclerView = tableViewFactory.coupledRecyclerView;
      grabbers = tableViewFactory.grabbers;
      headerView = tableViewFactory.headerView;
      rootLayout = tableViewFactory.rootLayout;
      scrollView = tableViewFactory.scrollView;
      screenGuideView = tableViewFactory.screenGuideView;
      firstColumnView = tableViewFactory.firstColumnRecyclerView;
      firstColumnHeaderCell = tableViewFactory.firstColumnHeaderCell;
    }
  }

  void invalidateLayout() {
    tableViewFactory.invalidateLayout();
    requestLayout();
  }
}
