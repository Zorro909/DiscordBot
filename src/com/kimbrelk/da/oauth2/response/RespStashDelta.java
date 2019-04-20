package com.kimbrelk.da.oauth2.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kimbrelk.da.oauth2.struct.StashDelta;

public final class RespStashDelta extends RespPaginationDelta<StashDelta> {
	public RespStashDelta(JSONObject json) throws JSONException {
		super(json, "entries");
	}
	
	@Override
	protected final void getResultsFromJsonArray(JSONArray json) throws JSONException {
		mResults = new StashDelta[json.length()];
		for(int a=0; a<mResults.length; a++) {
			mResults[a] = new StashDelta(json.getJSONObject(a));
		}
	}
}