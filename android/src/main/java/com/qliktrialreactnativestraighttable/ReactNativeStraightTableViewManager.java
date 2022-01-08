package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

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
      TableView tableView =  new TableView(reactContext);
      tableView.setOrientation(LinearLayout.VERTICAL);
      return tableView;
    }

    @ReactProp(name = "theme")
    public void setTheme(View view, ReadableMap theme) {
      TableTheme.from(theme);
      TableView tableView = (TableView) view;
      tableView.updateTheme();
    }

    @ReactProp(name = "cols")
    public void setCols(View view,  @Nullable ReadableArray columns) {
      HeaderViewFactory headerViewFactory = new HeaderViewFactory(columns, view.getContext());
      TableView tableView = (TableView) view;
      LinearLayout headerView = headerViewFactory.getHeaderView();
      tableView.setHeaderView(headerView);
      tableView.setDataColumns(headerViewFactory.getDataColumns());
    }

    @ReactProp(name = "rows")
    public void setRows(View view, @Nullable ReadableArray rows) {
      RowFactory factory = new RowFactory(rows);
      TableView tableView = (TableView) view;
      tableView.setRows(factory.getRows());
    }

}
