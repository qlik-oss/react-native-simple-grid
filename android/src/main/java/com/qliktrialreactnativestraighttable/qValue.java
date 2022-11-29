package com.qliktrialreactnativestraighttable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class qValue {
  String qNum;
  String qText;

  public qValue(HashMap<String,String> source) {
    qNum = source.get("qNum");
    qText = source.get("qText");
  }

  public JSONObject toEvent() throws JSONException {
    JSONObject qValue = new JSONObject();

    qValue.put("qText", qText != null ? qText : "");
    qValue.put("qNum", qNum != null ? qNum : "");

    return qValue;
  }
}
