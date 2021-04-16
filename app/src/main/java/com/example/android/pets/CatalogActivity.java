package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetsDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.android.pets.data.PetContract.PetsEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private PetsDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new PetsDbHelper(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        //Create and/or open a database to read from it
        //similar to ".open shelter.db"
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                PetsEntry._ID,
                PetsEntry.COLUMN_NAME,
                PetsEntry.COLUMN_BREED,
                PetsEntry.COLUMN_GENDER,
                PetsEntry.COLUMN_WEIGHT
        };

        Cursor cursor = db.query(PetsEntry.TABLE_NAME, projection,
                null, null, null, null, null);

        TextView displayView = (TextView) findViewById(R.id.text_view_pet);

        try {
            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
            displayView.append(PetsEntry._ID + " - " + PetsEntry.COLUMN_NAME + " - " + PetsEntry.COLUMN_GENDER
                    + " - " + PetsEntry.COLUMN_WEIGHT + "\n");

            //Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(PetsEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_NAME);
            int genderColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_WEIGHT);

            while(cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int integerGender = cursor.getInt(genderColumnIndex);
                String currentGender;
                if(integerGender==1)
                    currentGender = "male";
                else if(integerGender==2)
                    currentGender = "female";
                else
                    currentGender = "unknown";
                int currentWeight = cursor.getInt(weightColumnIndex);
                displayView.append("\n" + currentID + " - " + currentName + " - " + currentGender + " - " + currentWeight);
            }
        } finally {
            cursor.close();
        }
    }

    private void insertPet() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PetsEntry.COLUMN_NAME, "Toto");
        values.put(PetsEntry.COLUMN_BREED, "Terrier");
        values.put(PetsEntry.COLUMN_GENDER, PetsEntry.GENDER_MALE);
        values.put(PetsEntry.COLUMN_WEIGHT, 7);

        long newRowId = db.insert(PetsEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
