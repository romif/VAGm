package com.vagm.vagmdroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;
import com.vagm.vagmdroid.service.BluetoothService;
import com.vagm.vagmdroid.service.LabelServiceTest;
import com.vagm.vagmdroid.service.PropertyService;
/**
 * The Class AbstractActivityTest.
 * @author Roman_Konovalov
 * @param <T>
 */
public abstract class AbstractActivityTest <T extends Activity> extends ActivityInstrumentationTestCase2<T> {

	/**
	 * constructor.
	 * @param activityClass activityClass
	 */
	public AbstractActivityTest(final Class<T> activityClass) {
		super(activityClass);
	}

	/**
	 * solo.
	 */
	protected Solo solo;

	/**
	 * controller.
	 */
	protected Intent intent;

	/**
	 * {@inheritDoc}
	 */
	public void setUp() throws Exception {
	    super.setUp();
	    PropertyService.init();
	    intent = new Intent();
		intent.putExtra(MainActivity.CONTROLLER_CODE, 0x01);
		intent.putExtra(BluetoothService.BLUETOOTH_SERVICE_INSTANCE, BluetoothService.getInstance());
		intent.putExtra(ControllerActivity.ECU, LabelServiceTest.ECU);
		setActivityIntent(intent);
		solo = new Solo(getInstrumentation(), getActivity());
		getInstrumentation().waitForIdleSync();
	}

	/**
	 * {@inheritDoc}
	 */
	public void tearDown() throws Exception {
	    solo.finishOpenedActivities();
	    super.tearDown();
	}


}
