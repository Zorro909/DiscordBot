package com.kimbrelk.da.oauth2.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kimbrelk.da.oauth2.struct.Friend;

public final class RespUserFriends extends RespPaginationOffset<Friend> {
	public RespUserFriends(JSONObject json) throws JSONException {
		super(json, "results");
	}
	
	@Override
	protected final void getResultsFromJsonArray(JSONArray json) throws JSONException {
		mResults = new Friend[json.length()];
		for(int a=0; a<mResults.length; a++) {
			mResults[a] = new Friend(json.getJSONObject(a));
		}
	}
}