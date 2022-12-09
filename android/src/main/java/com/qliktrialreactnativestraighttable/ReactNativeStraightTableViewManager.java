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
      return new TableView(reactContext);
    }

    private void processRows(TableView tableView, ReadableMap rows) {
      ReadableArray dataRows = rows.getArray("rows");
      if(dataRows != null) {
        boolean resetData = rows.getBoolean("reset");
        RowFactory factory = new RowFactory(dataRows, tableView.getColumns(), tableView.imageLoader);
        List<DataRow> transformedRows = factory.getRows();
        tableView.setRows(transformedRows, resetData);
      }
    }

    private List<DataColumn> processColumns(TableView tableView, ReadableMap cols) {
      String totalsLabel = null, totalsPosition = null;
      ReadableArray totalsRows = null;

      ReadableArray columns = cols.getArray("header");
      ReadableMap totals = cols.getMap("totals");

      if(totals != null) {
        totalsPosition = totals.getString("position");
        totalsLabel = totals.getString("label");
        totalsRows = totals.getArray("rows");

        tableView.setTotals(totalsRows, totalsPosition, totalsLabel);
      }

      List<DataColumn> dataColumns = new ArrayList<>();
      if(columns != null) {
        for (int i = 0; i < columns.size(); i++) {
          DataColumn column = new DataColumn(columns.getMap(i), i);
          dataColumns.add(column);
        }
      }

      return dataColumns;
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
    public void setFreezeFirstColumn(View view, Boolean isFreezeFirstColumn) {
      TableView tableView = (TableView) (view);
      tableView.setFirstColumnFrozen(isFreezeFirstColumn);
    }

    @ReactProp(name = "cols")
    public void setCols(View view,  @Nullable ReadableMap source) {
      if(source == null) {
        return;
      }
      TableView tableView = (TableView) (view);
      List<DataColumn> dataColumns = processColumns(tableView, source);
      tableView.setDataColumns(dataColumns);
    }

    @ReactProp(name = "isDataView")
    public void setDataView(View view, boolean isDataView) {
      TableView tableView = (TableView) (view);
      tableView.setDataView(isDataView);
    }

    @ReactProp(name = "rows")
    public void setRows(View view, @Nullable ReadableMap source) {
      if(source == null) {
        return;
      }

      TableView tableView = (TableView) (view);
      processRows(tableView, source);
    }

    @ReactProp(name = "size")
    public void setSize(View view, @Nullable ReadableMap source) {
      if(source != null) {
        TableView tableView = (TableView) view;
        DataSize dataSize = new DataSize(source);
        tableView.setDataSize(dataSize);
      }
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
