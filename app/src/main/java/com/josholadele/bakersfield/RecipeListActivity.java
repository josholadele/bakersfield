package com.josholadele.bakersfield;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.josholadele.bakersfield.data.FetchRecipeData;
import com.josholadele.bakersfield.model.Recipe;
import com.josholadele.bakersfield.provider.BakersFieldContract;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane = false;
    public static List<Recipe> recipes;
    SimpleItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        adapter = new SimpleItemRecyclerViewAdapter(recipes);

        loadRecipesFromDb();

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void loadRecipesFromDb() {
        showLoadingView();

        Cursor cursor = new CursorLoader(this, BakersFieldContract.BakersFieldEntry.CONTENT_URI, null, null, null, null).loadInBackground();
        if (cursor != null) {
            recipes = new ArrayList<>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_NAME)),
                        steps = cursor.getString(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_STEPS)),
                        ingredients = cursor.getString(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_INGREDIENT)),
                        imageUrl = cursor.getString(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_IMAGE_URL));
                int recipeId = cursor.getInt(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_RECIPE_ID)),
                        servings = cursor.getInt(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_SERVINGS));

                recipes.add(new Recipe(recipeId, servings, name, steps, ingredients, imageUrl));
            }
            if (recipes != null && recipes.size() > 0) {
                adapter.setRecipeData(recipes);
                showDataView();
            } else {
                new FetchRecipeData(RecipeListActivity.this, new RecipeDataCallback() {
                    @Override
                    public void onDataReceived() {
                        loadRecipesFromDb();
                    }

                    @Override
                    public void onReceiveFailed() {
                        showErrorView();
                    }
                }).execute();
            }
            cursor.close();
        } else {
            showErrorView();
        }

//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                Cursor cursor = getContentResolver().query(BakersFieldContract.BakersFieldEntry.CONTENT_URI, null, null, null, null);
//                if (cursor != null) {
//                    while (cursor.moveToNext()) {
//                        String name = cursor.getString(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_NAME)),
//                                steps = cursor.getString(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_STEPS)),
//                                ingredients = cursor.getString(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_INGREDIENT)),
//                                imageUrl = cursor.getString(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_IMAGE_URL));
//                        int recipeId = cursor.getInt(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_RECIPE_ID)),
//                                servings = cursor.getInt(cursor.getColumnIndex(BakersFieldContract.BakersFieldEntry.COLUMN_SERVINGS));
//
//                        recipes.add(new Recipe(recipeId, servings, name, steps, ingredients, imageUrl));
//                    }
//                    cursor.close();
//                }
//                return null;
//            }
//        };
    }

    private void showDataView() {
        findViewById(R.id.recipe_list).setVisibility(View.VISIBLE);
        findViewById(R.id.error_view).setVisibility(View.GONE);
        findViewById(R.id.loading_view).setVisibility(View.GONE);
    }

    private void showLoadingView() {
        findViewById(R.id.recipe_list).setVisibility(View.GONE);
        findViewById(R.id.error_view).setVisibility(View.GONE);
        findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
    }

    private void showErrorView() {
        findViewById(R.id.recipe_list).setVisibility(View.GONE);
        findViewById(R.id.error_view).setVisibility(View.VISIBLE);
        findViewById(R.id.loading_view).setVisibility(View.GONE);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        boolean isTab = getResources().getBoolean(R.bool.is_tab),
                isLandscape = getResources().getBoolean(R.bool.is_landscape);
        if (isTab || isLandscape) {

            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            recyclerView.setHasFixedSize(true);

        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
        }
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Recipe> mValues;

        SimpleItemRecyclerViewAdapter(List<Recipe> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_item, parent, false);
            return new ViewHolder(view);
        }

        private void setRecipeData(List<Recipe> recipes) {
            mValues = recipes;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mRecipeTitle.setText(mValues.get(position).getName());
            holder.mServings.setText(getString(R.string.string_servings, mValues.get(position).getServings()));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(RecipeDetailFragment.ARG_ITEM, holder.mItem);
                        RecipeDetailFragment fragment = new RecipeDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.recipe_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RecipeDetailsActivity.class);
                        intent.putExtra(RecipeDetailFragment.ARG_ITEM, holder.mItem);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (mValues == null) return 0;
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mRecipeTitle;
            public final TextView mServings;
            public Recipe mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mRecipeTitle = (TextView) view.findViewById(R.id.recipe_title);
                mServings = (TextView) view.findViewById(R.id.recipe_servings);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mRecipeTitle.getText() + "'";
            }
        }
    }

    public interface RecipeDataCallback {
        void onDataReceived();

        void onReceiveFailed();
    }
}
