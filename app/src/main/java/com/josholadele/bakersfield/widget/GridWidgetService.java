package com.josholadele.bakersfield.widget;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.josholadele.bakersfield.R;
import com.josholadele.bakersfield.model.Recipe;
import com.josholadele.bakersfield.provider.BakersFieldContract;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.josholadele.bakersfield.RecipeDetailFragment.ARG_ITEM;


public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;

    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get all plant info ordered by creation time
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(
                BakersFieldContract.BakersFieldEntry.CONTENT_URI,
                null, null, null, null);

    }

    @Override
    public void onDestroy() {
        if (mCursor != null)
            mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);


        String name = mCursor.getString(mCursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_NAME)),
                steps = mCursor.getString(mCursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_STEPS)),
                ingredients = mCursor.getString(mCursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_INGREDIENT)),
                imageUrl = mCursor.getString(mCursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_IMAGE_URL));
        int recipeId = mCursor.getInt(mCursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_RECIPE_ID)),
                servings = mCursor.getInt(mCursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_SERVINGS));

        Recipe recipe = new Recipe(recipeId, servings, name, steps, ingredients, imageUrl);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        // Update the plant image
        String servingsText = mContext.getString(R.string.string_servings, servings);
        views.setTextViewText(R.id.recipe_servings, servingsText);
        views.setTextViewText(R.id.recipe_title, name);
        String ingredientList = "";
        try {
            JSONArray ingredientsJSON = new JSONArray(ingredients);
            for (int i = 0; i < ingredientsJSON.length(); i++) {

                JSONObject ingredientItemJSON = ingredientsJSON.getJSONObject(i);
                ingredientList = ingredientList + (mContext.getString(R.string.ingredient_item,
                        String.valueOf(ingredientItemJSON.getDouble("quantity")),
                        ingredientItemJSON.getString("measure"),
                        ingredientItemJSON.getString("ingredient")));

            }
        } catch (Exception ignored) {

        }
        views.setTextViewText(R.id.ingredient_item, ingredientList);
        // Always hide the water drop in GridView mode
//        views.setViewVisibility(R.id.widget_water_button, View.GONE);

        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        Bundle extras = new Bundle();
        extras.putParcelable(ARG_ITEM, recipe);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.item_view, fillInIntent);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

