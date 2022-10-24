package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ReactNativeStraightTableViewManager extends SimpleViewManager<View> {
    public static final String REACT_CLASS = "ReactNativeStraightTableView";
    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    TableView getTableViewFrom(View view) {
      HorizontalScrollView horizontalScrollView = (HorizontalScrollView) ((FrameLayout) view).getChildAt(0);
      TableView tableView = (TableView) horizontalScrollView.getChildAt(0);
      return tableView;
    }

    CustomHorizontalScrollView getHorizontalScrollView(View view) {
      CustomHorizontalScrollView horizontalScrollView = (CustomHorizontalScrollView) ((FrameLayout) view).getChildAt(0);
      return horizontalScrollView;
    }

    @SuppressLint("NewApi")
    @Override
    @NonNull
    public View createViewInstance(ThemedReactContext reactContext) {
      FrameLayout rootView = new FrameLayout(reactContext);
      FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
      rootView.setLayoutParams(layoutParams);

      CustomHorizontalScrollView scrollView = new CustomHorizontalScrollView(reactContext);
      TableView tableView =  new TableView(reactContext, scrollView, rootView);
      RelativeLayout.LayoutParams scrollLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
      scrollLayoutParams.bottomMargin = TableView.SCROLL_THUMB_HEIGHT;
      scrollView.setFillViewport(true);
      tableView.setLayoutParams(scrollLayoutParams);

      scrollView.addView(tableView, scrollLayoutParams);
      rootView.addView(scrollView);

      return rootView;
    }

    @ReactProp(name = "theme")
    public void setTheme(View view, ReadableMap theme) {
      TableTheme.from(theme);
      TableView tableView = getTableViewFrom(view);
      tableView.updateTheme();
    }

    @ReactProp(name = "freezeFirstColumn")
    public void setFreezeFirstColumn(View view, Boolean value) {
        TableView tableView = getTableViewFrom(view);
        tableView.setFirstColumnFrozen(value);
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
        headerViewFactory = new HeaderViewFactory(columns, getHorizontalScrollView(view), (FrameLayout) view, tableView);
        headerView.update(headerViewFactory.getDataColumns());
      } else {
        headerViewFactory = new HeaderViewFactory(columns, footer, getHorizontalScrollView(view),  (FrameLayout) view, tableView);
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

      RowFactory factory = new RowFactory(dataRows, tableView.getColumns());
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
