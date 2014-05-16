package com.vagm.vagmdroid.activities;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.dto.DataStreamDTO;
import com.vagm.vagmdroid.dto.LabelDTO;
import com.vagm.vagmdroid.enums.VAGmConstans;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.BufferService;
import com.vagm.vagmdroid.service.LabelService;

/**
 * The Class MeasBlocksActivity.
 * @author Roman_Konovalov
 */
public class MeasBlocksActivity extends CustomAbstractActivity implements OnClickListener {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MeasBlocksActivity.class);

	/**
	 * dataList.
	 */
	private final LinkedList<float[]> dataList = new LinkedList<>();

	/**
	 * MAX_DATA_LIST_SIZE.
	 */
	private static final int MAX_DATA_LIST_SIZE = 1000;

	/**
	 * labels.
	 */
	private SparseArray<LabelDTO> labels;

	/**
	 * ecu.
	 */
	private String ecu;

	/**
	 * group.
	 */
	private Integer group;

	/**
	 * bufferService.
	 */
	@Inject
	private BufferService bufferService;

	/**
	 * labelService.
	 */
	@Inject
	private LabelService labelService;

	/**
	 * The Handler that gets information back from the BluetoothService.
	 */
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg)  {
			super.handleMessage(msg);
			final ServiceCommand serviceCommand = ServiceCommand.values()[msg.what];
			if (serviceCommand == ServiceCommand.MESSAGE_READ) {
				byte[] message = (byte[]) msg.obj;
				LOG.trace("Recieved message from conroller: {}", bufferService.bytesToHex(message));
				try {
					proceedMessage(message);
				} catch (final ControllerCommunicationException e) {
					getControllerNotAnswerAlert().show();
				} catch (ControllerWrongResponseException e) {
					LOG.warn("Wrong response", e);
				}
			} else if (serviceCommand == ServiceCommand.CONNECTION_LOST) {
				Toast.makeText(getApplicationContext(), getText(R.string.connection_lost), Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(final View v) {
		try {
			switch (v.getId()) {
			case R.id.bGo1:
				group = getGroup1();
				LOG.debug("Request for group {}", group);
				bluetoothService.write(group);
				setLabels();
				break;

			case R.id.bUp1:
				group = getGroup1();
				if (group < 0xFF) {
					group++;
					((EditText) findViewById(R.id.groupInput1)).setText(String.valueOf(group));
					LOG.debug("Request for group {}", group);
					bluetoothService.write(group);
				}
				setLabels();
				break;

			case R.id.bDn1:
				group = getGroup1();
				if (group > 1) {
					group--;
					((EditText) findViewById(R.id.groupInput1)).setText(String.valueOf(group));
					LOG.debug("Request for group {}", group);
					bluetoothService.write(group);
				}
				setLabels();
				break;

			case R.id.bGo2:
				bluetoothService.write(getGroup2());
				setLabels();
				break;

			case R.id.bUp2:
				group = getGroup2();
				if (group < 0xFF) {
					group++;
					((EditText) findViewById(R.id.group2)).setText(String.valueOf(group));
					bluetoothService.write(group);
				}
				setLabels();
				break;

			case R.id.bDn2:
				group = getGroup2();
				if (group > 1) {
					group--;
					((EditText) findViewById(R.id.group2)).setText(String.valueOf(group));
					bluetoothService.write(group);
				}
				setLabels();
				break;

			case R.id.bMeasBlocksBack:
				bluetoothService.write(VAGmConstans.EXIT_COMMAND);
				finish();
				break;

			default:
				break;
			}
		} catch (NumberFormatException ex) {
			Toast.makeText(MeasBlocksActivity.this, getString(R.string.wrong_number), Toast.LENGTH_LONG).show();
			return;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meas_blocks, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Handler getHandler() {
		return mHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG.debug("onCreate");
		setContentView(R.layout.activity_meas_blocks);
		setButtonOnClickListner((ViewGroup) findViewById(R.id.measBlocksLayout), this);
		ecu = getIntent().getExtras().getString(ControllerActivity.ECU);
		String fileName = labelService.getLabelFileName(ecu);
		labels = labelService.getLabels(fileName);
	}

	/**
	 * getGroup1.
	 * @return group
	 */
	private int getGroup1() {
		EditText text = (EditText) findViewById(R.id.groupInput1);
		return Integer.parseInt(text.getText().toString());
	}

	/**
	 * getGroup2.
	 * @return group
	 */
	private int getGroup2() {
		EditText text = (EditText) findViewById(R.id.group2);
		return Integer.parseInt(text.getText().toString());
	}

	/**
	 * proceedMessage.
	 * @param message buffer
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
	private void proceedMessage(final byte[] message) throws ControllerCommunicationException, ControllerWrongResponseException {
		DataStreamDTO[] dtos = bufferService.getMeasBlocksInfo(message);
		if (dtos != null) {
			if (dataList.size() > MAX_DATA_LIST_SIZE) {
				dataList.removeFirst();
			}
			float[] fs = new float[4];
			for (int i = 0; i < 4; i++) {
				fs[i] = dtos[i].getValueFloat();
			}
			dataList.addLast(fs);
			((TextView) findViewById(R.id.block11)).setText(dtos[0].getValue() + dtos[0].getUnit());
			((TextView) findViewById(R.id.block12)).setText(dtos[1].getValue() + dtos[1].getUnit());
			((TextView) findViewById(R.id.block13)).setText(dtos[2].getValue() + dtos[2].getUnit());
			((TextView) findViewById(R.id.block14)).setText(dtos[3].getValue() + dtos[3].getUnit());
		}
	}

	/**
	 * setLabels.
	 */
	private void setLabels() {
		if (group != null) {
			((TextView) findViewById(R.id.group1Title)).setText(labels.get(group, LabelDTO.getDefaultLabelDTO(this)).getTitle());
			((TextView) findViewById(R.id.block11Label)).setText(labels.get(group, LabelDTO.getDefaultLabelDTO(this)).getGroup()[0]
					.getTitle());
			((TextView) findViewById(R.id.block12Label)).setText(labels.get(group, LabelDTO.getDefaultLabelDTO(this)).getGroup()[1]
					.getTitle());
			((TextView) findViewById(R.id.block13Label)).setText(labels.get(group, LabelDTO.getDefaultLabelDTO(this)).getGroup()[2]
					.getTitle());
			((TextView) findViewById(R.id.block14Label)).setText(labels.get(group, LabelDTO.getDefaultLabelDTO(this)).getGroup()[3]
					.getTitle());
		}

	}

}
