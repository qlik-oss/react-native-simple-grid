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

  Representation(ReadableMap data) {
    type = data.getString("type");
    imageUrl = data.getString("imageUrl");
    imageSize = data.getString("imageSize");
    imagePosition = data.getString("imagePosition");
    imageSize = data.getString("imageSize");

    globalMax = data.hasKey("globalMax") ? data.getDouble("globalMax") : 0.0;
    globalMin = data.hasKey("globalMin") ? data.getDouble("globalMin") : 0.0;
    miniChart = data.hasKey("miniChart") ? new MiniChartInfo(data.getMap("miniChart")) : null;
  }
}
