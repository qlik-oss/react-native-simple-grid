package com.qliktrialreactnativestraighttable;

import android.graphics.Color;

import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;
import org.json.JSONObject;

public class MiniChartInfo {
  public String type;
  public Boolean showDots;
  public YAxis yAxis;
  public ChartColors colors;

  class YAxis {
    public String position;
    public String scale;
    public YAxis(ReadableMap data) {
      position = data.hasKey("position") ? data.getString("position") : "";
      scale = data.hasKey("scale") ? data.getString("scale") : "";
    }

    public JSONObject toEvent() throws JSONException {
      JSONObject json = new JSONObject();
      json.put("position", position);
      json.put("scale", scale);
      return json;
    }
  }

  class MiniChartColor {
    public String colorValue;
    public int index;
    int color;
    boolean valid = false;
    public MiniChartColor(ReadableMap data) {
      colorValue = data.hasKey("color") ? data.getString("color") : "none";
      index = data.hasKey("index") ? data.getInt("index") : 0;
      if(!colorValue.equals("none")) {
        color = Color.parseColor(colorValue);
        valid = true;
      }
    }

    public JSONObject toEvent() throws JSONException {
      JSONObject json = new JSONObject();
      json.put("index", index);
      json.put("color", colorValue);
      json.put("valid", valid);
      return json;
    }
  }

  class ChartColors {
    public MiniChartColor first;
    public MiniChartColor last;
    public MiniChartColor min;
    public MiniChartColor max;
    public MiniChartColor negative;
    public MiniChartColor positive;
    public MiniChartColor main;
    public ChartColors(ReadableMap data) {
      resetChartColors(data);
    }

    MiniChartColor getMiniChartColor(String name, ReadableMap data) {
      return  data.hasKey(name) ? new MiniChartColor(data.getMap(name)) : null;
    }

    public void resetChartColors(ReadableMap data) {
      first = getMiniChartColor("first", data);
      last = getMiniChartColor("last", data);
      min = getMiniChartColor("min", data);
      max = getMiniChartColor("max", data);
      negative = getMiniChartColor("negative", data);
      positive = getMiniChartColor("positive", data);
      main = getMiniChartColor("main", data);
    }

    public JSONObject toEvent() throws JSONException {
      JSONObject json = new JSONObject();
      json.put("first", first.toEvent());
      json.put("last", last.toEvent());
      json.put("min", min.toEvent());
      json.put("max", max.toEvent());
      json.put("negative", negative.toEvent());
      json.put("positive", positive.toEvent());
      json.put("main", main.toEvent());

      return json;
    }
  }

  MiniChartInfo(ReadableMap data) {
    type = data.hasKey("type") ? data.getString("type") : "";
    showDots = data.hasKey("showDots") && data.getBoolean("showDots");
    yAxis = data.hasKey("yAxis") ? new YAxis(data.getMap("yAxis")) : null;
    colors = data.hasKey("colors") ? new ChartColors(data.getMap("colors")) : null;
  }

  public JSONObject toEvent() throws JSONException {
    JSONObject json = new JSONObject();
    json.put("type", type);
    json.put("showDots", showDots);
    json.put("yAxis", yAxis.toEvent());
    json.put("colors", colors.toEvent());
    return json;
  }
}
