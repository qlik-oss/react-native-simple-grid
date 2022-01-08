package com.qliktrialreactnativestraighttable;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DataProvider extends RecyclerView.Adapter<DataProvider.SimpleViewHolder> {
  List<DataRow> rows = new ArrayList<>();
  List<DataColumn> dataColumns = null;

  public static class SimpleViewHolder extends RecyclerView.ViewHolder {
    private final LinearLayout row;
    public SimpleViewHolder(View view) {
      super(view);
      row = (LinearLayout) view;
    }

    public void setBackGroundColor(int color) {
      row.setBackgroundColor(color);
    }
    public void setData(DataRow dataRow, List<DataColumn> columns) {
      for(int i = 0; i < dataRow.cells.size(); i++) {
        TextView view = (TextView) row.getChildAt(i);
        view.setText(dataRow.cells.get(i).qText);
      }
    }
  }

  @Override
  public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LinearLayout rowView = new LinearLayout(parent.getContext());
    rowView.setOrientation(LinearLayout.HORIZONTAL);
    for(int i = 0; i < dataColumns.size(); i++) {
      int width = (int)PixelUtils.dpToPx(dataColumns.get(i).width - 50);
      TextView view = new TextView(parent.getContext());
      view.setLayoutParams(new LinearLayout.LayoutParams(width, TableTheme.rowHeight));
      rowView.addView(view);
    }
    return new SimpleViewHolder(rowView);
  }

  @Override
  public void onBindViewHolder(SimpleViewHolder viewHolder, final int position) {
    int color = position % 2 == 0 ? Color.WHITE : 0xFFF7F7F7;
    viewHolder.setBackGroundColor(color);
    viewHolder.setData(rows.get(position), dataColumns);
  }

  @Override
  public int getItemCount() {
    return rows.size();
  }

  public void setRows(List<DataRow> data) {
    this.rows = data;
  }
  public void setDataColumns(List<DataColumn> cols) {
    this.dataColumns = cols;
  }

  public boolean ready() {
    return this.rows != null;
  }
}
