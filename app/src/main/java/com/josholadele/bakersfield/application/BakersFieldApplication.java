package com.josholadele.bakersfield.application;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Oladele on 6/11/17.
 */
public class BakersFieldApplication extends Application {

    public static final String TAG = BakersFieldApplication.class.getSimpleName();
    private static BakersFieldApplication bakersFieldApplication;


    public static synchronized BakersFieldApplication getInstance() {
        return bakersFieldApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bakersFieldApplication = this;
//        new FetchRecipeData(this, null).execute();
    }

    private RequestQueue mRequestQueue;

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
