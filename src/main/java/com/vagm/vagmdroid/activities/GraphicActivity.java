package com.vagm.vagmdroid.activities;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.dto.DataStreamDTO;
import com.vagm.vagmdroid.dto.LabelDTO;
import com.vagm.vagmdroid.exceptions.ControllerCommunicationException;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.BufferService;
import com.vagm.vagmdroid.service.ControllerInfoService;
import com.vagm.vagmdroid.service.LabelService;
import com.vagm.vagmdroid.util.AChartUtil;
import com.vagm.vagmdroid.util.MultiSelectionSpinner;

/**
 * The Class GraphicActivity.
 * @author Roman_Konovalov
 */
public class GraphicActivity extends CustomAbstractActivity implements OnClickListener {

	/**
	 * LOG.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GraphicActivity.class);

	/**
	 * MAX_DATA_LIST_SIZE.
	 */
	private static final int MAX_DATA_LIST_SIZE = 1000;

	/**
	 * Visible chart length (seconds per 1000px).
	 */
	private static final int VISIBLE_CHART_LENGTH = 20;

	/**
	 * dataList.
	 */
	private final LinkedList<float[]> dataList = new LinkedList<>();

	/**
	 * labels.
	 */
	private SparseArray<LabelDTO> labels;

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
	 * chartContainer.
	 */
	@InjectView(value = R.id.chart_container)
	private LinearLayout chartContainer;

    /**
     * mChartView.
     */
    private GraphicalView mChartView;

    /**
	 * timeSeries.
	 */
	private ScaleTimeSeries[] timeSeries;

	/**
	 * spinner.
	 */
	@InjectView(value = R.id.spinner)
	private MultiSelectionSpinner spinner;

	/**
	 * blocks.
	 */
	private boolean[] blocks = {true, false, false, true, false, true, false, false};

	/**
	 * graphicActivityControl.
	 */
	@InjectView(R.id.graphicActivityControl)
	private LinearLayout graphicActivityControl;

	/**
	 * renderer.
	 */
	private XYMultipleSeriesRenderer renderer;

	/**
	 * aChartUtil.
	 */
	@Inject
	private AChartUtil aChartUtil;

	/**
	 * isRocord.
	 */
	private boolean isRocord = true;

	/**
	 * bRec.
	 */
	@InjectView(R.id.bRec)
	private Button bRec;

	/**
	 * blocksCount.
	 */
	private int blocksCount;

	/**
	 * controllerInfoService.
	 */
	@Inject
	private ControllerInfoService controllerInfoService;

	/**
	 * The Handler that gets information back from the BluetoothService.
	 */
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
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
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG.debug("onCreate");
		setContentView(R.layout.activity_graphic);
		/*controllerInfoService.setGroup(1);
		controllerInfoService.setVagNumber("028906021GL");*/
		labels = labelService.getLabels();
		setButtonOnClickListner((ViewGroup) findViewById(R.id.graphicLayout), this);
		spinner.setEnabled(false);
		spinner.setOnClickListener(this);
		
