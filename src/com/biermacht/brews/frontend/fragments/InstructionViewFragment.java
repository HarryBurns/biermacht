package com.biermacht.brews.frontend.fragments;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.biermacht.brews.R;
import com.biermacht.brews.frontend.DisplayRecipeActivity;
import com.biermacht.brews.frontend.adapters.InstructionArrayAdapter;
import com.biermacht.brews.recipe.Instruction;
import com.biermacht.brews.recipe.Recipe;
import com.biermacht.brews.utils.Utils;

public class InstructionViewFragment extends Fragment {

	private int resource;
	private Recipe r;
	private OnItemClickListener mClickListener;
	private ListView instructionListView;
	private ArrayList<Instruction> instructionList;
	LinearLayout pageView;
	
	public InstructionViewFragment(Recipe r)
	{
		this.resource = R.layout.instruction_view;
		this.r = r;
	}
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		pageView = new LinearLayout(DisplayRecipeActivity.appContext);
		inflater.inflate(resource, pageView);
		
		setHasOptionsMenu(true);
		
		// Initialize important junk
		instructionListView = (ListView) pageView.findViewById(R.id.instruction_list);
		instructionList = r.getInstructionList();
  
		if (instructionList.size() > 0)
		{
			LinearLayout instructionListTitleView = (LinearLayout) pageView.findViewById(R.id.title_row);
	  
			InstructionArrayAdapter instructionArrayAdapter = new InstructionArrayAdapter(DisplayRecipeActivity.appContext, instructionList);
			instructionListView = (ListView) pageView.findViewById(R.id.instruction_list);
			instructionListView.setAdapter(instructionArrayAdapter);
			instructionListView.setVisibility(View.VISIBLE);
			instructionListTitleView.setVisibility(View.VISIBLE);
		}
		else
		{
			TextView noInstructionsView = (TextView) pageView.findViewById(R.id.no_instructions_view);
			noInstructionsView.setVisibility(View.VISIBLE);
		}
		
		// Remove all views, then add new ones
		container.removeAllViews();
		container.addView(pageView);
		
		return inflater.inflate(resource, container, false);
	}
	
	public void update()
	{
		if (pageView != null && instructionListView != null)
		{
			// Update the recipe and its fields..
			r = Utils.getRecipeWithId(r.getId());
			r.update();
			instructionList = r.getInstructionList();
			
			if (instructionList.size() > 0)
			{
				InstructionArrayAdapter instructionArrayAdapter = new InstructionArrayAdapter(DisplayRecipeActivity.appContext, instructionList);
				instructionListView = (ListView) pageView.findViewById(R.id.instruction_list);
				instructionListView.setAdapter(instructionArrayAdapter);
				instructionListView.setVisibility(View.VISIBLE);
				pageView.findViewById(R.id.title_row).setVisibility(View.VISIBLE);
				pageView.findViewById(R.id.no_instructions_view).setVisibility(View.GONE);
			}
			else
			{
				pageView.findViewById(R.id.no_instructions_view).setVisibility(View.VISIBLE);
				instructionListView.setVisibility(View.GONE);
				pageView.findViewById(R.id.title_row).setVisibility(View.GONE);
			}
		}
	}
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
			inflater.inflate(R.menu.instruction_menu, menu);
	}
	
}