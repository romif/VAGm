/*
 * Copyright (C) 2012 Kris Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vagm.vagmdroid.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.widget.DeselectableRadioButton;
import com.vagm.vagmdroid.widget.DeselectableRadioGroup;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * A Spinner view that does not dismiss the dialog displayed when the control is
 * "dropped down" and the user presses it. This allows for the selection of more
 * than one option.
 */
public class MultiSelectionSpinner extends Spinner implements OnMultiChoiceClickListener, OnCheckedChangeListener {

	/**
	 * OK_BUTTON_ID.
	 */
	public final static int OK_BUTTON_ID = 0;

	String[] _items = null;
	boolean[] _selection = null;
	boolean[] blocks = null;

	ArrayAdapter<String> _proxyAdapter;
	
	OnClickListener listener;
	
	AlertDialog alertDialog;
	
	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}

	/**
	 * Constructor for use when instantiating directly.
	 * @param context
	 */
	public MultiSelectionSpinner(final Context context) {
		super(context);

		_proxyAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		super.setAdapter(_proxyAdapter);
	}

	/**
	 * Constructor used by the layout inflater.
	 * @param context
	 * @param attrs
	 */
	public MultiSelectionSpinner(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		_proxyAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		super.setAdapter(_proxyAdapter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
		if (_selection != null && which < _selection.length) {
			_selection[which] = isChecked;

			_proxyAdapter.clear();
			_proxyAdapter.add(buildSelectedItemString());
			setSelection(0);
		} else {
			throw new IllegalArgumentException("Argument 'which' is out of bounds.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performClick() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		//builder.setMultiChoiceItems(_items, _selection, listener);
		alertDialog = builder.show();
		alertDialog.setContentView(getLayout());
		alertDialog.setCancelable(false);
		return true;
	}

	/**
	 * getLayout.
	 * @return LinearLayout
	 */
	private LinearLayout getLayout() {
		final LinearLayout linearLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		linearLayout.setLayoutParams(layoutParams);
		linearLayout.setOrientation(LinearLayout.VERTICAL);

		LayoutParams radioParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int id = 0;
		for (int i = 0; i < _items.length; i++) {
			DeselectableRadioGroup radioGroup = new DeselectableRadioGroup(getContext());
			radioGroup.setLayoutParams(radioParams);
			radioGroup.setOrientation(LinearLayout.HORIZONTAL);

			DeselectableRadioButton radioButtonL = new DeselectableRadioButton(getContext());
			radioButtonL.setLayoutParams(radioParams);
			//radioButtonL.setText(_items[i]);
			radioButtonL.setId(id);
			radioButtonL.setChecked(blocks[id]);
			id++;
			radioButtonL.setOnCheckedChangeListener(this);

			DeselectableRadioButton radioButtonR = new DeselectableRadioButton(getContext());
			radioButtonR.setLayoutParams(radioParams);
			radioButtonR.setText(_items[i]);
			radioButtonR.setId(id);
			radioButtonR.setChecked(blocks[id]);
			id++;
			radioButtonR.setOnCheckedChangeListener(this);

			radioGroup.addView(radioButtonL);
			radioGroup.addView(radioButtonR);
			linearLayout.addView(radioGroup);
		}

		Button btn = new Button(getContext());
		btn.setText(getContext().getString(R.string.bGo));
		btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		btn.setId(OK_BUTTON_ID);
		btn.setOnClickListener(listener);
		linearLayout.addView(btn);

		return linearLayout;
	}

	/**
	 * MultiSelectSpinner does not support setting an adapter. This will throw
	 * an exception.
	 * @param adapter
	 */
	@Override
	public void setAdapter(final SpinnerAdapter adapter) {
		throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
	}

	/**
	 * Sets the options for this spinner.
	 * @param items
	 */
	public void setItems(final String[] items) {
		_items = items;
		_selection = new boolean[_items.length];
		blocks = new boolean[_items.length * 2];

		Arrays.fill(_selection, false);
	}

	/**
	 * Sets the options for this spinner.
	 * @param items
	 */
	public void setItems(final List<String> items) {
		_items = items.toArray(new String[items.size()]);
		_selection = new boolean[_items.length];
		blocks = new boolean[_items.length * 2];

		Arrays.fill(_selection, false);
	}

	/**
	 * Sets the selected options based on an array of string.
	 * @param selection
	 */
	public void setSelection(final String[] selection) {
		for (final String sel : selection) {
			for (int j = 0; j < _items.length; ++j) {
				if (_items[j].equals(sel)) {
					_selection[j] = true;
				}
			}
		}
	}

	/**
	 * Sets the selected options based on a list of string.
	 * 
	 * @param selection
	 */
	public void setSelection(final List<String> selection) {
		for (final String sel : selection) {
			for (int j = 0; j < _items.length; ++j) {
				if (_items[j].equals(sel)) {
					_selection[j] = true;
				}
			}
		}
	}

	/**
	 * Sets the selected options based on an array of positions.
	 * @param selectedIndicies
	 */
	public void setSelection(final int[] selectedIndicies) {
		for (final int index : selectedIndicies) {
			if (index >= 0 && index < _selection.length) {
				_selection[index] = true;
			} else {
				throw new IllegalArgumentException("Index " + index + " is out of bounds.");
			}
		}
	}

	/**
	 * Returns a list of strings, one for each selected item.
	 * @return
	 */
	public List<String> getSelectedStrings() {
		final List<String> selection = new LinkedList<String>();
		for (int i = 0; i < _items.length; ++i) {
			if (_selection[i]) {
				selection.add(_items[i]);
			}
		}
		return selection;
	}

	/**
	 * Returns a list of positions, one for each selected item.
	 * @return
	 */
	public List<Integer> getSelectedIndicies() {
		final List<Integer> selection = new LinkedList<Integer>();
		for (int i = 0; i < _items.length; ++i) {
			if (_selection[i]) {
				selection.add(i);
			}
		}
		return selection;
	}

	/**
	 * Builds the string for display in the spinner.
	 * @return comma-separated list of selected items
	 */
	private String buildSelectedItemString() {
		final StringBuilder sb = new StringBuilder();
		boolean foundOne = false;

		for (int i = 0; i < _items.length; ++i) {
			if (_selection[i]) {
				if (foundOne) {
					sb.append(", ");
				}
				foundOne = true;

				sb.append(_items[i]);
			}
		}

		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		blocks[buttonView.getId()] = isChecked;
	}
	
	public boolean[] getBlocks() {
		return blocks;
	}
	
	public void setBlocks(boolean[] blocks) {
		this.blocks = blocks;
	}
	
	public void hideDialog() {
		alertDialog.dismiss();
	}

}
