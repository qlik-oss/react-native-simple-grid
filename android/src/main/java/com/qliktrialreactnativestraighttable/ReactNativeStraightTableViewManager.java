package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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
    public void setCols(View view,  @Nullable ReadableArray columns) {
      HeaderViewFactory headerViewFactory = new HeaderViewFactory(columns, view.getContext());
      TableView tableView = getTableViewFrom(view);
      HeaderView headerView = headerViewFactory.getHeaderView();
      tableView.setHeaderView(headerView);
      tableView.setDataColumns(headerViewFactory.getDataColumns());
      Log.d("rn-table", "setting cols");
    }

    @ReactProp(name = "rows")
    public void setRows(View view, @Nullable ReadableMap rows) {
      ReadableArray dataRows = rows.getArray("rows");
      boolean resetData = rows.getBoolean("reset");
      RowFactory factory = new RowFactory(dataRows);
      TableView tableView = getTableViewFrom(view);
      tableView.setRows(factory.getRows(), resetData);
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
    return MapBuilder.of(
      "onEndReached",
      MapBuilder.of("registrationName", "onEndReached")
    );
  }
}
