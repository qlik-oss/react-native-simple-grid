package com.qliktrialreactnativestraighttable;

import android.graphics.Color;

import com.facebook.react.bridge.ReadableMap;

public class Indicator {
  boolean applySegmentColors = false;
  int color = Color.BLACK;
  int index = -1;
  String position = null;
  boolean showTextValues = false;
  char icon;
  boolean hasIcon = false;

  Indicator(ReadableMap data) {
    applySegmentColors = data.hasKey("applySegmentColors") ? data.getBoolean("applySegmentColors") : false;
    showTextValues = data.hasKey("showTextValues") ? data.getBoolean("showTextValues") : false;
    position = data.hasKey("position") ? data.getString("position") : null;
    index = data.hasKey("index") ? data.getInt("index") : -1;
    parseColor(data);
  }

  private void parseColor(ReadableMap data) {
    if (data.hasKey("color")) {
      color = Color.parseColor(data.getString("color"));
    }
    getIcon(data);
  }

  private void getIcon(ReadableMap data) {
    if (data.hasKey("icon")) {
      hasIcon = true;
      String iconKey = data.getString("icon");
      switch (iconKey) {
        case "m":
          icon = 0xe96c;
          break;
        case "è":
          icon = 0xe997;
          break;
        case "ï":
          icon = 0xe951;
          break;
        case "R":
          icon = 0xe97f;
          break;
        case "S":
          icon = 0xe97c;
          break;
        case "T":
          icon = 0xe97d;
          break;
        case "U":
          icon = 0xe97e;
          break;
        case "P":
          icon = 0xe906;
          break;
        case "Q":
          icon = 0xe8e4;
          break;
        case "¢":
          icon = 0xe8a8;
          break;
        case "©":
          icon = 0xe894;
          break;
        case "23F4":
          icon = 0xe8c7;
          break;
        case "2013":
          icon = 0xe954;
          break;
        case "&":
          icon = 0xe8ff;
          break;
        case "add":
          icon = 0xe802;
          break;
        case "minus-2":
          icon = 0xe8e3;
          break;
        case "dot":
          icon = 0xe878;
          break;
        default:
          break;
      }
    }
  }


}