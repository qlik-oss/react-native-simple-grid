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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@SuppressLint("NotifyDataSetChanged")
public class DataProvider extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  public static Set<String> imagePaths = new HashSet<>();
  public static Map<String, Bitmap> imageData = new HashMap<>();
  private final int NUM_LINES = 1;
  private final int FONT_SIZE = 14;
  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;
  final SelectionsEngine selectionsEngine;
  boolean isDataView = false;
  List<DataRow> rows = null;
  List<DataColumn> dataColumns = null;
  Set<RowViewHolder> cachedViewHolders = new HashSet<>();
  Set<RowViewHolder> cachedFirstColumnViewHolders = new HashSet<>();
  ColumnWidths columnWidths;
  DataSize dataSize = null;
  boolean loading = false;
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

  public void setFirstColumnFrozen(boolean firstColumnFrozen) {
    this.isFirstColumnFrozen = firstColumnFrozen;
  }

  public List<DataColumn> getDataColumns() {
    return dataColumns;
  }

  public static Bitmap getImageData(String url) {
    return imageData.get(url);
  }

  public static void addImagePath(String imageUrl) {
    if(!URLUtil.isValidUrl(imageUrl)) {
      return;
    }
    imagePaths.add(imageUrl);
  }

  public static void fetchImages() {
    final CountDownLatch latch = new CountDownLatch(imagePaths.size());
    Iterator<String> iterator = imagePaths.iterator();
    while (iterator.hasNext()) {
      String imageUrl = iterator.next();
      boolean isDuplicateImageUrl = imageData.containsKey(imageUrl);
      if(isDuplicateImageUrl || !URLUtil.isValidUrl(imageUrl)) {
        latch.countDown();
        continue;
      }
      imageData.put(imageUrl, null);
      try {
        HttpUtils.get(imageUrl, new Callback() {
            public void onResponse(Call call, Response response) {
              InputStream inputStream = response.body().byteStream();
              Bitmap bitmap = PixelUtils.byteStreamToBitmap(inputStream);
              imageData.replace(imageUrl, bitmap);
              latch.countDown();
            }

            public void onFailure(Call call, IOException e) {
              latch.countDown();
            }
        });
      } catch(Exception e) {
        latch.countDown();
        imageData.remove(imageUrl);
        e.printStackTrace();
      }
    }
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public class ProgressHolder extends RecyclerView.ViewHolder {
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
    CustomRecyclerView recyclerView = (CustomRecyclerView) parent;
    RecyclerView.ViewHolder viewHolder;
    if (viewType == VIEW_TYPE_ITEM) {
      LinearLayout rowView = new LinearLayout(parent.getContext());
      rowView.setOrientation(LinearLayout.HORIZONTAL);
      int numColumns = recyclerView.firstColumnOnly ? 1 : dataColumns.size();
      for (int i = 0; i < numColumns; i++) {
        DataColumn column = dataColumns.get(i);
        float width = column.width;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams((int)width, TableTheme.rowHeight);

        if (column.representation.type.equals("image")) {
          RelativeLayout wrapper = new RelativeLayout(parent.getContext());
          CellView cellView = new CellView(parent.getContext(), "image", this.selectionsEngine, this.tableView, recyclerView.firstColumnOnly);
          RelativeLayout.LayoutParams cellLayoutParams = new RelativeLayout.LayoutParams(-1,-1);
          wrapper.addView(cellView, cellLayoutParams);
          rowView.addView(wrapper, layoutParams);
        } else if(column.representation.type.equals("miniChart")) {
          CellView cellView = new CellView(parent.getContext(), "miniChart", this.selectionsEngine, this.tableView, recyclerView.firstColumnOnly);

          rowView.addView(cellView);
        } else {
          CellView cellView = new CellView(parent.getContext(), "text", this.selectionsEngine, this.tableView, recyclerView.firstColumnOnly);
          ClickableTextView textView = (ClickableTextView) cellView.content;

          textView.setMaxLines(NUM_LINES);
          textView.setEllipsize(TextUtils.TruncateAt.END);
          textView.setTextSize(FONT_SIZE);
          textView.setLayoutParams(layoutParams);

          rowView.addView(cellView);
        }
      }
      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, TableTheme.rowHeight);
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
    fetchImages();
    if(viewHolder instanceof RowViewHolder) {
      RowViewHolder holder = (RowViewHolder) viewHolder;
      if (this.isDataView) {
        int color = position % 2 == 0 ? Color.WHITE : 0xFFF7F7F7;
        holder.setBackGroundColor(color);
      }
      holder.setData(rows.get(position));
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

  public int getItemViewType(int position) {
    return this.rows.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  public boolean needsMore() {
    if (this.rows != null && this.dataSize != null) {
      return this.rows.size() < this.dataSize.qcy;
    }
    return false;
  }

  public void updateRepresentation() {
    for(RecyclerView.ViewHolder holder : cachedViewHolders) {
      RowViewHolder viewHolder = (RowViewHolder) holder;
      viewHolder.updateColumnRepresentation();
    }
  }

  public boolean updateWidth(float deltaWidth, int column) {
    for(RecyclerView.ViewHolder holder : cachedViewHolders) {
      RowViewHolder viewHolder = (RowViewHolder) holder;
      if (!viewHolder.updateWidth(deltaWidth, column)) {
        return false;
      }
    }

    int next = column + 1;
    if (next < dataColumns.size()) {
      dataColumns.get(next).width -= (int)deltaWidth;
    }

    dataColumns.get(column).width += (int)deltaWidth;

    columnWidths.updateWidths();
    return true;
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
  }

  public void invalidateLayout() {
    for(DataColumn column : dataColumns) {
      for (RecyclerView.ViewHolder holder : cachedViewHolders) {
        RowViewHolder viewHolder = (RowViewHolder) holder;
        viewHolder.setWidth(column.width, column.dataColIdx);
      }
    }
    this.notifyDataSetChanged();
  }

}
