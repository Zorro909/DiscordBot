package com.kimbrelk.da.oauth2.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kimbrelk.da.oauth2.struct.Deviation;

public class RespDeviationsQuery extends RespPaginationOffset<Deviation> {
	public RespDeviationsQuery(JSONObject json) throws JSONException {
		super(json, "results");
	}
	
	@Override
	protected final void getResultsFromJsonArray(JSONArray json) throws JSONException {
		mResults = new Deviation[json.length()];
		for(int a=0; a<mResults.length; a++) {
			mResults[a] = new Deviation(json.getJSONObject(a));
		}
	}
}