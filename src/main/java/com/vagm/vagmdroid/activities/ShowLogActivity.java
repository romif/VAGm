package com.vagm.vagmdroid.activities;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.vagm.vagmdroid.R;

/**
 * @author Roman_Konovalov
 */
public class ShowLogActivity extends CustomAbstractActivity implements OnClickListener {
    
    /**
     * logText.
     */
    @InjectView(R.id.log_text)
    private TextView logText;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.show_log);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);
        logText.setText((String)getIntent().getExtras().get(SendLogActivity.LOG_TEXT));
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public void onClick(View v) {
    }

}
