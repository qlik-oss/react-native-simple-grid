package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TableView extends LinearLayout {
  LinearLayout headerView = null;
  RecyclerView recyclerView = null;
  DataProvider dataProvider = new DataProvider();
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

  public void setDataColumns(List<DataColumn> cols) {
    dataProvider.setDataColumns(cols);
    if (dataProvider.ready()) {
      createRecyclerView();
    }
  }

  public void setRows(List<DataRow> rows) {
    dataProvider.setRows(rows);
    if (this.headerView != null) {
      createRecyclerView();
    }
  }

  void createRecyclerView() {
    if (recyclerView == null) {
      LinearLayoutManager linearLayout = new LinearLayoutManager(this.getContext());
      recyclerView = new RecyclerView(this.getContext());
      recyclerView.setLayoutManager(linearLayout);
      recyclerView.setAdapter(dataProvider);
      recyclerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
      this.addView(recyclerView, 1);
    }
  }
}
