package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

@SuppressLint("ViewConstructor")
public class TotalsView extends AutoLinearLayout{
  List<DataColumn> dataColumns = null;
  final TableView tableView;
  TotalsView(Context context, TableView tableView) {
    super(context);
    this.tableView = tableView;
  }

  public void setDataColumns(List<DataColumn> dataColumns) {
    this.dataColumns = dataColumns;
  }

  public void updateLayout() {
    for (int i = 0; i < this.dataColumns.size(); i++){
      TotalsViewCell totalsCell = (TotalsViewCell) this.getChildAt(i);
      ViewGroup.LayoutParams layoutParams = totalsCell.getLayoutParams();
      layoutParams.width = dataColumns.get(i).width;
      totalsCell.setLayoutParams(layoutParams);
    }
  }

  public void testTextWrap() {
    for (int i = 0; i < this.dataColumns.size(); i++) {
      TotalsViewCell totalsCell = (TotalsViewCell) this.getChildAt(i);
      totalsCell.testTextWrap();
    }
  }

  public int getMaxLineCount() {
    if(this.getChildCount() == 0) {
      return 1;
    }
    int lineCount = 0;
    for(int i = 0; i < this.dataColumns.size(); i++) {
      TotalsViewCell totalsCell = (TotalsViewCell) this.getChildAt(i);
      lineCount = Math.max(lineCount, totalsCell.getMeasuredLinedCount());
    }

    for(int i = 0; i < this.dataColumns.size(); i++) {
      TotalsViewCell totalsCell = (TotalsViewCell)this.getChildAt(i);
      totalsCell.setMaxLines(lineCount, dataColumns.get(i));
    }
    return lineCount;
  }
}
