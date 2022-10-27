package com.qliktrialreactnativestraighttable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;
import java.util.List;

public class qMatrix {
  public List<qMatrixRow> rows = new ArrayList<>();

  class qMatrixColumn {
    public double qElemNumber;
    public double qNum;
    public String qText;
    public qMatrixColumn(ReadableMap data) {
      qElemNumber = data.hasKey("qElemNumber") ? data.getDouble("qElemNumber") : 0.0;
      qNum = data.hasKey("qNum") ? data.getDouble("qNum") : 0.0;
      qText = data.hasKey("qText") ? data.getString("qText") : "";
    }
  }

  class qMatrixRow {
    public List<qMatrixColumn> columns = new ArrayList<>();
    public qMatrixRow(ReadableArray dataArray) {
      for(int i = 0; i < dataArray.size(); i++) {
        columns.add(new qMatrixColumn(dataArray.getMap(i)));
      }
    }
  }

  public qMatrix(ReadableArray dataArray) {
    for(int i = 0; i < dataArray.size(); i++) {
      ReadableArray da = dataArray.getArray(i);
      rows.add(new qMatrixRow(da));
    }
  }

}
