package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ViewConstructor")
public class CustomRecyclerView extends RecyclerView {
  final LinearLayoutManager linearLayout;
  final DataProvider dataProvider;
  final CustomHorizontalScrollView scrollView;
  public boolean firstColumnOnly = false;
  public boolean active = false;
  public CustomRecyclerView scrollCoupledView = null;

  public CustomRecyclerView(Context context, boolean onlyFirstColumn, DataProvider dp, CustomHorizontalScrollView sv, LinearLayoutManager ll) {
    super(context);
    firstColumnOnly = onlyFirstColumn;
    dataProvider = dp;
    scrollView = sv;
    linearLayout = ll;

    DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
    OnScrollListener sharedScrollListener = new OnScrollListener(linearLayout);

    this.setLayoutManager(linearLayout);
    this.setAdapter(dataProvider);
    this.addItemDecoration(itemDecorator);
    this.setHasFixedSize(true);
    this.setBackgroundColor(Color.WHITE);
    this.addOnScrollListener(sharedScrollListener);
    if(onlyFirstColumn) {
      return;
    }
    this.setVerticalScrollBarEnabled(true);
    this.setScrollbarFadingEnabled(true);
    this.setVerticalScrollbarThumbDrawable(new ScrollBarDrawable());
  }

  public void setViewToScrollCouple(CustomRecyclerView viewToScroll) {
    scrollCoupledView = viewToScroll;
  }

  @Override
  public void requestLayout() {
    super.requestLayout();
    post(measureAndLayout);
  }

  class OnScrollListener extends RecyclerView.OnScrollListener {
    LinearLayoutManager linearLayoutManager;
    public OnScrollListener(LinearLayoutManager layoutManager) {
      linearLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
      super.onScrolled(rv, dx, dy);
      if(active && scrollCoupledView != null) {
        scrollCoupledView.scrollBy(dx, dy);
      }

      if(linearLayoutManager.findLastCompletelyVisibleItemPosition() >= dataProvider.getItemCount() - 50
        && !dataProvider.isLoading()
        && dataProvider.needsMore()) {
        // start the fetch
        dataProvider.setLoading(true);
        EventUtils.sendEventToJSFromView(scrollView, "onEndReached");
      }
    }
  }

  @Override
  public void onScrollStateChanged(int state) {
    super.onScrollStateChanged(state);

    active = true;
    if(state == SCROLL_STATE_IDLE) {
      active = false;
    }

  }

  private final Runnable measureAndLayout = () -> {
    measure(
      MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
    layout(getLeft(), getTop(), getRight(), getBottom());
  };
}
