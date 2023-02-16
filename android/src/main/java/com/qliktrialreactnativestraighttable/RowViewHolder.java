package com.qliktrialreactnativestraighttable;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.caverock.androidsvg.SVG;

public class RowViewHolder extends RecyclerView.ViewHolder  {
  private int startIndex, numColumns;
  private final LinearLayout row;
  private final DataProvider dataProvider;
  private boolean firstColumnOnly = false;

  public RowViewHolder(View view, DataProvider dp, boolean firstColumnOnly) {
    super(view);
    this.firstColumnOnly = firstColumnOnly;
    row = (LinearLayout) view;
    dataProvider = dp;
  }

  public void setBackGroundColor(int color) {
    row.setBackgroundColor(color);
  }
  public void setData(DataRow dataRow, int rowHeight, CellContentStyle cellContentStyle) {
    numColumns = dataProvider.dataColumns.size();
    if(firstColumnOnly) {
      numColumns = 1;
    }
    int numCells = row.getChildCount();
    int extraCells = row.getChildCount() - numColumns;
    if(extraCells > 0) {
      row.removeViews(numCells - extraCells, extraCells);
    }
    if(dataRow.cells.size() < numColumns) {
      return;
    }
    for(int i = 0; i < numColumns; i++) {
      DataCell cell = dataRow.cells.get(i);
      int columnIndex = cell.rawColIdx;
      DataColumn column = DataProvider.getDataColumnByRawIdx(columnIndex, dataProvider.dataColumns);

      if(columnIndex >= row.getChildCount()) {
        TableView tableView = dataProvider.tableView;
        CellView cellView = new CellView(tableView.getContext(), column.representation.type, tableView.selectionsEngine, tableView, tableView.isFirstColumnFrozen, column);
        row.addView(cellView);
      }

      CellView cellView = (CellView) row.getChildAt(columnIndex);
      if(column.representation.type.equals("image") && !dataProvider.isDataView) {
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(column.width, ViewGroup.LayoutParams.MATCH_PARENT);
        cellView.setLayoutParams(layout);
        cellView.convertCellContentType("image", column);
        cellView.setData(cell, dataRow, column);

        ClickableImageView imageView = (ClickableImageView) cellView.content;
        if(cell.imageUrl != null) {
          Glide.with(cellView.getContext()).asBitmap().listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
              return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

              imageView.scaleAndPositionImage(column, resource);
              return false;
            }
          }).load(cell.imageUrl).into(imageView);
        } else if(cell.qText != null) {
          loadSVG(cell, imageView);
        }
      } else if(column.representation.type.equals("miniChart") && !dataProvider.isDataView) {
        LinearLayout.LayoutParams cellViewLayoutParams = new LinearLayout.LayoutParams(column.width, ViewGroup.LayoutParams.MATCH_PARENT);
        cellView.setLayoutParams(cellViewLayoutParams);
        cellView.setData(cell, dataRow, column);
        cellView.convertCellContentType("miniChart", column);
        MiniChartView miniChartView = (MiniChartView) cellView.content;
        miniChartView.setData(cell, column, cellView);
      } else {
        cellView.convertCellContentType("text", column);
        ClickableTextView textView = (ClickableTextView) cellView.content;
        textView.setIsDataView(dataProvider.isDataView);
        RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(textViewLayoutParams);
        LinearLayout.LayoutParams cellViewLayoutParams = new LinearLayout.LayoutParams(column.width, ViewGroup.LayoutParams.MATCH_PARENT);
        cellViewLayoutParams.gravity = Gravity.TOP;
        cellView.setLayoutParams(cellViewLayoutParams);

        if(column.representation.type.equals("text") || dataProvider.isDataView) {
          cell.indicator = null;
        }

        cellView.setData(cell, dataRow, column);
        if(column.representation.type.equals("url")) {
          setupHyperLink(textView);
        }
        if(column.isDim && cellContentStyle.wrap) {
          textView.setMaxLines(rowHeight/cellContentStyle.lineHeight, column);
        } else {
          textView.setMaxLines(1);
        }
        textView.setGravity(column.textAlignment | Gravity.CENTER_VERTICAL);
      }
    }
  }

  private void loadSVG(DataCell cell, ImageView imageView) {
    String svgTag = "data:image/svg+xml,";
    if(cell.qText.startsWith(svgTag)) {
      try {
        String svgXML = cell.qText.substring(svgTag.length());
        SVG svg = SVG.getFromString(svgXML);
        Drawable drawable = new PictureDrawable(svg.renderToPicture());
        imageView.setImageDrawable(drawable);
      } catch (Exception exception) {
        Log.e("Image", exception.getMessage());
      }
    }
  }

  private void setupHyperLink(ClickableTextView textView) {
    String htmlText = String.format("<a href=\"%s\">%s</a>", textView.linkUrl, textView.linkLabel);
    Spanned result = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY);
    textView.setText(result);
    textView.setMovementMethod(LinkMovementMethod.getInstance());
    textView.setEllipsize(TextUtils.TruncateAt.END);
    textView.setMaxLines(1);
    // this must be set for elipse to show.  Weird but true.
    // https://stackoverflow.com/questions/1141651/how-to-set-a-long-string-in-a-text-view-in-a-single-line-with-horizontal-scroll
    textView.setHorizontallyScrolling(true);
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
    CellView view = (CellView) row.getChildAt(column);
    int currentWidth = dataProvider.dataColumns.get(column).width;
    float newWidth = currentWidth + deltaWidth;

    if(newWidth < DataProvider.minWidth) {
      return false;
    }

    view.updateWidth((int) newWidth);

    DataColumn dataColumn = dataProvider.dataColumns.get(column);
    checkTextWrap(dataColumn);
    return true;
  }

  private void checkTextWrap(DataColumn dataColumn) {
    if(dataColumn.isDim && dataColumn.isText() ) {
      CellView cellView = (CellView) row.getChildAt(dataColumn.columnIndex);
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

  public int getLineCount(DataColumn column) {
    CellView cellView = (CellView) row.getChildAt(column.columnIndex);
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
        CellView cellView = (CellView) row.getChildAt(column.columnIndex);
        if(cellView != null) {
          ClickableTextView clickableTextView = (ClickableTextView) cellView.content;
          maxLines = Math.max(clickableTextView.measureLines(column), maxLines);
        }
      }
    }
    return  maxLines;
  }

  public void initializeHeight(int rowHeight, CellContentStyle cellContentStyle) {
    int lines = rowHeight / cellContentStyle.lineHeight;

    for (DataColumn column: dataProvider.dataColumns) {
      if(column.isText() && column.isDim) {
        if(column.columnIndex < row.getChildCount()) {
          CellView cellView = (CellView) row.getChildAt(column.columnIndex);
          ClickableTextView clickableTextView = (ClickableTextView) cellView.content;
          clickableTextView.setMaxLines(lines, column);
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
