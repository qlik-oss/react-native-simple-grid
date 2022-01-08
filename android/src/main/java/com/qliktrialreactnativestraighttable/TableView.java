package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.widget.LinearLayout;

public class TableView extends LinearLayout {
  LinearLayout headerView = null;
  TableView(Context context) {
    super(context);
  }

  public void setHeaderView(LinearLayout view) {
    this.headerView = view;
    this.addView(this.headerView, 0);
  }

  public void updateTheme() {
    if (headerView != null) {
      headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    }

  }
}
