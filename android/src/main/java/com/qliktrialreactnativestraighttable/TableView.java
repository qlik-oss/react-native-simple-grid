package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.ArrayList;
import java.util.List;

public class TableView extends FrameLayout {
  LinearLayout rootView;
  CustomHorizontalScrollView scrollView;
  HeaderView headerView = null;
  CustomRecyclerView recyclerView = null;
  DataProvider dataProvider = new DataProvider();
  List<GrabberView> grabbers = null;
  Path path = null;
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  TableView(Context context, CustomHorizontalScrollView scrollView) {
    super(context);
    this.scrollView = scrollView;
    this.rootView = new LinearLayout(context);
    rootView.setOrientation(LinearLayout.VERTICAL);
    decorate();
    this.addView(rootView);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public void decorate() {
    GradientDrawable drawable = new GradientDrawable();
    GradientDrawable border = new GradientDrawable();
    border.setStroke((int)PixelUtils.dpToPx(1), TableTheme.borderBackgroundColor);
    border.setCornerRadius((int)PixelUtils.dpToPx(TableTheme.borderRadius));
    drawable.setCornerRadius((int)PixelUtils.dpToPx(TableTheme.borderRadius));
    this.setClipToOutline(true);
    this.setBackground(drawable);
    this.setForeground(border);
  }

  public void setHeaderView(HeaderView view) {
    this.headerView = view;
    rootView.addView(this.headerView, 0);
  }

  public void updateTheme() {
    if (headerView != null) {
      headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    }
  }

  public void setDataColumns(List<DataColumn> cols) {
    dataProvider.setDataColumns(cols);
    if (dataProvider.ready()) {
      createRecyclerView();
    }
  }

  public void setRows(List<DataRow> rows, boolean resetData) {
    dataProvider.setRows(rows, resetData);
    if (this.headerView != null && dataProvider.ready()) {
      createRecyclerView();
    }
  }

  public void setDataSize(DataSize dataSize) {
    dataProvider.setDataSize(dataSize);
    if (this.headerView != null && dataProvider.ready()) {
      createRecyclerView();
    }
  }

  void createRecyclerView() {
    if (recyclerView == null) {
      createGrabbers();

      LinearLayoutManager linearLayout = new LinearLayoutManager(this.getContext());
      recyclerView = new CustomRecyclerView(this.getContext());
      recyclerView.setLayoutManager(linearLayout);
      recyclerView.setAdapter(dataProvider);
      recyclerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

      DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
      recyclerView.addItemDecoration(itemDecorator);
      recyclerView.setHasFixedSize(true);
      recyclerView.addOnScrollListener( new OnScrollListener(linearLayout) );
      rootView.addView(recyclerView, 1);

      for(GrabberView view : grabbers) {
        view.setDataProvider(dataProvider);
        view.setHeaderView(headerView);
        view.setGrabbers(grabbers);
      }
    }
  }

  private void createGrabbers() {
    grabbers = new ArrayList<>();
    List<DataColumn> dataColumns = dataProvider.getDataColumns();
    int dragWidth = (int) PixelUtils.dpToPx(40);
    int offset = dragWidth / 2;
    int startOffset = 0;
    for(int i = 0; i < dataColumns.size(); i++) {
      GrabberView grabberView = new GrabberView(i, getContext(), scrollView);
      LayoutParams layoutParams = new LayoutParams(dragWidth, ViewGroup.LayoutParams.MATCH_PARENT);
      startOffset += (dataColumns.get(i).width) - offset;
      offset = 0;

      grabberView.setLayoutParams(layoutParams);
      grabberView.setBackgroundColor(Color.TRANSPARENT);
      grabberView.setTranslationX(startOffset);
      grabbers.add(grabberView);
      this.addView(grabberView);
    }
  }

  class OnScrollListener extends RecyclerView.OnScrollListener {
    LinearLayoutManager linearLayoutManager;
    public OnScrollListener(LinearLayoutManager layoutManager) {
      linearLayoutManager = layoutManager;
    }
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
      if(linearLayoutManager.findLastCompletelyVisibleItemPosition() >= dataProvider.getItemCount() - 50
        && !dataProvider.isLoading()
        && dataProvider.needsMore()) {
        // start the fetch
        dataProvider.setLoading(true);
        WritableMap event = Arguments.createMap();
        ReactContext context = (ReactContext) scrollView.getContext();
        context.getJSModule(RCTEventEmitter.class).receiveEvent(scrollView.getId(), "onEndReached", event);
      }
    }
  }
}
