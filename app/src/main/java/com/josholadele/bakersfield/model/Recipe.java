package com.josholadele.bakersfield.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Oladele on 6/12/17.
 */

public class Recipe implements Parcelable {

    public int getRecipeId() {
        return recipeId;
    }

    public int getServings() {
        return servings;
    }

    public String getName() {
        return name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    int recipeId;
    int servings;
    String name;
    String ingredients;
    String steps;
    String imageUrl;


    public Recipe(Parcel in) {
        recipeId = in.readInt();
        servings = in.readInt();
        name = in.readString();
        ingredients = in.readString();
        steps = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public Recipe(int recipeId, int servings, String name, String steps, String ingredients, String imageUrl) {
        this.recipeId = recipeId;
        this.servings = servings;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(recipeId);
        parcel.writeInt(servings);
        parcel.writeString(name);
        parcel.writeString(ingredients);
        parcel.writeString(steps);
        parcel.writeString(imageUrl);
    }
}
