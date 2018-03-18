package com.kimbrelk.da.oauth2.response;

import com.kimbrelk.da.oauth2.struct.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RespUserStatuses extends RespPaginationOffset<Status> {
	public RespUserStatuses(JSONObject json) throws JSONException {
		super(json, "results");
	}
	
	protected void getResultsFromJsonArray(JSONArray json) throws JSONException {
		mResults = new Status[json.length()];
		for(int a=0; a<mResults.length; a++) {
			mResults[a] = new Status(json.getJSONObject(a));
		}
	}
	
	public final Status[] getStatuses() {
		return mResults;
	}
}