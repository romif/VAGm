package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_MEAS_BLOCKS_4GROUPS;
import static com.vagm.vagmdroid.service.TestConstatnts.DATA_STREAM_DTOS4;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import android.content.Intent;
import android.widget.TextView;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.LabelServiceTest;
import com.vagm.vagmdroid.service.PropertyService;

/**
 * The Class MeasBlocksActivityTest.
 * @author roman_konovalov
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class MeasBlocksActivityTest {

	/**
	 * activity.
	 */
	private MeasBlocksActivity activity;

	/**
	 * propertyService.
	 */
	@Inject
	private PropertyService propertyService;

	/**
	 * block11.
	 */
	private TextView block11;

	/**
	 * block12.
	 */
	private TextView block12;

	/**
	 * block13.
	 */
	private TextView block13;

	/**
	 * block14.
	 */
	private TextView block14;

	/**
	 * group1Title.
	 */
	private TextView group1Title;

	/**
	 * block11Label.
	 */
	private TextView block11Label;

	/**
	 * block12Label.
	 */
	private TextView block12Label;

	/**
	 * block13Label.
	 */
	private TextView block13Label;

	/**
	 * block14Label.
	 */
	private TextView block14Label;

	/**
	 * setUp.
	 * @throws IOException
	 */
	@Before
	public void setUp() {
		ShadowLog.stream = System.out;
		Intent intent = new Intent(Robolectric.getShadowApplication().getApplicationContext(), ControllerActivity.class);
		intent.putExtra(ControllerActivity.ECU, LabelServiceTest.ECU);
		activity = Robolectric.buildActivity(MeasBlocksActivity.class).withIntent(intent).create().get();
		block11 = (TextView) activity.findViewById(R.id.block11);
		block12 = (TextView) activity.findViewById(R.id.block12);
		block13 = (TextView) activity.findViewById(R.id.block13);
		block14 = (TextView) activity.findViewById(R.id.block14);
		group1Title = (TextView) activity.findViewById(R.id.group1Title);
		block11Label = (TextView) activity.findViewById(R.id.block11Label);
		block12Label = (TextView) activity.findViewById(R.id.block12Label);
		block13Label = (TextView) activity.findViewById(R.id.block13Label);
		block14Label = (TextView) activity.findViewById(R.id.block14Label);

	}

	/**
	 * testGetHandler.
	 * @throws IOException
	 */
	@Test
	public final void testGetHandler() throws IOException {
		byte[] buffer = hexStringToByteArray(BUFFER_MEAS_BLOCKS_4GROUPS);
		activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
		assertThat(block11.getText().toString(), equalTo(DATA_STREAM_DTOS4[0].getValue() + DATA_STREAM_DTOS4[0].getUnit()));
		assertThat(block12.getText().toString(), equalTo(DATA_STREAM_DTOS4[1].getValue() + DATA_STREAM_DTOS4[1].getUnit()));
		assertThat(block13.getText().toString(), equalTo(DATA_STREAM_DTOS4[2].getValue() + DATA_STREAM_DTOS4[2].getUnit()));
		assertThat(block14.getText().toString(), equalTo(DATA_STREAM_DTOS4[3].getValue() + DATA_STREAM_DTOS4[3].getUnit()));

		// assertThat(group1Title.getText().toString(), equalTo(GROUP1_TITLE));
	}

}
