package com.biermacht.brews.ingredient;

public class Yeast extends Ingredient {

	public Yeast(String name)
	{
		super(name);
	}

	@Override
	public String getType() {
		return Ingredient.YEAST;
	}

	@Override
	public String getShortDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAmount(double amt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUnits() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
}