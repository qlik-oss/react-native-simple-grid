package com.qliktrialreactnativestraighttable;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

public class RowViewHolder extends RecyclerView.ViewHolder  {
  private int startIndex, numColumns;
  private final LinearLayout row;
  private final DataProvider dataProvider;

  public RowViewHolder(View view, DataProvider dp, boolean firstColumnOnly) {
    super(view);
    row = (LinearLayout) view;
    dataProvider = dp;
    numColumns = dataProvider.dataColumns.size();
    if(firstColumnOnly) {
      numColumns = 1;
    }
  }

  public void setBackGroundColor(int color) {
    row.setBackgroundColor(color);
  }
  public void setData(DataRow dataRow) {
    for(int i = 0; i < numColumns; i++) {
      DataCell cell = dataRow.cells.get(i);
      int columnIndex = cell.colIdx;
      DataColumn column = dataProvider.dataColumns.get(columnIndex);

      if(column.representation.type.equals("image")) {
        ViewGroup wrapper = (ViewGroup) row.getChildAt(columnIndex);
        CellView cellView = (CellView) wrapper.getChildAt(0);
        ViewGroup.LayoutParams layout = cellView.getLayoutParams();
        layout.height = TableTheme.rowHeight;
        layout.width = (int)column.width;
        cellView.setLayoutParams(layout);
        cellView.setData(cell);

        Bitmap imageBitmap = dataProvider.getImageData(cell.imageUrl);
        if(imageBitmap == null) {
          continue;
        }
        ClickableImageView imageView = (ClickableImageView) cellView.content;
        imageView.setImageBitmap(imageBitmap);
        imageView.setSizing(column, imageBitmap);
        imageView.setAlignment(column);
      } else if(column.representation.type.equals("miniChart")) {
        ViewGroup wrapper = (ViewGroup) row.getChildAt(columnIndex);
        MiniChartView miniChartView = (MiniChartView) wrapper.getChildAt(0);
        miniChartView.setData(cell, column);
      } else {
        CellView cellView = (CellView) row.getChildAt(columnIndex);
        ClickableTextView textView = (ClickableTextView) cellView.content;

        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(textViewLayoutParams);
        LinearLayout.LayoutParams cellViewLayoutParams = new LinearLayout.LayoutParams((int)column.width, TableTheme.rowHeight);
        cellView.setLayoutParams(cellViewLayoutParams);
        if(column.representation.type.equals("text")) {
          cell.indicator = null;
        }
        cellView.setData(cell);

        textView.setGravity(cell.textGravity | Gravity.CENTER_VERTICAL);
      }
    }
  }

  public void updateColumnRepresentation() {
    for(int i = 0; i < this.row.getChildCount(); i++) {
      DataColumn column = dataProvider.dataColumns.get(i);

      if(column.representation.type.equals("image")) {
        RelativeLayout wrapper = (RelativeLayout) row.getChildAt(i);
        RelativeLayout container = (RelativeLayout) wrapper.getChildAt(0);
        ClickableImageView imageView = (ClickableImageView) container.getChildAt(0);
        Bitmap imageBitmap = dataProvider.getImageData(column.representation.imageUrl);
        if(imageBitmap == null) {
          continue;
        }
        imageView.setSizing(column, imageBitmap);
        imageView.setAlignment(column);
      }
    }
  }

  public void onRecycled() {
    for(int i = 0; i <  row.getChildCount(); i++) {
      View view = row.getChildAt(i);
      if(view instanceof RelativeLayout) {
        return;
      }
      SelectionsObserver observerView = (SelectionsObserver) row.getChildAt(i);
      observerView.onRecycled();
    }
  }

  public boolean updateWidth(float deltaWidth, int column) {
    if(column > numColumns - 1) {
      return true;
    }
    View view = row.getChildAt(column);
    int currentWidth = (int)dataProvider.dataColumns.get(column).width;
    float newWidth = currentWidth + deltaWidth;

    if(newWidth < dataProvider.minWidth) {
      return false;
    }

    if (!updateNeighbour(deltaWidth, column)) {
      return false;
    }
    ViewGroup.LayoutParams params = view.getLayoutParams();
    params.width = (int) newWidth;
    view.setLayoutParams(params);

    return true;
  }

  public boolean setWidth(int width, int column) {
    if(column > numColumns - 1) {
      return true;
    }

    View view = row.getChildAt(column);
    ViewGroup.LayoutParams params = view.getLayoutParams();
    params.width = width;
    view.setLayoutParams(params);

    return true;
  }

  private boolean updateNeighbour(float deltaWidth, int column) {
    if (column + 1 < numColumns ) {
      View neighbour =  row.getChildAt(column + 1);
      int currentWidth = (int)dataProvider.dataColumns.get(column + 1).width;
      float newWidth = currentWidth - deltaWidth;
      if (newWidth < dataProvider.minWidth) {
        return false;
      }
      ViewGroup.LayoutParams params = neighbour.getLayoutParams();
      params.width = (int)newWidth;
      neighbour.setLayoutParams(params);
    }
    return true;
  }
}
