package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;

import com.facebook.react.bridge.ReadableArray;

import java.util.ArrayList;
import java.util.List;

public class HeaderViewFactory {
  List<DataColumn> dataColumns = new ArrayList<>();

  public LinearLayout getHeaderView() {
    return headerView;
  }

  LinearLayout headerView ;
  public HeaderViewFactory(ReadableArray readableArray, Context context) {
    headerView = new LinearLayout(context);
    headerView.setBackgroundColor(Color.DKGRAY);
    headerView.setMinimumHeight(300);
    for(int i = 0; i < readableArray.size(); i++) {
      DataColumn column = new DataColumn(readableArray.getMap(i));
      dataColumns.add(column);
    }

  }
}
