package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.react.bridge.ReadableArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@SuppressLint("NotifyDataSetChanged")
public class DataProvider extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private final int NUM_LINES = 1;
  private final int FONT_SIZE = 14;
  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;
  final SelectionsEngine selectionsEngine;
  boolean isDataView = false;
  List<DataRow> rows = null;
  List<DataColumn> dataColumns = null;
  List<TotalsCell> totalsCells = null;
  Set<RowViewHolder> cachedViewHolders = new HashSet<>();
  Set<RowViewHolder> cachedFirstColumnViewHolders = new HashSet<>();
  ColumnWidths columnWidths;
  DataSize dataSize = null;
  boolean loading = false;
  String totalsLabel;
  String totalsPosition;
  Boolean isFirstColumnFrozen = false;
  final TableView tableView;
  public static final float minWidth = PixelUtils.dpToPx(80);

  public DataProvider(ColumnWidths columnWidths, SelectionsEngine selectionsEngine, TableView tableView) {
    this.columnWidths = columnWidths;
    this.selectionsEngine = selectionsEngine;
    this.tableView = tableView;
  }

  public boolean isLoading() {
    return loading;
  }

  public void setLoading(boolean loading) {
    this.loading = loading;
  }

  public void setTotals(ReadableArray totals, String totalsLabel, String totalsPosition) {
    if(this.isDataView) {
      this.totalsCells = null;
      this.totalsLabel = null;
      this.totalsPosition = "noTotals";
      return;
    }

    if(totals != null) {
      this.totalsCells = HeaderViewFactory.getTotalsCellList(totals);
      this.totalsLabel = totalsLabel;
    }
  }

  public void setFirstColumnFrozen(boolean firstColumnFrozen) {
    this.isFirstColumnFrozen = firstColumnFrozen;
  }
  public static DataColumn getDataColumnByIdx(int idx, List<DataColumn> columns) {
    for(DataColumn column : columns) {
      if(column.dataColIdx == idx) {
        return column;
      }
    }
    return null;
  }

  public static DataColumn getDataColumnByRawIdx(int idx, List<DataColumn> columns) {
    for(DataColumn column : columns) {
      if(column.columnIndex == idx) {
        return column;
      }
    }
    return null;
  }

  public List<DataColumn> getDataColumns() {
    return dataColumns;
  }


  public boolean isInitialized() {
    return dataColumns != null && rows != null;
  }

  public class ProgressHolder extends RecyclerView.ViewHolder {
    private final RelativeLayout row;
    public ProgressHolder(View view) {
      super(view);
      row = (RelativeLayout) view;
      ProgressBar bar = new ProgressBar(view.getContext());
      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)PixelUtils.dpToPx(48));
      bar.setLayoutParams(layoutParams);
      row.setGravity(Gravity.CENTER);
      int padding = (int)PixelUtils.dpToPx(8);
      bar.setPadding(0, padding, 0, padding);
      row.addView(bar);
    }
  }


  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    CustomRecyclerView recyclerView = (CustomRecyclerView) parent;
    RecyclerView.ViewHolder viewHolder;
    if (viewType == VIEW_TYPE_ITEM) {
      LinearLayout rowView = new LinearLayout(parent.getContext());
      rowView.setOrientation(LinearLayout.HORIZONTAL);
      int numColumns = recyclerView.firstColumnOnly ? 1 : dataColumns.size();
      for (int i = 0; i < numColumns; i++) {
        DataColumn column = dataColumns.get(i);
        float width = column.width;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)width, tableView.rowHeight);

        if (column.representation.type.equals("image") && !isDataView) {
          CellView cellView = new CellView(parent.getContext(), "image", this.selectionsEngine, this.tableView, recyclerView.firstColumnOnly, column);
          LinearLayout.LayoutParams cellLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

          rowView.addView(cellView, cellLayoutParams);

        } else if(column.representation.type.equals("miniChart")  && !isDataView) {
          CellView cellView = new CellView(parent.getContext(), "miniChart", this.selectionsEngine, this.tableView, recyclerView.firstColumnOnly, column);

          rowView.addView(cellView);
        } else {
          CellView cellView = new CellView(parent.getContext(), "text", this.selectionsEngine, this.tableView, recyclerView.firstColumnOnly, column);
          ClickableTextView textView = (ClickableTextView) cellView.content;

          textView.setMaxLines(NUM_LINES);
          textView.setEllipsize(TextUtils.TruncateAt.END);
          textView.setTextSize(tableView.cellContentStyle.fontSize);
          textView.setLayoutParams(layoutParams);

          rowView.addView(cellView);
        }
      }
      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, tableView.rowHeight);
      rowView.setLayoutParams(layoutParams);
      RowViewHolder rowViewHolder = new RowViewHolder(rowView, this, recyclerView.firstColumnOnly);
      viewHolder = rowViewHolder;
    } else {
      RelativeLayout rowView = new RelativeLayout(parent.getContext());
      viewHolder = new ProgressHolder(rowView);
    }
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
    if(viewHolder instanceof RowViewHolder) {
      RowViewHolder holder = (RowViewHolder) viewHolder;
      if (this.isDataView) {
        int color = position % 2 == 0 ? Color.WHITE : 0xFFF7F7F7;
        holder.setBackGroundColor(color);
      }
      holder.setData(rows.get(position), tableView.rowHeight, tableView.cellContentStyle);
      cachedViewHolders.add(holder);
    }
  }

  @Override
  public int getItemCount() {
    return rows.size();
  }

  public void setDataView(boolean isDataView) {
    this.isDataView = isDataView;
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
    if(dataColumns == null) {
      dataColumns = cols;
      return;
    }

    dataColumns = cols.stream().peek(col -> {
        DataColumn column = cols.stream()
          .filter(dataCol -> dataCol.dataColIdx == col.dataColIdx)
          .findAny()
          .orElse(col);
          col.width = column.width == 0 ? columnWidths.resizeColumnByAverage(column, rows, true, tableView.totalWidth) : column.width;
    }).collect(Collectors.toList());
  }

  public void setDataSize(DataSize dataSize) {
    this.dataSize = dataSize;
    if (needsMore()) {
      if(!this.rows.isEmpty()) {
        if (this.rows.get(this.rows.size() - 1) != null) {
          this.rows.add(null);
        }
      }
    }
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
      RowViewHolder viewHolder = (RowViewHolder) holder;
      if (!viewHolder.updateWidth(deltaWidth, column)) {
        return false;
      }
    }

    dataColumns.get(column).width += (int)deltaWidth;
    columnWidths.updateWidths();

    return true;
  }

  public void updateRowHeight(int rowHeight) {
    for(RecyclerView.ViewHolder holder : cachedViewHolders) {
      RowViewHolder viewHolder = (RowViewHolder) holder;
      viewHolder.updateHeight(rowHeight);
    }
    notifyDataSetChanged();
  }

  @Override
  public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
    if (viewHolder instanceof  RowViewHolder) {
      RowViewHolder RowViewHolder = (RowViewHolder) viewHolder;
      RowViewHolder.onRecycled();
      cachedViewHolders.remove(RowViewHolder);
    }
  }

  public void onEndPan() {
    columnWidths.syncWidths();
    tableView.onEndPan();
  }

  public void invalidateLayout() {
    for(DataColumn column : dataColumns) {
      for (RecyclerView.ViewHolder holder : cachedViewHolders) {
        RowViewHolder viewHolder = (RowViewHolder) holder;
        viewHolder.setWidth(column.width, column.columnIndex);
      }
    }
    this.notifyDataSetChanged();
  }

}
