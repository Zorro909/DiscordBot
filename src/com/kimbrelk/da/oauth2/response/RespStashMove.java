package com.kimbrelk.da.oauth2.response;

import com.kimbrelk.da.oauth2.struct.StashMetadata;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class RespStashMove extends Response {
	private StashMetadata[] mChanges;
	private StashMetadata mTarget;
	
	public RespStashMove(JSONObject json) throws JSONException {
		super();
		JSONArray jsonArr = json.getJSONArray("changes");
		mChanges = new StashMetadata[jsonArr.length()];
		for(int a=0; a<mChanges.length; a++) {
			mChanges[a] = new StashMetadata(jsonArr.getJSONObject(a));
		}
		mTarget = new StashMetadata(json.getJSONObject("target"));
	}

	public final StashMetadata[] getChanges() {
		return mChanges;
	}
	public final StashMetadata getTarget() {
		return mTarget;
	}
}