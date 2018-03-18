package com.kimbrelk.da.oauth2.response;

import com.kimbrelk.da.oauth2.struct.CollectionFolder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class RespCollectionsFolders extends RespPaginationOffset<CollectionFolder> {
	public RespCollectionsFolders(JSONObject json) throws JSONException {
		super(json, "results");
		
	}
	protected final void getResultsFromJsonArray(JSONArray json) throws JSONException {
		mResults = new CollectionFolder[json.length()];
		for(int a=0; a<mResults.length; a++) {
			mResults[a] = new CollectionFolder(json.getJSONObject(a));
		}
	}
}