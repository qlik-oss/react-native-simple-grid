package com.qliktrialreactnativestraighttable;

import android.graphics.Color;
import android.util.Log;
import android.webkit.URLUtil;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataCell {
  String type;
  String qText;
  Double qNum;
  int qElemNumber;
  String  qState;
  String imageUrl;
  int rowIdx;
  int colIdx;
  int columnIndex;
  boolean  isDim = false;
  int rawRowIdx;
  int  rawColIdx;
  boolean isNumber;
  qMiniChart miniChart;
  Indicator indicator;
  int cellForegroundColor;
  int cellBackgroundColor;
  boolean cellForegroundColorValid = false;
  boolean cellBackgroundColorValid = false;
  List<Object> qAttrExps;
  public DataCell(ReadableMap source, DataColumn column) {
    type = column.representation.type;
    qText = source.getString("qText");
    qElemNumber =  source.getInt("qElemNumber");
    qState =  source.getString("qState");
    rowIdx =  source.getInt("rowIdx");
    colIdx =  source.getInt("colIdx");
    columnIndex = column.columnIndex;
    rawRowIdx =  source.getInt("rawRowIdx");
    rawColIdx =  source.getInt("rawColIdx");
    ReadableType qNumType = source.getType("qNum");
    isNumber = qNumType == ReadableType.Number;
    if (source.hasKey("isDim")) {
      isDim =  source.getBoolean("isDim");
    }
    if(source.hasKey("isSelectable")) {
      isDim = source.getBoolean("isSelectable");
    }
    if(source.hasKey("qAttrExps")) {
      qAttrExps = source.getMap("qAttrExps").getArray("qValues").toArrayList();
      int urlId = column.stylingInfo.indexOf("imageUrl");
      if(urlId != -1) {
        String url = ((HashMap<String, String>) qAttrExps.get(urlId)).get("qText");
        if (URLUtil.isValidUrl(url)) {
          imageUrl = url;
          DataProvider.addImagePath(imageUrl);
        }
      }
    }
    updateCellColors(source);
    miniChart = source.hasKey("qMiniChart") ? new qMiniChart(source.getMap("qMiniChart")) : null;
    indicator = source.hasKey("indicator") ? new Indicator(source.getMap("indicator")) : null;

  }

  private void updateCellColors(ReadableMap data) {
    try {
      String fgColor = JsonUtils.getString(data, "cellForegroundColor");
      if (fgColor != null) {
        cellForegroundColorValid = true;
        cellForegroundColor = Color.parseColor(fgColor);
      }

      String bgColor = JsonUtils.getString(data, "cellBackgroundColor");
      if (bgColor != null) {
        cellBackgroundColorValid = true;
        cellBackgroundColor = Color.parseColor(bgColor);
      }
    } catch (Exception e) {
      Log.e("ReactNativeSimpleGrid", e.getMessage());
    }
  }

  public JSONObject toEvent() throws JSONException {
    JSONObject cell = new JSONObject();

    cell.put("qText", qText);
    cell.put("qNum", qNum);
    cell.put("qElemNumber", qElemNumber);
    cell.put("qState", qState);
    cell.put("imageUrl", imageUrl);
    cell.put("rowIdx", rowIdx);
    cell.put("colIdx", colIdx);
    cell.put("isDim", isDim);
    cell.put("rawRowIdx", rawRowIdx);
    cell.put("rawColIdx", rawColIdx);
    cell.put("isNumber", isNumber);
    cell.put("miniChart", miniChart);
    cell.put("indicator", indicator);

    return cell;
  }
}
