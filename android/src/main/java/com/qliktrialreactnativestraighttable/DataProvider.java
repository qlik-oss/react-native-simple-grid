package com.qliktrialreactnativestraighttable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataProvider extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private final int NUM_LINES = 1;
  private final int FONT_SIZE = 14;
  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;
  boolean isDataView = false;
  List<DataRow> rows = null;
  List<DataColumn> dataColumns = null;
  Map<String, Bitmap> imageData = new HashMap<>();
  Set<RowViewHolder> cachedViewHolders = new HashSet<>();
  SelectionsEngine selectionsEngine = null;
  DataSize dataSize = null;
  boolean loading = false;
  CustomHorizontalScrollView scrollView;
  public final float minWidth = PixelUtils.dpToPx(40);

  public boolean isLoading() {
    return loading;
  }

  public void setLoading(boolean loading) {
    this.loading = loading;
  }

  public Bitmap getImageData(String url) {
    return imageData.get(url);
  }

  public List<DataColumn> getDataColumns() {
    return dataColumns;
  }

  public void processColumns(List<DataColumn> columns) {
    final CountDownLatch latch = new CountDownLatch(columns.size());
    for( int i = 0; i < columns.size(); i++) {
      DataColumn column = columns.get(i);

      if(column.type.equals("image")) {
        boolean isDuplicateImageUrl = imageData.containsKey(column.imageUrl);
        if(isDuplicateImageUrl || !URLUtil.isValidUrl(column.imageUrl)) {
          latch.countDown();
          continue;
        }
        imageData.put(column.imageUrl, null);
        try {
          HttpUtils.get(column.imageUrl, new Callback() {
              public void onResponse(Call call, Response response) {
                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = PixelUtils.byteStreamToBitmap(inputStream);
                imageData.replace(column.imageUrl, bitmap);
                latch.countDown();
              }

              public void onFailure(Call call, IOException e) {
                latch.countDown();
              }
          });
        } catch(Exception e) {
          latch.countDown();
          imageData.remove(column.imageUrl);
          e.printStackTrace();
        }
      } else {
        latch.countDown();
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
    RecyclerView.ViewHolder viewHolder;
    if (viewType == VIEW_TYPE_ITEM) {
      LinearLayout rowView = new LinearLayout(parent.getContext());
      int padding = (int)PixelUtils.dpToPx(16);
      rowView.setOrientation(LinearLayout.HORIZONTAL);

      for (int i = 0; i < dataColumns.size(); i++) {
        DataColumn column = dataColumns.get(i);
        int width = column.width;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, TableTheme.rowHeight);

        if (column.type.equals("image")) {
          RelativeLayout wrapper = new RelativeLayout(parent.getContext());
          wrapper.setLayoutParams(layoutParams);

          RelativeLayout container = new RelativeLayout(parent.getContext());
          container.setPadding(padding, 0, (int) padding, 0);

          ImageView imageView = new ClickableImageView(parent.getContext(), this.selectionsEngine, this.scrollView);
          container.addView(imageView);
          wrapper.addView(container);
          rowView.addView(wrapper);
        } else {
          ClickableTextView view = new ClickableTextView(parent.getContext(), this.selectionsEngine, this.scrollView);
          view.setMaxLines(NUM_LINES);
          view.setEllipsize(TextUtils.TruncateAt.END);
          view.setLayoutParams(layoutParams);
          view.setPadding(padding, 0, (int) padding, 0);
          view.setTextSize(FONT_SIZE);
          rowView.addView(view);
        }
      }
      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, TableTheme.rowHeight);
      rowView.setLayoutParams(layoutParams);
      RowViewHolder rowViewHolder = new RowViewHolder(rowView, this);
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
    processColumns(cols);
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
    return this.rows != null && dataSize != null && this.dataColumns != null;
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

  public void onEndPan(CustomHorizontalScrollView contextView) {
    EventUtils.sendOnColumnResize(contextView, dataColumns);
  }

}
