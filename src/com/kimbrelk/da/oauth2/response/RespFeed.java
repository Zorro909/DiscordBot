package com.kimbrelk.da.oauth2.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kimbrelk.da.oauth2.struct.Feed;

public final class RespFeed extends RespPaginationCursor<Feed> {
	public RespFeed(JSONObject json) throws JSONException {
		super(json);
	}
	
	@Override
	protected final void getResultsFromJsonArray(JSONArray json) throws JSONException {
		mResults = new Feed[json.length()];
		for(int a=0; a<mResults.length; a++) {
			mResults[a] = new Feed(json.getJSONObject(a));
		}
	}
}