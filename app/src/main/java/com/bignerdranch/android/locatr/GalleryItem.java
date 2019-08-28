package com.bignerdranch.android.locatr;

import android.net.Uri;

/**
 * Created by Mohamed Amr on 5/11/2019.
 */

public class GalleryItem {

    private String mId;
    private String mCaption;
    private String mUrl;
    private String mOwner;
    private double mLon;
    private double mLat;

    @Override
    public String toString() {
        return mCaption;
    }

    public String getId() {
        return mId;
    }

    public String getCaption() {
        return mCaption;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setOwner (String owner){
        mOwner = owner;
    }
    public String getOwner (){
        return mOwner;
    }

    public  Uri  getPhotoPageUri (){
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build();
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public double getLat() {
        return mLat;
    }
}
