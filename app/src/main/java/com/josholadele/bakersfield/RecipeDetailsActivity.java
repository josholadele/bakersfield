package com.josholadele.bakersfield;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.josholadele.bakersfield.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.josholadele.bakersfield.RecipeDetailFragment.ARG_ITEM;
import static com.josholadele.bakersfield.RecipeDetailFragment.ARG_STEP_POSITION;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeDetailsActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Recipe mItem;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_list);
        mFragmentManager = getSupportFragmentManager();
        if (getIntent() != null && getIntent().hasExtra(ARG_ITEM)) {
            mItem = getIntent().getExtras().getParcelable(ARG_ITEM);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mItem.getName());
        }

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.recipe_detail_container) != null || getResources().getBoolean(R.bool.is_tab)) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new RecipeStepsAdapter(mItem));
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder> {

        private final Recipe mRecipe;
        JSONArray stepsJSON;
        JSONArray ingredientsJSON;
        String ingredientList = "";

        public RecipeStepsAdapter(Recipe recipe) {
            mRecipe = recipe;
            try {
                stepsJSON = new JSONArray(recipe.getSteps());
                ingredientsJSON = new JSONArray(recipe.getIngredients());
                for (int i = 0; i < ingredientsJSON.length(); i++) {

                    JSONObject ingredientItemJSON = ingredientsJSON.getJSONObject(i);
                    ingredientList = ingredientList + (getString(R.string.ingredient_item,
                            String.valueOf(ingredientItemJSON.getDouble("quantity")),
                            ingredientItemJSON.getString("measure"),
                            ingredientItemJSON.getString("ingredient")));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mItem = mRecipe;
            if (position == 0) {
                holder.recipeStepLayout.setVisibility(View.GONE);
                holder.ingredientLayout.setVisibility(View.VISIBLE);
                holder.mIngredientList.setText(ingredientList);
                holder.mContentView.setGravity(Gravity.CENTER);
            } else {
                holder.recipeStepLayout.setVisibility(View.VISIBLE);
                holder.ingredientLayout.setVisibility(View.GONE);
                String positionString = String.valueOf(position - 1);
                try {
                    holder.mContentView.setGravity(Gravity.START);
                    holder.mIdView.setVisibility(View.VISIBLE);
                    holder.mIdView.setText(positionString);
                    holder.mContentView.setText(stepsJSON.getJSONObject(position - 1).getString("shortDescription"));
                } catch (Exception ignored) {

                }
                final int positionInt = position;
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putParcelable(ARG_ITEM, mRecipe);
                            arguments.putInt(ARG_STEP_POSITION, positionInt - 1);
                            RecipeDetailFragment fragment = new RecipeDetailFragment();
                            fragment.setArguments(arguments);
                            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.recipe_detail_container, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
//                            getSupportFragmentManager().beginTransaction()
//                                    .replace(R.id.recipe_detail_container, fragment)
//                                    .commit();
                        } else {
                            Bundle arguments = new Bundle();
                            arguments.putParcelable(ARG_ITEM, mRecipe);
                            arguments.putInt(ARG_STEP_POSITION, positionInt - 1);
                            RecipeDetailFragment fragment = new RecipeDetailFragment();
                            fragment.setArguments(arguments);

                            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frameLayout, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
//                            getSupportFragmentManager().beginTransaction()
//                                    .replace(R.id.frameLayout, fragment)
//                                    .commit();
//                            Context context = v.getContext();
//                            Intent intent = new Intent(context, RecipeDetailActivity.class);
//                            intent.putExtra(ARG_ITEM, mRecipe);
//                            intent.putExtra(ARG_STEP_POSITION, position - 1);
//
//                            context.startActivity(intent);
                        }
                    }
                });
            }


        }

        @Override
        public int getItemCount() {
            return stepsJSON.length() + 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public LinearLayout ingredientLayout;
            public LinearLayout recipeStepLayout;
            public final TextView mIdView;
            public TextView mIngredientList;
            public final TextView mContentView;
            public Recipe mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mIngredientList = (TextView) view.findViewById(R.id.ingredient_item);
                ingredientLayout = (LinearLayout) view.findViewById(R.id.ingredient_layout);
                recipeStepLayout = (LinearLayout) view.findViewById(R.id.recipe_step_layout);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
