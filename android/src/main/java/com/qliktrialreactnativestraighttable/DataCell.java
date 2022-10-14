package com.qliktrialreactnativestraighttable;

import android.view.Gravity;
import android.webkit.URLUtil;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

import java.util.ArrayList;
import java.util.HashMap;

public class DataCell {
  String qText;
  Double  qNum;
  int  qElemNumber;
  String  qState;
  String imageUrl;
  int  rowIdx;
  int  colIdx;
  boolean  isDim = false;
  int rawRowIdx;
  int  rawColIdx;
  boolean isNumber;
  int textGravity = Gravity.LEFT;
  public DataCell(ReadableMap source, DataColumn column) {
    qText = source.getString("qText");
    qElemNumber =  source.getInt("qElemNumber");
    qState =  source.getString("qState");
    rowIdx =  source.getInt("rowIdx");
    colIdx =  source.getInt("colIdx");
    rawRowIdx =  source.getInt("rawRowIdx");
    rawColIdx =  source.getInt("rawColIdx");
    ReadableType qNumType = source.getType("qNum");
    isNumber = qNumType == ReadableType.Number;
    if (isNumber) {
      textGravity = Gravity.RIGHT;
    }
    if (source.hasKey("isDim")) {
      isDim =  source.getBoolean("isDim");
    }
    if(source.hasKey("isSelectable")) {
      isDim = source.getBoolean("isSelectable");
    }
    if(source.hasKey("qAttrExps")) {
      ArrayList attrExps = source.getMap("qAttrExps").getArray("qValues").toArrayList();
      int urlId = column.stylingInfo.indexOf("imageUrl");
      String url = ((HashMap<String, String>) attrExps.get(urlId)).get("qText");
      if(URLUtil.isValidUrl(url)) {
        imageUrl = url;
        DataProvider.addImagePath(imageUrl);
      }
    }

  }

}
