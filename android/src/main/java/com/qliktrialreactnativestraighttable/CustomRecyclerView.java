package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ViewConstructor")
public class CustomRecyclerView extends RecyclerView {
  final LinearLayoutManager linearLayout;
  final DataProvider dataProvider;
  final TableView tableView;
  final DragBox dragBox;
  RowCountView rowCountView;
  public boolean firstColumnOnly;
  public boolean active = false;
  public CustomRecyclerView scrollCoupledView = null;
  public MockVerticalScrollView verticalScrollBar = null;
  Paint paint = new Paint();

  public CustomRecyclerView(Context context, boolean onlyFirstColumn, DataProvider dp, TableView tv, LinearLayoutManager ll, DragBox db, DragBox firstColumnDb) {
    super(context);
    firstColumnOnly = onlyFirstColumn;
    dataProvider = dp;
    tableView = tv;
    linearLayout = ll;
    dragBox = db;

    DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
    OnScrollListener sharedScrollListener = new OnScrollListener(linearLayout);

    this.setLayoutManager(linearLayout);
    this.addItemDecoration(itemDecorator);
    this.setHasFixedSize(true);
    this.setBackgroundColor(Color.WHITE);
    this.addOnScrollListener(sharedScrollListener);
    if (onlyFirstColumn) {
      return;
    }
    this.setVerticalScrollBarEnabled(false);

    dragBox.setScrollListener(this);
    firstColumnDb.setScrollListener(this);
  }

  public void setScrollbar(MockVerticalScrollView verticalScrollBar){
    this.verticalScrollBar = verticalScrollBar;
  }

  @Override
  public void onDraw(Canvas c) {
    super.onDraw(c);
    paint.setStrokeWidth(PixelUtils.dpToPx(2));
    paint.setColor(TableTheme.borderBackgroundColor);
    c.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), paint);
  }

  public void setViewToScrollCouple(CustomRecyclerView viewToScroll) {
    scrollCoupledView = viewToScroll;
  }

  public void setRowCountView(RowCountView view) {
    this.rowCountView = view;
  }

  @Override
  public void requestLayout() {
    super.requestLayout();
    post(measureAndLayout);
    if(rowCountView == null) {
      return;
    }
    post(() -> {
      if(linearLayout == null || dataProvider == null) {
        return;
      }
      int windowMin = linearLayout.findFirstVisibleItemPosition() + 1;
      int windowMax = linearLayout.findLastVisibleItemPosition() + 1;
      int total = dataProvider.dataSize.qcy;
      rowCountView.update(windowMin, windowMax, total);
    });
  }

  class OnScrollListener extends RecyclerView.OnScrollListener {
    LinearLayoutManager linearLayoutManager;

    public OnScrollListener(LinearLayoutManager layoutManager) {
      linearLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
      super.onScrolled(rv, dx, dy);
      if (active && scrollCoupledView != null) {
        scrollCoupledView.scrollBy(dx, dy);
      }
      if (verticalScrollBar != null) {
        verticalScrollBar.setContentHeight(rv.computeVerticalScrollRange());
        verticalScrollBar.setScrollY(rv.computeVerticalScrollOffset());
      }

      if (linearLayoutManager.findLastCompletelyVisibleItemPosition() >= dataProvider.getItemCount() - 50
        && !dataProvider.isLoading()
        && dataProvider.needsMore()) {
        // start the fetch
        dataProvider.setLoading(true);
        EventUtils.sendEventToJSFromView(tableView, "onEndReached");
      }
    }
  }

  @Override
  public void onScrollStateChanged(int state) {
    super.onScrollStateChanged(state);

    active = state != SCROLL_STATE_IDLE;
  }

  private final Runnable measureAndLayout = () -> {
    measure(
      MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
    layout(getLeft(), getTop(), getRight(), getBottom());
  };

  public void updateLineHeight(DataColumn column) {
    int maxLineCount = 0;
    int childCount = getChildCount();

    for (int i = 0; i < childCount; i++) {
      View view = getChildAt(i);
      RowViewHolder viewHolder = (RowViewHolder) getChildViewHolder(view);
      if (viewHolder != null) {
        int lineCount = viewHolder.getLineCount(column);
        maxLineCount = Math.max(lineCount, maxLineCount);
      }
    }

    int rowHeight = (maxLineCount * tableView.cellContentStyle.lineHeight) + CellView.PADDING_X_2;
    tableView.rowHeight = Math.max(tableView.cellContentStyle.themedRowHeight, rowHeight);
    for (int i = 0; i < childCount; i++) {
      View view = getChildAt(i);
      RowViewHolder viewHolder = (RowViewHolder) getChildViewHolder(view);
      viewHolder.updateHeight(tableView.rowHeight);
    }

    dataProvider.updateRowHeight(tableView.rowHeight);

  }

  public boolean testTextWrap( boolean recursive ) {
    if(!tableView.cellContentStyle.wrap) {
      // don't test but tell whoever is calling
      // that it's done testing
      return true;
    }
    int childCount = getChildCount();
    if (childCount == 0) {
      return false;
    }
    int maxLines = 0;
    for (int i = 0; i < childCount; i++) {
      View view = getChildAt(i);
      RowViewHolder viewHolder = (RowViewHolder) getChildViewHolder(view);
      int lines = viewHolder.initialMeasure();
      maxLines = Math.max(lines, maxLines);
    }
    if (!firstColumnOnly) {
      int rowHeight =  (maxLines * tableView.cellContentStyle.lineHeight) + CellView.PADDING_X_2;
      tableView.rowHeight = Math.max(rowHeight, tableView.cellContentStyle.themedRowHeight);
    }

    tableView.post(new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < childCount; i++) {
          View view = getChildAt(i);
          RowViewHolder viewHolder = (RowViewHolder) getChildViewHolder(view);
          viewHolder.initializeHeight(tableView.rowHeight, tableView.cellContentStyle);
        }
        if (tableView.firstColumnView != null) {
          tableView.firstColumnView.requestLayout();
        }
        if(tableView.rootLayout != null) {
          tableView.rootLayout.requestLayout();
        }
        requestLayout();
        if(recursive) {
          dataProvider.notifyDataSetChanged();
        }
      }
    });

    return true;
  }
}
