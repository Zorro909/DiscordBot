package com.kimbrelk.da.oauth2.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kimbrelk.da.oauth2.struct.Watcher;

public class RespUserWatchers extends RespPaginationOffset<Watcher> {
	public RespUserWatchers(JSONObject json) throws JSONException {
		super(json, "results");
	}

	@Override
	protected final void getResultsFromJsonArray(JSONArray json) throws JSONException {
		mResults = new Watcher[json.length()];
		for(int a=0; a<mResults.length; a++) {
			mResults[a] = new Watcher(json.getJSONObject(a));
		}
	}
}