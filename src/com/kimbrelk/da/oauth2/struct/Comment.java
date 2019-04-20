package com.kimbrelk.da.oauth2.struct;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.kimbrelk.da.util.Util;

public final class Comment {
	public enum HiddenReason {
		HIDDEN_BY_ADMIN,
		HIDDEN_BY_COMMENTER,
		HIDDEN_BY_OWNER,
		HIDDEN_AS_SPAM
	}
	
	private String mBody;
	private HiddenReason mHiddenReason;
	private String mId;
	private String mParentId;
	private int mReplies;
	private Date mTimePosted;
	private User mUser;
	
	public Comment(JSONObject json) throws JSONException {
		mBody = json.getString("body");
		String hidden = json.optString("hidden");
		if (hidden != null) {
			mHiddenReason = HiddenReason.valueOf(hidden.toUpperCase());
		}
		mId = json.getString("commentid");
		mParentId = json.optString("parentid");
		if (mParentId.equals("")) {
			mParentId = null;
		}
		mReplies = json.getInt("replies");
		mTimePosted = Util.stringToDate(json.getString("posted"));
		mUser = new User(json.getJSONObject("user"));
	}
	
	public final String getBody() {
		return mBody;
	}
	public final HiddenReason getHiddenReason() {
		return mHiddenReason;
	}
	public final Date getTimePosted() {
		return mTimePosted;
	}
	public final String getId() {
		return mId;
	}
	public final String getParentId() {
		return mParentId;
	}
	public final int getNumReplies() {
		return mReplies;
	}
	public final User getUser() {
		return mUser;
	}

	public final boolean isHidden() {
		return mHiddenReason != null;
	}
}