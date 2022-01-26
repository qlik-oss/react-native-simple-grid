package com.qliktrialreactnativestraighttable;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataProvider extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;
  List<DataRow> rows = null;
  List<DataColumn> dataColumns = null;
  Set<SimpleViewHolder> cachedViewHolders = new HashSet<>();
  SelectionsEngine selectionsEngine = null;
  DataSize dataSize = null;
  boolean loading = false;
  public final float minWidth = PixelUtils.dpToPx(40);

  public boolean isLoading() {
    return loading;
  }

  public void setLoading(boolean loading) {
    this.loading = loading;
  }

  public List<DataColumn> getDataColumns() {
    return dataColumns;
  }

  public class SimpleViewHolder extends RecyclerView.ViewHolder  {
    private final LinearLayout row;
    public SimpleViewHolder(View view) {
      super(view);
      row = (LinearLayout) view;
    }

    public void setBackGroundColor(int color) {
      row.setBackgroundColor(color);
    }
    public void setData(DataRow dataRow) {
      for(int i = 0; i < dataRow.cells.size(); i++) {
        int width = dataColumns.get(i).width;
        ClickableTextView view = (ClickableTextView) row.getChildAt(i);
        view.setLayoutParams(new LinearLayout.LayoutParams(width, TableTheme.rowHeight));
        view.setData(dataRow.cells.get(i));
        view.setText(dataRow.cells.get(i).qText);
        view.setGravity(dataRow.cells.get(i).textGravity | Gravity.CENTER_VERTICAL);
      }
    }

    public void onRecycled() {
      for(int i = 0; i <  row.getChildCount(); i++) {
        ClickableTextView view = (ClickableTextView) row.getChildAt(i);
        view.onRecycled();
      }
    }

    public boolean updateWidth(float width, int column) {
      View view =  row.getChildAt(column);
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();
      float newWidth = params.width + width;
      if(newWidth < minWidth) {
        return false;
      }

      if (!updateNeighbour(width, column)) {
        return false;
      }
      params.width = (int) newWidth;
      view.setLayoutParams(params);

      return true;
    }

    private boolean updateNeighbour(float width, int column) {
      if (column + 1 < DataProvider.this.dataColumns.size() ) {
        View neighbour =  row.getChildAt(column + 1);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) neighbour.getLayoutParams();
        float newWidth = params.width - width;
        if (newWidth < minWidth) {
          return false;
        }
        params.width = (int)newWidth;
        neighbour.setLayoutParams(params);
      }
      return true;
    }
  }

  public  class ProgressHolder extends RecyclerView.ViewHolder {
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
    RecyclerView.ViewHolder viewHolder;
    if (viewType == VIEW_TYPE_ITEM) {
      LinearLayout rowView = new LinearLayout(parent.getContext());
      rowView.setOrientation(LinearLayout.HORIZONTAL);
      for (int i = 0; i < dataColumns.size(); i++) {
        int width = dataColumns.get(i).width;
        ClickableTextView view = new ClickableTextView(parent.getContext(), this.selectionsEngine);
        view.setMaxLines(1);
        view.setEllipsize(TextUtils.TruncateAt.END);
        view.setLayoutParams(new LinearLayout.LayoutParams(width, TableTheme.rowHeight));
        rowView.addView(view);
        int leftPadding = (int)PixelUtils.dpToPx(16);
        view.setPadding(leftPadding, 0, (int) PixelUtils.dpToPx(16), 0);
        view.setGravity(Gravity.CENTER_VERTICAL);

      }
      SimpleViewHolder simpleViewHolder = new SimpleViewHolder(rowView);
      viewHolder = simpleViewHolder;
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
      holder.setData(rows.get(position));
      cachedViewHolders.add(holder);
    }
  }

  @Override
  public int getItemCount() {
    return rows.size();
  }

  public void setRows(List<DataRow> data, boolean resetData) {
    if (this.rows == null || resetData) {
      int prevSize = 0;
      if (this.rows != null) {
        prevSize = this.rows.size();
      }
      this.rows = data;
      if (needsMore()) {
        this.rows.add(null);
      }
      if (resetData && prevSize != 0) {
        this.notifyDataSetChanged();
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

  public boolean updateWidth(float deltaWidth, int column) {
    for(RecyclerView.ViewHolder holder : cachedViewHolders) {
      SimpleViewHolder viewHolder = (SimpleViewHolder) holder;
      if (!viewHolder.updateWidth(deltaWidth, column)) {
        return false;
      }
    }

    int next = column + 1;
    if (next < dataColumns.size()) {
      dataColumns.get(next).width -= (int)deltaWidth;
    }

    dataColumns.get(column).width += (int)deltaWidth;

    return true;
  }

  @Override
  public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
    if (viewHolder instanceof  SimpleViewHolder) {
      SimpleViewHolder simpleViewHolder = (SimpleViewHolder) viewHolder;
      simpleViewHolder.onRecycled();
      cachedViewHolders.remove(simpleViewHolder);
    }
  }

  public void onEndPan() {
    WritableArray widths = Arguments.createArray();
    for(int i = 0; i < dataColumns.size(); i++) {
      widths.pushDouble(PixelUtils.pxToDp(dataColumns.get(i).width));
    }
    WritableMap event = Arguments.createMap();
    event.putArray("widths", widths);
    EventUtils.sendEventToJSFromView("onColumnsResized", event);
  }

}
