package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableMap;

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

  Representation(ReadableMap data) {
    type = data.getString("type");
    imageUrl = data.getString("imageUrl");
    imageSize = data.getString("imageSize");
    imagePosition = data.getString("imagePosition");
    imageSize = data.getString("imageSize");
    linkUrl = JsonUtils.getString(data, "linkUrl");
    linkLabel = JsonUtils.getString(data, "linkLabel");
    urlPosition = JsonUtils.getString(data, "urlPosition");
    globalMax = data.hasKey("globalMax") ? data.getDouble("globalMax") : 0.0;
    globalMin = data.hasKey("globalMin") ? data.getDouble("globalMin") : 0.0;
    miniChart = data.hasKey("miniChart") ? new MiniChartInfo(data.getMap("miniChart")) : null;
  }
}
