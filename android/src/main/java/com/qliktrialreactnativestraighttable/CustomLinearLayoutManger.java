package com.qliktrialreactnativestraighttable;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLinearLayoutManger extends LinearLayoutManager {
  boolean initialized = false;
  CustomRecyclerView recyclerView = null;
  CustomLinearLayoutManger(Context context) {
    super(context);
  }

  @Override
  public void onLayoutCompleted(RecyclerView.State state) {
    super.onLayoutCompleted(state);
    if(!initialized && recyclerView != null) {
      if(recyclerView.testTextWrap()) {
        recyclerView = null;
        initialized = true;
      }
    }
  }
}
