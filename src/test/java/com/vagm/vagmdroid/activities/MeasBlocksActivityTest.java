package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_MEAS_BLOCKS_4GROUPS;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;

import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
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

	public final void testHandleMessage() {
		byte[] buffer = hexStringToByteArray(BUFFER_MEAS_BLOCKS_4GROUPS);
		getActivity().getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		assertTrue("Should be text: " + LabelServiceTest.GROUP1_TITLE, solo.searchText(LabelServiceTest.GROUP1_TITLE));
		
	}

/*	public final void test() throws IOException {
		solo.clickOnButton(getActivity().getString(R.string.bGo));
		Log.d("TEST", ((TextView)solo.getView(R.id.group1Title)).getText().toString());
		assertTrue("Should be text: " + LabelServiceTest.GROUP1_TITLE, solo.searchText(LabelServiceTest.GROUP1_TITLE));
	}*/

}
