package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

  public void updateTotals(List<DataColumn> cols, DataProvider dataProvider) {
    setDataColumns(cols);
    // Create new totals when there are new columns
    int totalCellCount = getChildCount();
    int numMissingCells = dataProvider.dataColumns.size() - totalCellCount;
    int numCells = dataProvider.totalsCells.size();
    int j = numCells - numMissingCells;
    for(int i = totalCellCount; i < dataProvider.dataColumns.size(); i++) {
      DataColumn column = dataProvider.dataColumns.get(i);
      TotalsViewCell totalsViewCell = HeaderViewFactory.createTotalsCell(getContext(), column,
        tableView);
      LinearLayout.LayoutParams totalsParams = new LinearLayout.LayoutParams(column.width, ViewGroup.LayoutParams.MATCH_PARENT);
      if (!column.isDim && j < numCells) {
        totalsViewCell.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        totalsViewCell.setText(dataProvider.totalsCells.get(j).qText);
        j++;
      }
      addView(totalsViewCell, totalsParams);
    }
    // Update totals in case of moved columns
    j = 0;
    for(int i = 0; i < getChildCount(); i++) {
      TotalsViewCell viewCell = (TotalsViewCell) getChildAt(i);
      viewCell.setText("");

      if(i > dataProvider.dataColumns.size() - 1) {
        removeView(viewCell);
        continue;
      }

      DataColumn column = dataProvider.dataColumns.get(i);
      viewCell.setColumn(column);
      if(!column.isDim && j < dataProvider.totalsCells.size()) {
        String newText = dataProvider.totalsCells.get(j).qText;
        viewCell.setText(newText != null && newText.length() > 0 ? newText : "");
        j++;
      }
    }

    DataColumn firstColumn = dataProvider.dataColumns.get(0);
    TotalsViewCell firstTotalsCell = (TotalsViewCell) getChildAt(0);
    if(tableView.isFirstColumnFrozen) {
      firstTotalsCell = tableView.tableViewFactory.firstColumnTotalsCell;
    }
    if (firstColumn.isDim) {
      firstTotalsCell.setText(tableView.totalsLabel);
      firstTotalsCell.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    } else {
      TotalsCell totalsCell = dataProvider.totalsCells.get(0);
      firstTotalsCell.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
      firstTotalsCell.setText(totalsCell.qText);
    }
  }
}
