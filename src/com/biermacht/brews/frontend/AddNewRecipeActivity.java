package com.biermacht.brews.frontend;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.biermacht.brews.R;
import com.biermacht.brews.recipe.BeerStyle;
import com.biermacht.brews.recipe.Recipe;
import com.biermacht.brews.utils.Utils;

public class AddNewRecipeActivity extends Activity implements OnClickListener {

	private Spinner beerTypeSpinner;
	private EditText recipeNameEditText;
	private EditText recipeDescEditText;
	private Button submitButton;
	private String beerType = Utils.BEERTYPE_OTHER.toString();
	private ArrayList<String> beerTypeArray;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);
        
        // Initialize views and stuff
        recipeNameEditText = (EditText) findViewById(R.id.recipe_name_edit_text);
        recipeDescEditText = (EditText) findViewById(R.id.recipe_description_edit_text);
        submitButton = (Button) findViewById(R.id.new_recipe_submit_button);
        
        //Arraylist of beer types
        beerTypeArray = Utils.getBeerStyleStringList();
        
        // Set up beer type spinner
        beerTypeSpinner = (Spinner) findViewById(R.id.beer_type_spinner);
        SpinnerAdapter<String> adapter = new SpinnerAdapter<String>(this, beerTypeArray);  
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        beerTypeSpinner.setAdapter(adapter);
        beerTypeSpinner.setSelection(0);    
        
        // Handle beer type selector here
        beerTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                beerType = beerTypeArray.get(position);
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // Blag
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_new_recipe, menu);
        return true;
    }

	public void onClick(View v) {
		if (v.getId() == R.id.new_recipe_submit_button)
		{
			String recipeName = recipeNameEditText.getText().toString();
			String recipeDesc = recipeDescEditText.getText().toString();
			Recipe r = Utils.createRecipeWithName(recipeName);
			
			if (!recipeDesc.isEmpty())
				r.setDescription(recipeDesc);
			
			if (!recipeName.isEmpty())
			{
				r.setBeerType(beerType);
				Utils.updateRecipe(r);
				finish();
			}
		}
	}
}