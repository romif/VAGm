package com.vagm.vagmdroid.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
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
	 * GROUP.
	 */
	public static final String GROUP = "group";

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
	private Integer group1;

	/**
	 * group.
	 */
	private Integer group2;

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
	 * groupInput1.
	 */
	@InjectView(R.id.groupInput1)
	private EditText groupInput1;

	/**
	 * groupInput1.
	 */
	@InjectView(R.id.groupInput2)
	private EditText groupInput2;

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
					LOG.error("No answer from controller", e);
					getControllerNotAnswerAlert().show();
				} catch (ControllerWrongResponseException e) {
					LOG.info(e.getMessage(), e);
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
				group1 = getGroup1();
				setGroup1();
				break;

			case R.id.bUp1:
				group1 = getGroup1();
				if (group1 < 0xFF) {
					group1++;
					setGroup1();
				}
				break;

			case R.id.bDn1:
				group1 = getGroup1();
				if (group1 > 1) {
					group1--;
					setGroup1();
				}
				break;

			case R.id.bGo2:
				group2 = getGroup2();
				setGroup2();
				break;

			case R.id.bUp2:
				group2 = getGroup2();
				if (group2 < 0xFF) {
					group2++;
					setGroup2();
				}
				break;

			case R.id.bDn2:
				group2 = getGroup2();
				if (group2 > 1) {
					group2--;
					setGroup2();
				}
				break;

			case R.id.bGraph:
				final Intent graphIntent = new Intent(this, GraphicActivity.class);
				graphIntent.putExtra(GROUP, group1);
				startActivityForResult(graphIntent, -1);
				break;

			case R.id.bMeasBlocksBack:
				LOG.debug("Exiting Controller Activity, writing exit command: {}", VAGmConstans.EXIT_COMMAND);
				bluetoothService.write(0xFF);
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
		return Integer.parseInt(groupInput1.getText().toString());
	}

	/**
	 * setGroup1.
	 */
	private void setGroup1() {
		groupInput1.setText(String.format("%03d", group1));
		LOG.debug("Request for group {}", group1);
		bluetoothService.write(group1);
		setLabels();
	}

	/**
	 * getGroup2.
	 * @return group
	 */
	private int getGroup2() {
		return Integer.parseInt(groupInput2.getText().toString());
	}

	/**
	 * setGroup1.
	 */
	private void setGroup2() {
		groupInput2.setText(String.format("%03d", group2));
		LOG.debug("Request for group {}", group2);
		bluetoothService.write(group2);
		setLabels();
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
		if (group1 != null) {
			((TextView) findViewById(R.id.group1Title)).setText(labels.get(group1, new LabelDTO(this)).getTitle());
			((TextView) findViewById(R.id.block11Label)).setText(labels.get(group1, new LabelDTO(this)).getGroup()[0].getTitle());
			((TextView) findViewById(R.id.block12Label)).setText(labels.get(group1, new LabelDTO(this)).getGroup()[1].getTitle());
			((TextView) findViewById(R.id.block13Label)).setText(labels.get(group1, new LabelDTO(this)).getGroup()[2].getTitle());
			((TextView) findViewById(R.id.block14Label)).setText(labels.get(group1, new LabelDTO(this)).getGroup()[3].getTitle());
		}
	}

}
