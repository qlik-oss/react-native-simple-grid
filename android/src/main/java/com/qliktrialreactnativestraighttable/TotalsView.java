package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TotalsView extends AutoLinearLayout{
  List<DataColumn> dataColumns = null;
  TotalsView(Context context) {
    super(context);
  }

  public void setDataColumns(List<DataColumn> dataColumns) {
    this.dataColumns = dataColumns;
  }

  public void updateLayout() {
    for (int i = 0; i < this.dataColumns.size(); i++){
      TextView totalsCell = (TextView) this.getChildAt(i);
      ViewGroup.LayoutParams layoutParams = totalsCell.getLayoutParams();
      layoutParams.width = dataColumns.get(i).width;
      totalsCell.setLayoutParams(layoutParams);
    }
  }
}
