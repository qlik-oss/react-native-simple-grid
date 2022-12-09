package com.qliktrialreactnativestraighttable;

import android.util.Log;

import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;
import org.json.JSONObject;

public class Representation {
  public String imageUrl;
  public String imageSize;
  public String imagePosition;
  public String type;
  public double globalMax = 0.0;
  public double globalMin = 0.0;
  public MiniChartInfo miniChart;
  public String linkUrl;
  public String linkLabel;
  public String urlPosition;
  public String imageSetting;

  Representation(ReadableMap data) {
    type = data.getString("type");
    if(type == null) {
      type = "text";
    }
    imageUrl = data.getString("imageUrl");
    imageSize = data.getString("imageSize");
    imagePosition = data.getString("imagePosition");
    linkUrl = JsonUtils.getString(data, "linkUrl");
    linkLabel = JsonUtils.getString(data, "linkLabel");
    urlPosition = JsonUtils.getString(data, "urlPosition");
    imageSetting = JsonUtils.getString(data, "imageSetting");
    globalMax = data.hasKey("globalMax") ? data.getDouble("globalMax") : 0.0;
    globalMin = data.hasKey("globalMin") ? data.getDouble("globalMin") : 0.0;
    miniChart = data.hasKey("miniChart") ? new MiniChartInfo(data.getMap("miniChart")) : null;
  }

  public JSONObject toEvent() throws JSONException {
    JSONObject column = new JSONObject();
    column.put("type", type);
    column.put("imageUrl", imageUrl);
    column.put("imageSize", imageSize);
    column.put("imagePosition", imagePosition);
    column.put("linkUrl", linkUrl);
    column.put("linkLabel", linkLabel);
    column.put("urlPosition", urlPosition);
    column.put("globalMax", globalMax);
    column.put("globalMin", globalMin);
    column.put("imageSetting", imageSetting);
    if(miniChart != null) {
      column.put("miniChart", miniChart.toEvent());
    }

    return column;
  }
}
