package com.kimbrelk.da.oauth2.response;

import com.kimbrelk.da.oauth2.struct.GalleryFolder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class RespGalleryFolders extends RespPaginationOffset<GalleryFolder> {
	public RespGalleryFolders(JSONObject json) throws JSONException {
		super(json, "results");
	}
	protected final void getResultsFromJsonArray(JSONArray json) throws JSONException {
		mResults = new GalleryFolder[json.length()];
		for(int a=0; a<mResults.length; a++) {
			mResults[a] = new GalleryFolder(json.getJSONObject(a));
		}
	}
	public final GalleryFolder[] getFolders() {
		return mResults;
	}
}