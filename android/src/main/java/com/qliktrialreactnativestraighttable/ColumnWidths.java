package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColumnWidths {
  List<DataColumn> dataColumns;
  protected SharedPreferences preferences ;
  protected List<Integer> widths = new ArrayList<>();
  protected String name = "";
  protected final Context context;
  public final String NATIVE_TABLES_KEY = "native.tables";

  ColumnWidths(Context context) {
    this.context = context;
    preferences = context.getSharedPreferences(NATIVE_TABLES_KEY, 0);
  }

  public void setName(String value) {
    name = value;
  }

  public void loadWidths(int frameWidth, List<DataColumn> dataColumns, List<DataRow> rows) {
    this.dataColumns = dataColumns;
    widths.clear();
    if(!loadWidthsFromStorage()) {
      loadDefaultWidths(frameWidth, rows);
    }

    for(DataColumn col : dataColumns) {
      widths.add(col.width);
    }
  }

  private void loadDefaultWidths(int frameWidth, List<DataRow> rows) {
    int runningTotal = 0;
    for (DataColumn col : dataColumns) {
      col.width = resizeColumnByAverage(col, rows);
      runningTotal += col.width;
    }

    if (runningTotal < frameWidth) {
      int defaultWidth = frameWidth / dataColumns.size();
      for (DataColumn column : dataColumns) {
        column.width = defaultWidth;
      }
    }
  }

  private boolean loadWidthsFromStorage() {
    String key = buildTableKey();
    String value = preferences.getString(key, null);
    if(value == null) {
      return false;
    }
    try {
      JSONArray jsonArray = new JSONArray(value);
      for(int i = 0; i< jsonArray.length(); i++) {
        JSONArray jsonWidths = jsonArray.getJSONArray(i);
        if(jsonWidths.length() != dataColumns.size()) {
          return false;
        }
        for(int j = 0; j < jsonWidths.length(); j++) {
          dataColumns.get(j).width = jsonWidths.getInt(j);
        }
      }
      return true;
    } catch (Exception exception) {
      Log.e("ColumnWidths", exception.getMessage());
    }
    return false;
  }

  private int resizeColumnByAverage(DataColumn column, List<DataRow> rows) {
    int runningTotal = 0;
    Paint paint = new Paint();
    for(DataRow row : rows) {
      if (row != null) {
        String text = row.cells.get(column.columnIndex).qText;
        if(text != null) {
          runningTotal += text.length();
        }
        else {
          // give it something
          runningTotal += DataProvider.minWidth;
        }
      }
    }
    int averageTextSize = runningTotal / rows.size();
    // Create a string with max text
    String tempString = new String(new char[averageTextSize]).replace("\0", "X");
    float width = paint.measureText(tempString, 0, tempString.length());
    width = Math.max(DataProvider.minWidth * 1.5f, PixelUtils.dpToPx(width));
    // cast later since this is the equiv of a floor.
    return (int)width;
  }

  public void updateWidths(List<DataColumn> columns) {
    if(dataColumns == null) {
      return;
    }
    if(dataColumns.size() == widths.size()) {
      dataColumns = columns;
      for (int i = 0; i < widths.size(); i++) {
        DataColumn column = dataColumns.get(i);
        column.width = widths.get(i);
      }
    }
  }

  public void updateWidths() {
    if(dataColumns.size() == widths.size()) {
      for (int i = 0; i < dataColumns.size(); i++) {
        widths.set(i, dataColumns.get(i).width);
      }
    }
  }

  public int getTotalWidth() {
    return widths.stream().reduce(0, (a, b) -> a + b);
  }

  public void syncWidths() {
    String key = buildTableKey();
    SharedPreferences.Editor editor = preferences.edit();
    JSONArray jsonArray = new JSONArray(Arrays.asList(widths));
    String value = jsonArray.toString();
    editor.putString(key, value);
    editor.apply();
  }

  private String buildTableKey() {
    int orientation = context.getResources().getConfiguration().orientation;
    String key = "";
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      key = String.format("%s.LANDSCAPE", name);
    } else {
      key = String.format("%s.PORTRAIT", name);
    }
    return key;
  }
}
