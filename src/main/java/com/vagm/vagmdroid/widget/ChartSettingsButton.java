package com.vagm.vagmdroid.widget;

import java.util.Arrays;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import com.vagm.vagmdroid.R;

/**
 * The Class ChartSettingsButton.
 * 
 * @author Roman_Konovalov
 */
public class ChartSettingsButton extends Button implements OnCheckedChangeListener {

    /**
     * OK_BUTTON_ID.
     */
    public static final int OK_BUTTON_ID = 0;

    private String[] items = null;

    private boolean[] blocks = null;

    private AlertDialog alertDialog;

    private OnClickListener listener;

    /**
     * constructor.
     * @param context
     */
    public ChartSettingsButton(final Context context) {
        super(context);
    }

    /**
     * constructor.
     * 
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ChartSettingsButton(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * constructor.
     * 
     * @param context
     * @param attrs
     */
    public ChartSettingsButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick() {
        // TODO Auto-generated method stub

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // builder.setMultiChoiceItems(_items, _selection, listener);
        alertDialog = builder.show();
        alertDialog.setContentView(getLinearLayout());
        alertDialog.setCancelable(false);
        enableDesableCheckBoxes();
        reCheckBoxes();
        return super.performClick();
    }

    /**
     * getLayout.
     * 
     * @return LinearLayout
     */
    private LinearLayout getLinearLayout() {
        final LinearLayout linearLayout = new LinearLayout(getContext());
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final LayoutParams radioParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        int id = 0;
        for (int i = 0; i < items.length; i++) {
            final DeselectableRadioGroup radioGroup = new DeselectableRadioGroup(getContext());
            radioGroup.setLayoutParams(radioParams);
            radioGroup.setOrientation(LinearLayout.HORIZONTAL);

            final DeselectableRadioButton radioButton = new DeselectableRadioButton(getContext());
            radioButton.setLayoutParams(radioParams);
            radioButton.setText(items[i]);
            radioButton.setId(id);
            radioButton.setChecked(blocks[id]);
            id++;
            radioButton.setOnCheckedChangeListener(this);

            final CheckBox checkBox = new CheckBox(getContext());
            checkBox.setLayoutParams(radioParams);
            checkBox.setText(getContext().getString(R.string.use_right_axis));
            checkBox.setId(id);
            checkBox.setChecked(blocks[id]);
            checkBox.setOnCheckedChangeListener(this);
            id++;

            radioGroup.addView(radioButton);
            radioGroup.addView(checkBox);
            linearLayout.addView(radioGroup);
        }

        final Button btn = new Button(getContext());
        btn.setText(getContext().getString(R.string.bGo));
        btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        btn.setId(OK_BUTTON_ID);
        btn.setOnClickListener(listener);
        linearLayout.addView(btn);

        return linearLayout;
    }

    /**
     * @return the items
     */
    public String[] getItems() {
        return Arrays.copyOf(items, items.length);
    }

    /**
     * @param items
     *            the items to set
     */
    public void setItems(final String[] items) {
        this.items = Arrays.copyOf(items, items.length);
    }

    /**
     * @return the blocks
     */
    public boolean[] getBlocks() {
        return Arrays.copyOf(blocks, blocks.length);
    }

    /**
     * @param blocks
     *            the blocks to set
     */
    public void setBlocks(final boolean[] blocks) {
        this.blocks = Arrays.copyOf(blocks, blocks.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        blocks[buttonView.getId()] = isChecked;
        if (buttonView instanceof DeselectableRadioButton) {
            enableDesableCheckBoxes();
            reCheckBoxes();
        } else if (buttonView instanceof CheckBox) {
            if (getCheckedBlocksCount() == getCheckedRightAxes()) {
                deselectCheckBoxes();
            }
        }

    }

    /**
     * hideDialog.
     */
    public void hideDialog() {
        alertDialog.dismiss();
    }

    public void setOnClickListener(final OnClickListener listener) {
        this.listener = listener;
    }

    private int getCheckedBlocksCount() {
        int count = 0;
        for (int i = 0; i < blocks.length; i += 2) {
            if (blocks[i]) {
                count++;
            }
        }
        return count;
    }

    private int getCheckedRightAxes() {
        int count = 0;
        for (int i = 1; i < blocks.length; i += 2) {
            if (blocks[i]) {
                count++;
            }
        }
        return count;
    }

    private void reCheckBoxes() {
        for (int i = 0; i < blocks.length; i += 2) {
            DeselectableRadioButton radioButton = (DeselectableRadioButton) alertDialog.getWindow().findViewById(i);
            CheckBox checkBox = (CheckBox) alertDialog.getWindow().findViewById(i + 1);
            if (!radioButton.isChecked()) {
                checkBox.setEnabled(false);
            }
        }
    }

    private void deselectCheckBoxes() {
        for (int i = 1; i < blocks.length; i += 2) {
            CheckBox checkBox = (CheckBox) alertDialog.getWindow().findViewById(i);
            blocks[i] = false;
            checkBox.setChecked(false);
        }
    }

    private void enableDesableCheckBoxes() {
        boolean isEnabled;
        if (getCheckedBlocksCount() > 1) {
            isEnabled = true;
        } else {
            isEnabled = false;
        }
        for (int i = 1; i < blocks.length; i += 2) {
            CheckBox checkBox = (CheckBox) alertDialog.getWindow().findViewById(i);
            checkBox.setEnabled(isEnabled);
            if (!isEnabled) {
                blocks[i] = false;
                checkBox.setChecked(false);
            }
        }
    }

}
