package com.kimbrelk.da.oauth2.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kimbrelk.da.oauth2.struct.FeedNotification;

public final class RespFeedNotifications extends RespPaginationCursor<FeedNotification> {
	public RespFeedNotifications(JSONObject json) throws JSONException {
		super(json);
	}
	
	@Override
	protected final void getResultsFromJsonArray(JSONArray json) throws JSONException {
		mResults = new FeedNotification[json.length()];
		for(int a=0; a<mResults.length; a++) {
			mResults[a] = new FeedNotification(json.getJSONObject(a));
		}
	}
}