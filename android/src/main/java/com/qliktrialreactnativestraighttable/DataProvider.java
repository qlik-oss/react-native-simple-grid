package com.qliktrialreactnativestraighttable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

  Map<String, ImageView.ScaleType> AlignmentLookup = Map.of(
    "topCenter", ImageView.ScaleType.FIT_START,
    "bottomCenter", ImageView.ScaleType.FIT_END,
    "centerCenter", ImageView.ScaleType.CENTER_INSIDE
  );

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

  public class RowViewHolder extends RecyclerView.ViewHolder  {

    private final LinearLayout row;
    public RowViewHolder(View view) {
      super(view);
      row = (LinearLayout) view;
    }

    public void setBackGroundColor(int color) {
      row.setBackgroundColor(color);
    }
    public void setData(DataRow dataRow) {
      for(int i = 0; i < dataRow.cells.size(); i++) {
        DataCell cell = dataRow.cells.get(i);
        int columnIndex = cell.colIdx;
        DataColumn column = dataColumns.get(columnIndex);

        if(column.type.equals("image")) {
          ImageView imageView = (ImageView) row.getChildAt(columnIndex);

          Bitmap imageBitmap = getImageData(column.imageUrl);
          imageView.setImageBitmap(imageBitmap);
          LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(column.width, TableTheme.rowHeight);
          imageView.setMaxHeight(TableTheme.rowHeight);
          imageView.setAdjustViewBounds(true);
          imageView.setLayoutParams(layoutParams);
          imageView.setScaleType(AlignmentLookup.get(column.imagePosition));
        } else {
          ClickableTextView view = (ClickableTextView) row.getChildAt(columnIndex);
          view.setData(cell);
          view.setText(cell.qText);
          view.setGravity(cell.textGravity | Gravity.CENTER_VERTICAL);
        }
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
        DataColumn dataColumn = dataColumns.get(i);
        int width = dataColumn.width;
        int padding = (int)PixelUtils.dpToPx(16);

        if (dataColumn.type.equals("image")) {
          ImageView imageView = new ImageView((parent.getContext()));
          rowView.addView(imageView);
          imageView.setPadding(padding, 0, padding, 0);
        } else {
          ClickableTextView view = new ClickableTextView(parent.getContext(), this.selectionsEngine, this.scrollView);
          view.setMaxLines(NUM_LINES);
          view.setEllipsize(TextUtils.TruncateAt.END);
          LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, TableTheme.rowHeight);
          view.setLayoutParams(layoutParams);
          view.setPadding(padding, 0, (int) padding, 0);
          view.setTextSize(FONT_SIZE);
          rowView.addView(view);
        }
      }
      RowViewHolder RowViewHolder = new RowViewHolder(rowView);
      viewHolder = RowViewHolder;
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
