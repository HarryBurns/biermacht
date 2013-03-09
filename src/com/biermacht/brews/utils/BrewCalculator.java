package com.biermacht.brews.utils;

import java.util.ArrayList;

import com.biermacht.brews.ingredient.Fermentable;
import com.biermacht.brews.ingredient.Hop;
import com.biermacht.brews.ingredient.Ingredient;
import com.biermacht.brews.ingredient.Yeast;
import com.biermacht.brews.recipe.Recipe;
import android.util.Log;
import android.os.*;

public class BrewCalculator {
	
	/**
	 *  Beer details calculations
	 *  http://www.howtobrew.com/section1/chapter5-5.html
	 *  
	 *  MCU = (weight of grain in lbs)*(color of grain in lovibond) / (volume in gal)
	 *  SRM = 1.4922 * MCU**.6859
	 *  
	 *  OG = (Weight of grain in lbs)*(Extract potential of grain)*(Extraction Efficiency)
	 *  
	 *  IBU = 7498*(Weight of hops in OZ)*(% of Alpha Acids)*(Utilization factor) / (Volume in gal)
	 */
	
	public static float calculateColorFromRecipe(Recipe r)
	{
		float SRM = 0;
		float MCU = 0;
		ArrayList<Ingredient> ingredientsList = r.getIngredientList();
		
		for (Ingredient i : ingredientsList)
		{
			if (i.getType().equals(Ingredient.FERMENTABLE))
			{
				Fermentable g = (Fermentable) i;
				MCU += g.getAmount() * g.getLovibondColor() / r.getBatchSize();
			}
		}
		SRM = (float) (1.4922*Math.pow(MCU, .6859));
		return SRM;
	}
	
	public static double calculateOriginalGravityFromRecipe(Recipe r)
	{
		if (r.getType().equals(Recipe.EXTRACT))
			return calculateExtractOG(r);
		else
			return calculateExtractOG(r);
	}
	
	public static double calculateGrainPercent(Recipe r, Ingredient i)
	{
		if(i.getType().equals(Ingredient.FERMENTABLE))
		{
			double g_amt = i.getAmount();
			double tot_amt = getTotalGrainWeight(r);
			
			return (g_amt / tot_amt) * 100;
		}
		
		return 0;
	}
	
	public static double getTotalGrainWeight(Recipe r)
	{
		double amt = 0;
		for(Ingredient i : r.getIngredientList())
		{
			if (i.getType().equals(Ingredient.FERMENTABLE))
			{
				amt += i.getAmount();
			}
		}
		
		return amt;
	}
	
	public static double calculateExtractOG(Recipe r)
	{
		float gravity_points = 0;
		ArrayList<Ingredient> ingredientsList = r.getIngredientList();
		
		// http://homebrew.stackexchange.com/questions/1434/wiki-how-do-you-calculate-original-gravity
		for (Ingredient i : ingredientsList)
		{
			gravity_points += calculateGravityPoints(r, i);
		}
		return 1 + gravity_points/1000;
	}
	
	public static double calculateBoilGrav(Recipe r)
	{
		double mGPs = calculateExtractOG(r) - 1;
		return 1 + (mGPs * r.getBatchSize() / r.getBoilSize());
	}
	
	public static double calculateGravityPoints(Recipe r, Ingredient i)
	{
		double pts = 0;
		if (i.getType().equals(Ingredient.FERMENTABLE))
		{
			Fermentable f = (Fermentable) i;
					
			if(f.getFermentableType().equals(Fermentable.EXTRACT))
			{
				pts = f.getAmount() * f.getPpg() / r.getBatchSize();
			}
			else if (f.getFermentableType().equals(Fermentable.SUGAR))
			{
				pts = f.getAmount() * f.getPpg() / r.getBatchSize();
			}
			else if (f.getFermentableType().equals(Fermentable.GRAIN))
			{
				pts = 5 * f.getAmount() / r.getBatchSize();
				
				if(f.getName().equalsIgnoreCase("Roasted Barley"))
					pts = 10 * f.getAmount() / r.getBatchSize();
				if(f.getName().equalsIgnoreCase("Black Patent Malt"))
					pts = 10 * f.getAmount() / r.getBatchSize();
			}
			else
			{
				pts = f.getAmount() * f.getPpg() / r.getBatchSize();
			}
		}
		return pts;
	}
	
	public static double calculateIbuFromRecipe(Recipe r)
	{
		double ibu = 0;
		
		ArrayList<Ingredient> ingredientsList = r.getIngredientList();
		
		// http://www.howtobrew.com/section1/chapter5-5.html
		for (Ingredient i : ingredientsList)
		{
			ibu += calculateHopIbu(r, i);
		}
		
		return ibu;
	}
	
	public static double calculateHopIbu(Recipe r, Ingredient i)
	{
		double ibu = 0;
		double AAU = 0;
		double utilization = 0;
		
			if (i.getType().equals(Ingredient.HOP))
			{
				Hop h = (Hop) i;
				// Only add bitterness if we are boiling the hops!
				if (h.getUse().equals(Hop.USE_BOIL))
				{
					utilization = getHopUtilization(r, h);
					AAU = h.getAmount() * h.getAlphaAcidContent();
					ibu = (AAU * utilization * 75)/r.getBatchSize();
				}
			}

		// We scale down because our algorithm returns a few IBU too high.
		return .94*ibu;
	}
	
	public static double estimateFinalGravityFromRecipe(Recipe r)
	{
		double OG = r.getOG();
		double attn;          // Yeast attentuation
		
		for (Ingredient i : r.getIngredientList())
		{
			if (i.getType().equals(Ingredient.YEAST))
			{
				// First, try to determine if we have an attenuation value
				Yeast y = (Yeast) i;
				attn = y.getAttenuation();
				
				if (attn > 0)
				{
					double FG = OG - (attn)*(OG-1)*(.0106);
					return FG;
				}	
			}
		}
		
		// If we don't have any yeast, there's no fermentation!
		return OG;
	}
	
	public static double calculateAbvFromRecipe(Recipe r)
	{
		double FG = r.getFG();
		double OG = r.getOG();
		
		return (OG-FG) * 131;
	}
	
	public static float getHopUtilization(Recipe r, Hop i)
	{
		float utilization;
		double bignessFactor;
		double boilTimeFactor;
		double boilGrav = calculateBoilGrav(r);
		
		// Default : 1.65/4.25
		bignessFactor = 1.65 * Math.pow(.00025, boilGrav-1);
		boilTimeFactor = (1 - Math.pow(Math.E, -.04*i.getTime()))/4.25;
		
		utilization = (float) (bignessFactor * boilTimeFactor);
		
		return utilization;
	}

}
