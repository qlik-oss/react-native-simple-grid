package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

public class ReactNativeStraightTableViewManager extends SimpleViewManager<View> {
    public static final String REACT_CLASS = "ReactNativeStraightTableView";

    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    TableView getTableViewFrom(View view) {
      HorizontalScrollView horizontalScrollView = (HorizontalScrollView) view;
      TableView tableView = (TableView) horizontalScrollView.getChildAt(0);
      return tableView;
    }

  CustomHorizontalScrollView getHorizontaScrllView(View view) {
    CustomHorizontalScrollView horizontalScrollView = (CustomHorizontalScrollView) view;
    return horizontalScrollView;
  }

    @SuppressLint("NewApi")
    @Override
    @NonNull
    public View createViewInstance(ThemedReactContext reactContext) {

      CustomHorizontalScrollView scrollView = new CustomHorizontalScrollView(reactContext);
      TableView tableView =  new TableView(reactContext, scrollView);
      scrollView.setFillViewport(true);
      scrollView.addView(tableView);

      return scrollView;
    }

    @ReactProp(name = "theme")
    public void setTheme(View view, ReadableMap theme) {
      TableTheme.from(theme);
      TableView tableView = getTableViewFrom(view);
      tableView.updateTheme();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ReactProp(name = "cols")
    public void setCols(View view,  @Nullable ReadableMap source) {
      ReadableArray columns = source.getArray("header");
      ReadableArray footer = source.getArray("footer");

      TableView tableView = getTableViewFrom(view);
      HeaderView headerView = tableView.getHeaderView();
      HeaderViewFactory headerViewFactory;
      if (headerView != null && columns != null) {
        headerViewFactory = new HeaderViewFactory(columns, getHorizontaScrllView(view));
        headerView.update(headerViewFactory.getDataColumns());
      } else {
        headerViewFactory = new HeaderViewFactory(columns, footer, tableView.getContext(), getHorizontaScrllView(view));
        headerView = headerViewFactory.getHeaderView();
        tableView.setHeaderView(headerView);
      }

      tableView.setDataColumns(headerViewFactory.getDataColumns());
    }

    @ReactProp(name = "isDataView")
    public void setDataView(View view, boolean isDataView) {
      TableView tableView = getTableViewFrom(view);
      tableView.setDataView(isDataView);
    }

    @ReactProp(name = "rows")
    public void setRows(View view, @Nullable ReadableMap rows) {
      TableView tableView = getTableViewFrom(view);
      ReadableArray dataRows = rows.getArray("rows");
      boolean resetData = rows.getBoolean("reset");

      RowFactory factory = new RowFactory(dataRows);
      tableView.setRows(factory.getRows(), resetData);
    }

    @ReactProp(name = "size")
    public void setSize(View view, @Nullable ReadableMap source) {
      DataSize dataSize = new DataSize(source);
      TableView tableView = getTableViewFrom(view);
      tableView.setDataSize(dataSize);
    }

    @ReactProp(name = "containerWidth")
    public void setContainerWidth(View view, int width) {
      TableView tableView = getTableViewFrom(view);
      tableView.createScreenGuide((int)PixelUtils.dpToPx(width));
    }

    @ReactProp(name = "clearSelections")
    public void setClearSelections(View view, String value) {
      if (value.equalsIgnoreCase("yes")) {
        TableView tableView = getTableViewFrom(view);
        tableView.clearSelections();
      }
    }

  @Nullable
  @Override
  public Map getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.of(
      "onEndReached",
      MapBuilder.of("registrationName", "onEndReached"),
      "onSelectionsChanged",
      MapBuilder.of("registrationName", "onSelectionsChanged"),
      "onColumnsResized",
      MapBuilder.of("registrationName", "onColumnsResized"),
      "onHeaderPressed",
      MapBuilder.of("registrationName", "onHeaderPressed")
    );
  }
}
