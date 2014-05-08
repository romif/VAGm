package com.vagm.vagmdroid.activities;

import java.io.IOException;

import android.util.Log;
import android.widget.TextView;

import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.LabelServiceTest;


/**
 * The Class MeasBlocksActivityTest.
 * @author Roman_Konovalov
 */
public class MeasBlocksActivityTest extends AbstractActivityTest<MeasBlocksActivity> {

	/**
	 * constructor.
	 */
	public MeasBlocksActivityTest() {
		super(MeasBlocksActivity.class);
	}


	public final void test() throws IOException {
		solo.clickOnButton(getActivity().getString(R.string.bGo));
		Log.d("TEST", ((TextView)solo.getView(R.id.group1Title)).getText().toString());
		assertTrue("Should be text: " + LabelServiceTest.GROUP1_TITLE, solo.searchText(LabelServiceTest.GROUP1_TITLE));
	}

}
