package com.kimbrelk.da.oauth2.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kimbrelk.da.oauth2.struct.StashMetadata;

public final class RespStashStackContents extends RespPaginationOffset<StashMetadata> {
	public RespStashStackContents(JSONObject json) throws JSONException {
		super(json, "results");
	}
	protected final void getResultsFromJsonArray(JSONArray json) throws JSONException {
		mResults = new StashMetadata[json.length()];
		for(int a=0; a<mResults.length; a++) {
			mResults[a] = new StashMetadata(json.getJSONObject(a));
		}
	}
}