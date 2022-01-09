package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @SuppressLint("NewApi")
    @Override
    @NonNull
    public View createViewInstance(ThemedReactContext reactContext) {
      CustomHorizontalScrollView scrollView = new CustomHorizontalScrollView(reactContext);
      TableView tableView =  new TableView(reactContext, scrollView);
      scrollView.addView(tableView);
      return scrollView;
    }

    @ReactProp(name = "theme")
    public void setTheme(View view, ReadableMap theme) {
      TableTheme.from(theme);
      TableView tableView = getTableViewFrom(view);
      tableView.updateTheme();
    }

    @ReactProp(name = "cols")
    public void setCols(View view,  @Nullable ReadableArray columns) {
      HeaderViewFactory headerViewFactory = new HeaderViewFactory(columns, view.getContext());
      TableView tableView = getTableViewFrom(view);
      LinearLayout headerView = headerViewFactory.getHeaderView();
      tableView.setHeaderView(headerView);
      tableView.setDataColumns(headerViewFactory.getDataColumns());
    }

    @ReactProp(name = "rows")
    public void setRows(View view, @Nullable ReadableArray rows) {
      RowFactory factory = new RowFactory(rows);
      TableView tableView = getTableViewFrom(view);
      tableView.setRows(factory.getRows());
    }

    @ReactProp(name = "size")
    public void setSize(View view, @Nullable ReadableMap source) {
      DataSize dataSize = new DataSize(source);
      TableView tableView = getTableViewFrom(view);
      tableView.setDataSize(dataSize);
    }

  @Nullable
  @Override
  public Map getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.builder().put(
      "onEndReached",
      MapBuilder.of(
        "registrationName", "onEndReached"
      )
    ).build();
  }
}
