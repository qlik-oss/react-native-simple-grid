package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

public class ClickableTextView extends androidx.appcompat.widget.AppCompatTextView implements View.OnClickListener, SelectionsObserver {
  DataCell cell = null;
  boolean selected = false;
  int defaultTextColor = Color.BLACK;
  final SelectionsEngine selectionsEngine;
  ClickableTextView(Context context, SelectionsEngine selectionsEngine) {
    super(context);

    this.selectionsEngine = selectionsEngine;
    defaultTextColor = getCurrentTextColor();
    setOnClickListener(this);
  }

  public void setData(DataCell cell) {
    this.cell = cell;
    // check to see if I'm here
    if (cell.isDim) {
      selectionsEngine.observe(this);
      selected = selectionsEngine.contains(cell);
      updateBackgroundColor();
    }
  }

  @Override
  public void onClick(View view) {
    if (cell.isDim) {
      String selection = SelectionsEngine.getSignatureFrom(cell);
      selectionsEngine.selectionsChanged(selection);
    }

  }

  public void onSelectionsChanged(String s) {
    String received = SelectionsEngine.getKeyFrom(s);
    String me = SelectionsEngine.getKeyFrom(cell);
    if(received.equalsIgnoreCase(me)) {
      selected = !selected;
      updateBackgroundColor();
    }
  }

  public  void onClear() {
    selected = false;
    updateBackgroundColor();
  }

  private void updateBackgroundColor() {
    int color = selected ? TableTheme.selectedBackground : Color.TRANSPARENT;
    int textColor = selected ? Color.WHITE : defaultTextColor;
    setBackgroundColor(color);
    setTextColor(textColor);
  }

  public void onRecycled() {
    if (cell.isDim) {
      selectionsEngine.remove(this);
    }
  }

}
