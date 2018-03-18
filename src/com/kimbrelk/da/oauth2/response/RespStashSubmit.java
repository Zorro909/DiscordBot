package com.kimbrelk.da.oauth2.response;

import org.json.JSONException;
import org.json.JSONObject;

public final class RespStashSubmit extends Response {
	private long mItemId;
	private long mStackId;
	private String mStackName;
	
	public RespStashSubmit(JSONObject json) throws JSONException {
		super();
		mStackId = json.getLong("stackid");
		if (json.has("stack")) {
			mStackName = json.getString("stack");
		}
		mItemId = json.getLong("itemid");
	}
	public final long getItemId() {
		return mItemId;
	}
	public final long getStackId() {
		return mStackId;
	}
	public final String getStackName() {
		return mStackName;
	}
}