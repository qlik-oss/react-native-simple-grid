package com.qliktrialreactnativestraighttable;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.text.ReactFontManager;

import java.util.ArrayList;
import java.util.List;

public class TableView extends FrameLayout {

  public final static int SCROLL_THUMB_HEIGHT = 12;
  RootLayout rootLayout;
  CustomHorizontalScrollView  scrollView;
  final DragBox dragBox;
  DragBox firstColumnDragBox = null;
  HeaderView headerView = null;
  AutoLinearLayout footerView = null;
  CustomRecyclerView recyclerView = null;
  CustomRecyclerView firstColumnView = null;
  HeaderCell firstColumnHeaderCell = null;
  ScreenGuideView screenGuideView = null;
  SelectionsEngine selectionsEngine = new SelectionsEngine();
  ReadableMap translations;
  final ColumnWidths columnWidths ;
  DataProvider dataProvider;
  boolean isFirstColumnFrozen = false;
  String name = "";


  DragBoxEventHandler dragBoxEventHandler = new DragBoxEventHandler(this);
  HeaderContentStyle headerContentStyle;
  CellContentStyle cellContentStyle;
  List<GrabberView> grabbers = null;
  int rowHeight = TableTheme.rowHeightFactor;
  int themedRowHeight = TableTheme.rowHeightFactor;
  int headerHeight = TableTheme.rowHeightFactor;
  final TableViewFactory tableViewFactory;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  TableView(ThemedReactContext context) {
    super(context);
    TableTheme.iconFonts = ReactFontManager.getInstance().getTypeface("fontello", 0, context.getAssets());
    columnWidths = new ColumnWidths(this.getContext());
    dataProvider = new DataProvider(columnWidths, selectionsEngine, this);
    dragBox = new DragBox(context, this, dragBoxEventHandler, false);
    firstColumnDragBox = new DragBox(context, this, dragBoxEventHandler, true);
    dragBoxEventHandler.setDragBoxes(dragBox, firstColumnDragBox);
    tableViewFactory = new TableViewFactory(this, columnWidths, dataProvider, dragBox, firstColumnDragBox);
  }

  public void clearSelections() {
    hideDragBoxes();
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

  public void setTranslations(ReadableMap translations) {
    this.translations = translations;
  }

  public String getTranslation(String mapKey, String stringKey) {
    String defaultString = mapKey + "." + stringKey;
    ReadableMap map = translations.getMap(mapKey);
    if(map == null) {
      return defaultString;
    }
    return JsonUtils.getString(map, stringKey, defaultString);
  }

  public void setName(String value) {
    this.name = value;
    columnWidths.setName(value);
  }

  public void setHeaderStyle(HeaderContentStyle headerContentStyle) {
    this.headerContentStyle = headerContentStyle;
  }

  public void setCellContentStyle(CellContentStyle cellContentStyle) {
    this.cellContentStyle = cellContentStyle;
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

    if(grabbers != null) {
      for(GrabberView grabberView : grabbers) {
        grabberView.setDataProvider(dataProvider);
      }
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

  void updateHeaderViewLineCount() {
    tableViewFactory.updateHeaderViewLineCount();
  }

  void updateRecyclerViewLineCount(DataColumn column) {
    tableViewFactory.updateRecyclerViewLineCount(column);
  }

  void onEndPan() {
    // avoid drift
    tableViewFactory.updateGrabbers();
  }
}
