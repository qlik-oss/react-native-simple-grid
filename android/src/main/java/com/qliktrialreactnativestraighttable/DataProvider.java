package com.qliktrialreactnativestraighttable;

import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DataProvider extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;
  List<DataRow> rows = null;
  List<DataColumn> dataColumns = null;
  DataSize dataSize = null;

  public boolean isLoading() {
    return loading;
  }

  public void setLoading(boolean loading) {
    this.loading = loading;
  }

  boolean loading = false;

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

  public static class ProgressHolder extends RecyclerView.ViewHolder {
    private final RelativeLayout row;
    public ProgressHolder(View view) {
      super(view);
      row = (RelativeLayout) view;
      ProgressBar bar = new ProgressBar(view.getContext());
      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableTheme.rowHeight);
      bar.setLayoutParams(layoutParams);
      row.setGravity(Gravity.CENTER);
      int padding = (int)PixelUtils.dpToPx(8);
      bar.setPadding(0, padding, 0, padding);
      row.addView(bar);
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder = null;
    if (viewType == VIEW_TYPE_ITEM) {
      LinearLayout rowView = new LinearLayout(parent.getContext());
      rowView.setOrientation(LinearLayout.HORIZONTAL);
      for (int i = 0; i < dataColumns.size(); i++) {
        int width = (int) PixelUtils.dpToPx(dataColumns.get(i).width - TableTheme.delta);
        TextView view = new TextView(parent.getContext());
        view.setMaxLines(1);
        view.setEllipsize(TextUtils.TruncateAt.END);
        view.setLayoutParams(new LinearLayout.LayoutParams(width, TableTheme.rowHeight));
        rowView.addView(view);
        int leftPadding = i == 0 ? (int) PixelUtils.dpToPx(16) : 0;
        view.setPadding(leftPadding, 0, (int) PixelUtils.dpToPx(16), 0);
        view.setGravity(Gravity.CENTER_VERTICAL);
      }
      viewHolder = new SimpleViewHolder(rowView);
    } else {
      RelativeLayout rowView = new RelativeLayout(parent.getContext());
      viewHolder = new ProgressHolder(rowView);
    }
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
    if(viewHolder instanceof SimpleViewHolder) {
      SimpleViewHolder holder = (SimpleViewHolder) viewHolder;
      int color = position % 2 == 0 ? Color.WHITE : 0xFFF7F7F7;
      holder.setBackGroundColor(color);
      holder.setData(rows.get(position), dataColumns);
    }
  }

  @Override
  public int getItemCount() {
    return rows.size();
  }

  public void setRows(List<DataRow> data) {
    if (this.rows == null) {
      this.rows = data;
      if (needsMore()) {
        this.rows.add(null);
      }
    } else {
      // remove last item
      this.rows.remove(this.rows.size() - 1);
      this.rows.addAll(data);
      if(needsMore()) {
        this.rows.add(null);
      }
      this.notifyDataSetChanged();
    }
    setLoading(false);
  }
  public void setDataColumns(List<DataColumn> cols) {
    this.dataColumns = cols;
  }

  public void setDataSize(DataSize dataSize) {
    this.dataSize = dataSize;
    if (needsMore()) {
      if (this.rows.get(this.rows.size() - 1) != null) {
        this.rows.add(null);
      }
    }
  }

  public boolean ready() {
    return this.rows != null && dataSize != null;
  }

  public int getItemViewType(int position) {
    return this.rows.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  public boolean needsMore() {
    if (this.rows != null && this.dataSize != null) {
      return this.rows.size() < this.dataSize.qcy;
    }
    return false;
  }
}
