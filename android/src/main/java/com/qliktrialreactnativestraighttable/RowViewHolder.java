package com.qliktrialreactnativestraighttable;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

public class RowViewHolder extends RecyclerView.ViewHolder  {

  private final LinearLayout row;
  private DataProvider dataProvider;

  public RowViewHolder(View view, DataProvider dp) {
    super(view);
    row = (LinearLayout) view;
    dataProvider = dp;
  }

  public void setBackGroundColor(int color) {
    row.setBackgroundColor(color);
  }
  public void setData(DataRow dataRow) {
    for(int i = 0; i < dataRow.cells.size(); i++) {
      DataCell cell = dataRow.cells.get(i);
      int columnIndex = cell.colIdx;
      DataColumn column = dataProvider.dataColumns.get(columnIndex);

      if(column.type.equals("image")) {
        RelativeLayout wrapper = (RelativeLayout) row.getChildAt(columnIndex);
        RelativeLayout container = (RelativeLayout) wrapper.getChildAt(0);
        ViewGroup.LayoutParams layout = (ViewGroup.LayoutParams) container.getLayoutParams();
        layout.height = TableTheme.rowHeight;
        layout.width = column.width;
        container.setLayoutParams(layout);
        ClickableImageView imageView = (ClickableImageView) container.getChildAt(0);

        imageView.setData(cell);
        Bitmap imageBitmap = dataProvider.getImageData(column.imageUrl);
        imageView.setImageBitmap(imageBitmap);
        imageView.setSizing(column, imageBitmap);
        imageView.setAlignment(column);
      } else {
        ClickableTextView view = (ClickableTextView) row.getChildAt(columnIndex);
        view.setData(cell);
        view.setText(cell.qText);
        view.setGravity(cell.textGravity | Gravity.CENTER_VERTICAL);
      }
    }
  }

  public void updateColumnRepresentation() {
    for(int i = 0; i < this.row.getChildCount(); i++) {
      DataColumn column = dataProvider.dataColumns.get(i);

      if(column.type.equals("image")) {
        RelativeLayout wrapper = (RelativeLayout) row.getChildAt(i);
        RelativeLayout container = (RelativeLayout) wrapper.getChildAt(0);
        ClickableImageView imageView = (ClickableImageView) container.getChildAt(0);
        Bitmap imageBitmap = dataProvider.getImageData(column.imageUrl);
        imageView.setImageBitmap(imageBitmap);
        imageView.setSizing(column, imageBitmap);
        imageView.setAlignment(column);
      }
    }
  }

  public void onRecycled() {
    for(int i = 0; i <  row.getChildCount(); i++) {
      View view = (View) row.getChildAt(i);
      if(view instanceof RelativeLayout) {
        return;
      }
      SelectionsObserver observerView = (SelectionsObserver) row.getChildAt(i);
      observerView.onRecycled();
    }
  }

  public boolean updateWidth(float width, int column) {
    View view =  row.getChildAt(column);
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();
    float newWidth = params.width + width;
    if(newWidth < dataProvider.minWidth) {
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
    if (column + 1 < dataProvider.dataColumns.size() ) {
      View neighbour =  row.getChildAt(column + 1);
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) neighbour.getLayoutParams();
      float newWidth = params.width - width;
      if (newWidth < dataProvider.minWidth) {
        return false;
      }
      params.width = (int)newWidth;
      neighbour.setLayoutParams(params);
    }
    return true;
  }
}
