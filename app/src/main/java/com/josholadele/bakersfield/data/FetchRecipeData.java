package com.josholadele.bakersfield.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.josholadele.bakersfield.RecipeListActivity;
import com.josholadele.bakersfield.application.BakersFieldApplication;
import com.josholadele.bakersfield.provider.BakersFieldContract.BakersFieldEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by Oladele on 6/11/17.
 */

public class FetchRecipeData extends AsyncTask<Void, Void, Void> {

    public static final String KEY_IMAGE = "image";
    public static final String KEY_SERVINGS = "servings";
    public static final String KEY_STEPS = "steps";
    public static final String KEY_INGREDIENTS = "ingredients";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";

    private Context mContext;
    private RecipeListActivity.RecipeDataCallback mCallback;

    public FetchRecipeData(Context context, @Nullable RecipeListActivity.RecipeDataCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        makeJSONArrayRequest("http://go.udacity.com/android-baking-app-json");
        return null;
    }

    private void saveRecipes(JSONArray response) {
        try {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject recipeObject = response.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BakersFieldEntry.COLUMN_IMAGE_URL, recipeObject.getString(KEY_IMAGE));
                    contentValues.put(BakersFieldEntry.COLUMN_NAME, recipeObject.getString(KEY_NAME));
                    contentValues.put(BakersFieldEntry.COLUMN_INGREDIENT, recipeObject.getJSONArray(KEY_INGREDIENTS).toString());
                    contentValues.put(BakersFieldEntry.COLUMN_STEPS, recipeObject.getJSONArray(KEY_STEPS).toString());
                    contentValues.put(BakersFieldEntry.COLUMN_SERVINGS, recipeObject.getInt(KEY_SERVINGS));
                    contentValues.put(BakersFieldEntry.COLUMN_RECIPE_ID, recipeObject.getInt(KEY_ID));
                    mContext.getContentResolver().insert(BakersFieldEntry.CONTENT_URI, contentValues);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (mCallback != null) {
                mCallback.onDataReceived();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (mCallback != null) {
                mCallback.onReceiveFailed();
            }
        }
    }

    private boolean isRedirect(VolleyError error) {
        try {
            return error.networkResponse != null && (error.networkResponse.statusCode == HttpURLConnection.HTTP_MOVED_PERM
                    || error.networkResponse.statusCode == HttpURLConnection.HTTP_MOVED_TEMP);
        } catch (Exception ignored) {
            return false;
        }
    }

    private void makeJSONArrayRequest(String url) {
        Request<JSONArray> request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                saveRecipes(response);
                Log.e("Response", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isRedirect(error)) {
                    String location = error.networkResponse.headers.get("Location");
                    Log.e("Redirection", "Redirecting " + location);
                    makeJSONArrayRequest(location);
                } else {
                    Log.e("Error loading data", "An error occurred");
                    if (mCallback != null) {
                        mCallback.onReceiveFailed();
                    }
                }
            }
        });
        BakersFieldApplication.getInstance().addToRequestQueue(request);
    }
}
