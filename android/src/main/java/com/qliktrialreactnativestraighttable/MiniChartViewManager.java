package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MiniChartViewManager extends SimpleViewManager<View> {
  public static final String REACT_CLASS = "MiniChartView";
  public ReadableMap column = null;
  public ReadableMap cell = null;
  @Override
  @NonNull
  public String getName() {
      return REACT_CLASS;
  }

  @SuppressLint("NewApi")
  @Override
  @NonNull
  public View createViewInstance(ThemedReactContext reactContext) {
    MiniChartView miniChartView = new MiniChartView(reactContext);
    return miniChartView;
  }

  @ReactProp(name = "colData")
  public void setCol(View view, ReadableMap col) {
    MiniChartView miniChartView = (MiniChartView) view;
    column = col;

    if (cell != null) {
      setupMiniChart(miniChartView);
    }
  }

  @ReactProp(name = "rowData")
  public void setCell(View view, ReadableMap cell) {
    MiniChartView miniChartView = (MiniChartView) view;
    this.cell = cell;

    if (column != null) {
      setupMiniChart(miniChartView);
    }
  }

  public void setupMiniChart(MiniChartView miniChartView) {
    DataColumn dataColumn = new DataColumn(column, 0);
    DataCell dataCell = new DataCell(cell, dataColumn, null);

    miniChartView.setData(dataCell, dataColumn);
  }
}
