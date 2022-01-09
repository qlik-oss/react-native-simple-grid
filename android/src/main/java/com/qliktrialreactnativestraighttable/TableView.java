package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.uimanager.events.RCTModernEventEmitter;

import java.util.List;

public class TableView extends FrameLayout {
  LinearLayout rootView;
  CustomHorizontalScrollView scrollView;
  LinearLayout headerView = null;
  RecyclerView recyclerView = null;
  DataProvider dataProvider = new DataProvider();
  boolean isLoading = false;
  TableView(Context context, CustomHorizontalScrollView scrollView) {
    super(context);
    this.scrollView = scrollView;
    this.rootView = new LinearLayout(context);
    rootView.setOrientation(LinearLayout.VERTICAL);
    this.addView(rootView);
  }

  public void setHeaderView(LinearLayout view) {
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

  public void setRows(List<DataRow> rows) {
    dataProvider.setRows(rows);
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
      //createGrabbers();

      LinearLayoutManager linearLayout = new LinearLayoutManager(this.getContext());
      recyclerView = new RecyclerView(this.getContext());
      recyclerView.setLayoutManager(linearLayout);
      recyclerView.setAdapter(dataProvider);
      recyclerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

      DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
      recyclerView.addItemDecoration(itemDecorator);
      recyclerView.addOnScrollListener( new OnScrollListener(linearLayout) );
      rootView.addView(recyclerView, 1);
    }
  }

  private void createGrabbers() {
    GrabberView grabberView = new GrabberView(getContext(), scrollView);
    int dragWidth = (int) PixelUtils.dpToPx(33);
    LayoutParams layoutParams = new LayoutParams(dragWidth, ViewGroup.LayoutParams.MATCH_PARENT);
    int startOffsetX = (int) PixelUtils.dpToPx(150 - 33);
    grabberView.setLayoutParams( layoutParams);
    grabberView.setBackgroundColor(Color.TRANSPARENT);
    grabberView.setTranslationX(startOffsetX);
    this.addView(grabberView);
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
        Log.d("rn-table", "Fetching data here");
        dataProvider.setLoading(true);
        WritableMap event = Arguments.createMap();
        ReactContext context = (ReactContext) scrollView.getContext();
        context.getJSModule(RCTEventEmitter.class).receiveEvent(scrollView.getId(), "onEndReached", event);
      }
    }
  }
}
