package com.qliktrialreactnativestraighttable;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.text.HtmlCompat;
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
  public void setData(DataRow dataRow, int rowHeight) {
    for(int i = 0; i < numColumns; i++) {
      DataCell cell = dataRow.cells.get(i);
      int columnIndex = cell.colIdx;
      DataColumn column = dataProvider.dataColumns.get(columnIndex);

      if(column.representation.type.equals("image")) {
        ViewGroup wrapper = (ViewGroup) row.getChildAt(columnIndex);
        CellView cellView = (CellView) wrapper.getChildAt(0);
        ViewGroup.LayoutParams layout = cellView.getLayoutParams();
        layout.height = rowHeight;
        layout.width = column.width;
        cellView.setLayoutParams(layout);
        cellView.setData(cell, dataRow, column);

        Bitmap imageBitmap = dataProvider.getImageData(cell.imageUrl);
        if(imageBitmap == null) {
          continue;
        }
        ClickableImageView imageView = (ClickableImageView) cellView.content;
        imageView.setImageBitmap(imageBitmap);
        imageView.setSizing(column, imageBitmap);
        imageView.setAlignment(column);
      } else if(column.representation.type.equals("miniChart")) {
        CellView cellView = (CellView) row.getChildAt(columnIndex);
        LinearLayout.LayoutParams cellViewLayoutParams = new LinearLayout.LayoutParams(column.width, rowHeight);
        cellView.setLayoutParams(cellViewLayoutParams);

        MiniChartView miniChartView = (MiniChartView) cellView.content;
        miniChartView.setData(cell, column);
      } else {
        CellView cellView = (CellView) row.getChildAt(columnIndex);
        ClickableTextView textView = (ClickableTextView) cellView.content;

        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(textViewLayoutParams);

        LinearLayout.LayoutParams cellViewLayoutParams = new LinearLayout.LayoutParams(column.width, rowHeight);
        cellView.setLayoutParams(cellViewLayoutParams);
        if(column.representation.type.equals("text")) {
          cell.indicator = null;
        }
        cellView.setData(cell, dataRow, column);
        if(column.representation.type.equals("url")) {
          setupHyperLink(textView, column.representation, cell);
        }
        if(column.isDim) {
          textView.setMaxLines(rowHeight/TableTheme.rowHeightFactor);
        } else {
          textView.setMaxLines(1);
        }
        textView.setGravity(column.textAlignment | Gravity.CENTER_VERTICAL);
      }
    }
  }

  private void setupHyperLink(ClickableTextView textView, Representation representation,  DataCell cell) {
    String htmlLabel = representation.urlPosition.equals("dimension") ? representation.linkUrl : cell.qText;
    String htmlText = String.format("<a href=\"%s\">%s</a>",representation.linkUrl, htmlLabel);
    Spanned result = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY);
    textView.setText(result);
    textView.setMovementMethod(LinkMovementMethod.getInstance());
    textView.setEllipsize(TextUtils.TruncateAt.END);
    textView.setMaxLines(1);
    // this must be set for elipse to show.  Weird but true.
    // https://stackoverflow.com/questions/1141651/how-to-set-a-long-string-in-a-text-view-in-a-single-line-with-horizontal-scroll
    textView.setHorizontallyScrolling(true);
  }

  public void updateColumnRepresentation() {
    for(int i = 0; i < this.row.getChildCount(); i++) {
      DataColumn column = dataProvider.dataColumns.get(i);

      if(column.representation.type.equals("image")) {
        RelativeLayout wrapper = (RelativeLayout) row.getChildAt(i);
        CellView cellView = (CellView) wrapper.getChildAt(0);
        ClickableImageView imageView = (ClickableImageView) cellView.content;
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
    int currentWidth = dataProvider.dataColumns.get(column).width;
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

    DataColumn dataColumn = dataProvider.dataColumns.get(column);
    checkTextWrap(dataColumn);
    checkNeighbourTextWrap(column);

    return true;
  }

  private void checkNeighbourTextWrap(int column) {
    if (column + 1 < numColumns ) {
      DataColumn dataColumn = dataProvider.dataColumns.get(column);
      checkTextWrap(dataColumn);
    }
  }

  private void checkTextWrap(DataColumn dataColumn) {
    if(dataColumn.isDim && dataColumn.isText() ) {
      CellView cellView = (CellView) row.getChildAt(dataColumn.dataColIdx);
      ClickableTextView textView = (ClickableTextView)cellView.content;
      textView.testTextWrap(dataColumn);
    }
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
      int currentWidth = dataProvider.dataColumns.get(column + 1).width;
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

  public int getLineCount(DataColumn column) {
    CellView cellView = (CellView) row.getChildAt(column.dataColIdx);
    ClickableTextView textView = (ClickableTextView) cellView.content;
    return textView.getMeasuredLineCount();
  }

  public void updateHeight(int rowHeight) {
    ViewGroup.LayoutParams params = row.getLayoutParams();
    params.height = rowHeight;
    row.setLayoutParams(params);

  }

  public int initialMeasure() {
    int maxLines = 0;
    for (DataColumn column: dataProvider.dataColumns) {
      if(column.isText() && column.isDim) {
        CellView cellView = (CellView) row.getChildAt(column.dataColIdx);
        if(cellView != null) {
          ClickableTextView clickableTextView = (ClickableTextView) cellView.content;
          maxLines = Math.max(clickableTextView.measureLines(column), maxLines);
        }
      }
    }
    return  maxLines;
  }

  public void initializeHeight(int rowHeight) {
    int lines = rowHeight / TableTheme.rowHeightFactor;

    for (DataColumn column: dataProvider.dataColumns) {
      if(column.isText() && column.isDim) {
        if(column.dataColIdx < row.getChildCount()) {
          CellView cellView = (CellView) row.getChildAt(column.dataColIdx);
          ClickableTextView clickableTextView = (ClickableTextView) cellView.content;
          clickableTextView.setMaxLines(lines);
          clickableTextView.setGravity(column.textAlignment | Gravity.CENTER_VERTICAL);
          clickableTextView.requestLayout();
        }
      }
    }

    ViewGroup.LayoutParams params = row.getLayoutParams();
    params.height = rowHeight;
    row.setLayoutParams(params);
    row.requestLayout();

  }
}
