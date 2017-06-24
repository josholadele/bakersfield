package com.josholadele.bakersfield.provider;

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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.josholadele.bakersfield.provider.BakersFieldContract.BakersFieldEntry;


public class BakersFieldDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "bakersfield.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public BakersFieldDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold the plants data
        final String SQL_CREATE_PLANTS_TABLE = "CREATE TABLE " + BakersFieldEntry.TABLE_NAME + " (" +
                BakersFieldEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BakersFieldEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                BakersFieldEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                BakersFieldEntry.COLUMN_INGREDIENT + " TEXT NOT NULL, " +
                BakersFieldEntry.COLUMN_STEPS + " TEXT NOT NULL, " +
                BakersFieldEntry.COLUMN_SERVINGS + " INTEGER NOT NULL, " +
                BakersFieldEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_PLANTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BakersFieldEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
