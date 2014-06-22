package com.biermacht.brews.frontend.IngredientActivities;

import java.util.Arrays;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.biermacht.brews.ingredient.Misc;
import com.biermacht.brews.utils.Constants;
import com.biermacht.brews.utils.Database;

public class AddCustomMiscActivity extends AddMiscActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Set views
        this.setViews(Arrays.asList(nameView, amountView, unitsSpinner, timeView, useSpinner, typeSpinner));
        
        // Set initial values
        misc = new Misc("Custom misc");
        setValues(misc);
    }

    @Override
    public void acquireValues() throws Exception
    {
        super.acquireValues();
        misc.setShortDescription("Custom misc");
        misc.setUseFor(use);
    }
    
    public void setInitialSearchableListSelection()
    {
    	// Don't set the searchable list selector.
    	// Initial values are set based on the new ingredient.
    }

    @Override
    public void onFinished()
    {
        Log.d("AddCustomMisc::onFinished", "Adding misc to db_custom: " + misc.getName());
        Database.addIngredientToVirtualDatabase(Constants.DATABASE_CUSTOM, misc, Constants.MASTER_RECIPE_ID);
        if (haveRecipe())
        {
        	// If not master ID, update the recipe.
            Log.d("AddCustomMisc::onFinished", "Adding misc '" + 
            		misc.getName() + "' to recipe '" + mRecipe.getRecipeName() + "'");
            mRecipe.addIngredient(misc);
            mRecipe.save();
        }
        Log.d("AddCustomMisc::onFinished", "Closing activity");
        finish();
    }

}
