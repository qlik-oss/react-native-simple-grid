package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

public class ReactNativeStraightTableViewManager extends SimpleViewManager<View> {
    public static final String REACT_CLASS = "ReactNativeStraightTableView";

    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    public static float dpToPx(float dp) {
      return  (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @SuppressLint("NewApi")
    @Override
    @NonNull
    public View createViewInstance(ThemedReactContext reactContext) {
      LinearLayout linearLayout =  new LinearLayout(reactContext);
      linearLayout.setBackgroundColor(Color.GREEN);
      linearLayout.setClipChildren(true);
      linearLayout.setClipToOutline(true);
      return linearLayout;
    }

    @ReactProp(name = "borderRadius")
    public void setBorderRadius(View view, float val) {

      Drawable drawable = view.getBackground();
      PaintDrawable paintDrawable = new PaintDrawable();
      if(drawable instanceof ColorDrawable) {
        ColorDrawable colorDrawable = (ColorDrawable) drawable;
        paintDrawable.setColorFilter(colorDrawable.getColor(), PorterDuff.Mode.SRC_ATOP);
      }
      paintDrawable.setCornerRadius(dpToPx(val));
      view.setBackground(paintDrawable);
    }

    @ReactProp(name = "cols")
    public void setCols(View view,  @Nullable ReadableArray columns) {
      HeaderViewFactory headerViewFactory = new HeaderViewFactory(columns, view.getContext());
      ViewGroup vg = (ViewGroup) view;
      View headerView = headerViewFactory.getHeaderView();
      headerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
      vg.addView(headerView);
    }
}
