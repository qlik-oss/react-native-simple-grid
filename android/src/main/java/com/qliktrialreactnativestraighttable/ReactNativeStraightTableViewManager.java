package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ReactNativeStraightTableViewManager extends SimpleViewManager<View> {
    ReadableMap cols;
    ReadableMap rows;
    Boolean isFreezeFirstColumn;
    DataSize dataSize;
    boolean alreadyInitialized = false;

  public static final String REACT_CLASS = "ReactNativeStraightTableView";
    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @SuppressLint("NewApi")
    @Override
    @NonNull
    public View createViewInstance(ThemedReactContext reactContext) {
      TableView table = new TableView(reactContext);
      return table;
    }

    private void resetProps() {
      cols = null;
      rows = null;
      isFreezeFirstColumn = null;
      alreadyInitialized = false;
    }

    public void initializeWhenReady(View view) {
      if(cols == null || rows == null || isFreezeFirstColumn == null) {
        return;
      }
      TableView tableView = (TableView) (view);
      String totalsLabel = null, totalsPosition = null;
      ReadableArray totalsRows = null;
      tableView.setFirstColumnFrozen(isFreezeFirstColumn);

      ReadableArray columns = cols.getArray("header");
      ReadableArray footer = cols.getArray("footer");
      ReadableMap totals = cols.getMap("totals");

      if(totals != null) {
        totalsPosition = totals.getString("position");
        totalsLabel = totals.getString("label");
        totalsRows = totals.getArray("rows");
        tableView.setTotals(totalsRows, totalsPosition, totalsLabel);
      }

      List<DataColumn> dataColumns = new ArrayList<>();
      for(int i = 0; i < columns.size(); i++) {
        DataColumn column = new DataColumn(columns.getMap(i), i);
        dataColumns.add(column);
      }

      ReadableArray dataRows = rows.getArray("rows");
      boolean resetData = rows.getBoolean("reset");
      RowFactory factory = new RowFactory(dataRows, dataColumns);

      tableView.setRows(factory.getRows(), resetData);
      tableView.setDataColumns(dataColumns);
      alreadyInitialized = true;
    }

    @ReactProp(name = "theme")
    public void setTheme(View view, ReadableMap theme) {

    }

    @ReactProp(name = "translations")
    public void setTranslations(View view, @Nullable ReadableMap translations) {
      if(translations == null) {
        return;
      }
      TableView tableView = (TableView) (view);
      tableView.setTranslations(translations);
    }

    @ReactProp(name = "freezeFirstColumn")
    public void setFreezeFirstColumn(View view, Boolean value) {
      if(alreadyInitialized) {
        resetProps();
      }
      isFreezeFirstColumn = value;
      initializeWhenReady(view);
    }

    @ReactProp(name = "cols")
    public void setCols(View view,  @Nullable ReadableMap source) {
      if(alreadyInitialized) {
        resetProps();
      }
      cols = source;
      initializeWhenReady(view);
    }

    @ReactProp(name = "isDataView")
    public void setDataView(View view, boolean isDataView) {
      TableView tableView = (TableView) (view);
      tableView.setDataView(isDataView);

      initializeWhenReady(view);
    }

    @ReactProp(name = "rows")
    public void setRows(View view, @Nullable ReadableMap source) {
      TableView tableView = (TableView) (view);
      rows = source;
      boolean resetData = rows.getBoolean("reset");

      if(resetData) {
        if(alreadyInitialized) {
          resetProps();
        }
        initializeWhenReady(view);
        return;
      }

      ReadableArray dataRows = rows.getArray("rows");
      RowFactory factory = new RowFactory(dataRows, tableView.getColumns());

      tableView.setRows(factory.getRows(), resetData);
    }

    @ReactProp(name = "size")
    public void setSize(View view, @Nullable ReadableMap source) {
      TableView tableView = (TableView) view;
      dataSize = new DataSize(source);
      tableView.setDataSize(dataSize);
    }

    @ReactProp(name = "containerWidth")
    public void setContainerWidth(View view, int width) {
      // no op
    }

    @ReactProp(name = "clearSelections")
    public void setClearSelections(View view, String value) {
      if (value.equalsIgnoreCase("yes")) {
        TableView tableView = (TableView) (view);
        tableView.clearSelections();
      }
    }

    @ReactProp(name = "name")
    public void setName(View view, String value) {
      TableView tableView =(TableView) view;
      tableView.setName(value);
    }

    @ReactProp(name = "headerContentStyle")
    public void setHeaderContentStyle(View view, ReadableMap source) {
      HeaderContentStyle headerContentStyle = new HeaderContentStyle(source);
      TableView tableView = (TableView) view;
      tableView.setHeaderStyle(headerContentStyle);
    }

    @ReactProp(name = "cellContentStyle")
    public void setCellContentStyle(View view, ReadableMap source) {
      CellContentStyle cellContentStyle = new CellContentStyle(source);
      TableView tableView = (TableView) view;
      tableView.setCellContentStyle(cellContentStyle);
    }

  @Nullable
  @Override
  public Map getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.<String, Object>builder()
      .put("onEndReached",
        MapBuilder.of("registrationName", "onEndReached"))
      .put("onSelectionsChanged",
        MapBuilder.of("registrationName", "onSelectionsChanged"))
      .put("onHeaderPressed",
        MapBuilder.of("registrationName", "onHeaderPressed"))
      .put("onExpandCell",
        MapBuilder.of("registrationName", "onExpandCell"))
      .put("onSearchColumn",
        MapBuilder.of("registrationName", "onSearchColumn"))
      .put("onDragBox",
        MapBuilder.of("registrationName", "onDragBox"))
      .build();
  }
}