		//test();

	}

	/**
	 * initSpinners.
	 * @param blockCount blockCount
	 */
	private void initSpinners(final int blockCount) {
		String[] items = new String[blockCount];
		for (int i = 0; i < items.length; i++) {
			items[i] = labels.get(controllerInfoService.getGroup(), new LabelDTO(this)).getGroup()[i].getTitle();
		}
		spinner.setItems(items);
		spinner.setBlocks(blocks);
	}

	/**
	 * initTimeSeries.
	 * @param blockCount blockCount
	 */
	private void initTimeSeries(final int blockCount) {
		timeSeries = new ScaleTimeSeries[blockCount];
		for (int i = 0; i < blockCount; i++) {
			timeSeries[i] = new ScaleTimeSeries(labels.get(controllerInfoService.getGroup(), new LabelDTO(this)).getGroup()[i].getTitle());
		}
	}

	/**
	 * initGraph.
	 */
	private void initGraph() {
		// create dataset and renderer
		renderer = new XYMultipleSeriesRenderer(2);
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		int length = 0;
		for (int i = 0; i < blocks.length; i += 2) {
			if (blocks[i] || blocks[i + 1]) {
				timeSeries[i / 2].setScaleNumber(blocks[i] ? 0 : 1);
				dataset.addSeries(timeSeries[i / 2]);
				length++;
			}
		}

		aChartUtil.setRenderer(renderer, labels.get(controllerInfoService.getGroup(), new LabelDTO(this)).getTitle(),
				getString(R.string.x_axis_title), length);
		for (XYSeries series : dataset.getSeries()) {
			LOG.debug(String.valueOf(series.getScaleNumber()));
		}

		mChartView = ChartFactory.getTimeChartView(this, dataset, renderer, "H:mm:ss");
		chartContainer.removeAllViews();
		chartContainer.addView(mChartView);
		mChartView.repaint();
	}

	/**
	 * proceedMessage.
	 * @param message buffer
	 * @throws ControllerCommunicationException if some communication error occurs
	 * @throws ControllerWrongResponseException if wrong response from controller occurs
	 */
	private void proceedMessage(final byte[] message) throws ControllerCommunicationException, ControllerWrongResponseException {
		DataStreamDTO[] dtos = bufferService.getMeasBlocksInfo(message);

		if (timeSeries == null) {
			int blocksCount = 0;
			for (DataStreamDTO dto:dtos) {
				if (!dto.getValue().equals(getString(R.string.no_data))) {
					blocksCount++;
				}
			}
			this.blocksCount = blocksCount;
			initSpinners(blocksCount);
			initTimeSeries(blocksCount);
			spinner.setEnabled(true);
			initGraph();
		}

		if (dtos != null && isRocord) {
			if (dataList.size() > MAX_DATA_LIST_SIZE) {
				dataList.removeFirst();
			}
			float[] fs = new float[4];
			for (int i = 0; i < 4; i++) {
				fs[i] = dtos[i].getValueFloat();
			}
			dataList.addLast(fs);
			
			if (mChartView != null) {
				for (int i = 0; i < 4; i++) {
					fs[i] = dtos[i].getValueFloat();
					timeSeries[i].add(new Date(), dtos[i].getValueFloat());
				}
				repaint();
			}
			
		}

	}

	/**
	 * repaint.
	 */
	private void repaint() {
		double[] range = renderer.getInitialRange();
		double maxX = timeSeries[0].getMaxX();
		double minX = timeSeries[0].getMinX();
		int deltaX = VISIBLE_CHART_LENGTH * chartContainer.getWidth();
		minX = (maxX - deltaX) < minX ? minX : maxX - deltaX;

		renderer.setRange(new double[] {minX, maxX, range[2], range[3] });
		mChartView.repaint();
	}
	
	private void test() {
		byte[] buffer = bufferService.hexStringToByteArray("e701690027330015142405097c");
		getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					byte[] buffer = bufferService.hexStringToByteArray("e701690027330015142405097c");
					getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					buffer = bufferService.hexStringToByteArray("e701690127340015142505098c");
					getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

	}
	
	@Override
	public void onConfigurationChanged(Configuration myConfig) {
	    super.onConfigurationChanged(myConfig);
	    int orient = getResources().getConfiguration().orientation; 
	    switch(orient) {
	                case Configuration.ORIENTATION_LANDSCAPE:
	                    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	                	graphicActivityControl.setVisibility(View.GONE);
	                    break;
	                case Configuration.ORIENTATION_PORTRAIT:
	                	graphicActivityControl.setVisibility(View.VISIBLE);
	                    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	                    break;
	                default:
	                    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	                }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(final View v) {
		switch (v.getId()) {

		case R.id.bGraphicBack:
			LOG.debug("Exiting Graphic Activity");
			finish();
			break;

		case R.id.bRec:
			LOG.debug("Satrt/stop recording");
			isRocord = !isRocord;
			if (isRocord) {
				bRec.setText(getString(R.string.stop));
			} else {
				bRec.setText(getString(R.string.start));
			}
			break;

		case R.id.bSave:
			aChartUtil.saveChart(timeSeries, blocksCount);
			break;

		case MultiSelectionSpinner.OK_BUTTON_ID:
			synchronized (blocks) {
				blocks = spinner.getBlocks();
				LOG.debug("blocks {}", Arrays.toString(blocks));
				spinner.hideDialog();
				initGraph();
			}
			break;

		default:
			break;
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
	 * The Class ScaleTimeSeries.
	 * @author Roman_Konovalov
	 */
	public static class ScaleTimeSeries extends TimeSeries  {

		/**
		 * LOG.
		 */
		private static final Logger LOG = LoggerFactory.getLogger(ScaleTimeSeries.class);

		/**
		 * serialVersionUID.
		 */
		private static final long serialVersionUID = 4020891366423167712L;

		/**
		 * constructor.
		 * @param title title
		 */
		public ScaleTimeSeries(final String title) {
			super(title);
		}

		/**
		 * Gets Date.
		 * @param index index
		 * @return Date
		 */
		public Date getDate(final int index) {
			return new Date((long) getX(index));
		}

		/**
		 * Sets ScaleNumber.
		 * @param scaleNumber scaleNumber
		 */
		public void setScaleNumber(final int scaleNumber) {
			try {
				Field field = XYSeries.class.getDeclaredField("mScaleNumber");
				field.setAccessible(true);
				field.set(this, scaleNumber);
			} catch (Exception e) {
				LOG.error("Cannot set scaleNumber", e);
				throw new RuntimeException("Cannot set scaleNumber", e);
			}
		}
	}

}
