package com.vagm.vagmdroid.activities;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roboguice.inject.InjectView;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.vagm.vagmdroid.service.LabelService;
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
	 * group.
	 */
	private Integer group;

	/**
	 * MAX_DATA_LIST_SIZE.
	 */
	private static final int MAX_DATA_LIST_SIZE = 1000;

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
	 * spinner1.
	 */
	@InjectView(value = R.id.spinner)
	private MultiSelectionSpinner spinner;
	
	/**
	 * blocks.
	 */
	private boolean[] blocks;

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
		group = getIntent().getExtras().getInt(MeasBlocksActivity.GROUP);
		//group = 1;
		labels = labelService.getLabels();
		initSpinners();
		setButtonOnClickListner((ViewGroup) findViewById(R.id.graphicLayout), this);
		spinner.setOnClickListener(this);
		


       /* new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					timeSeries[0].add(new Date(), Math.cos((double) i / 10));
					timeSeries[1].add(new Date(), Math.sin((double) i / 10));
					timeSeries[2].add(new Date(), Math.tan((double) i / 10));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mChartView.repaint();
				}
			}
		}).start();*/


	}

	/**
	 * initSpinner.
	 */
	private void initSpinners() {
		spinner.setItems(new String[] {labels.get(group, new LabelDTO(this)).getGroup()[0].getTitle(),
				labels.get(group, new LabelDTO(this)).getGroup()[1].getTitle(),
				labels.get(group, new LabelDTO(this)).getGroup()[2].getTitle(),
				labels.get(group, new LabelDTO(this)).getGroup()[3].getTitle() });
	}

	/**
	 * initGraph.
	 */
	private void initGraph(boolean[] blocks) {
		int length = 0;
		for (boolean b:blocks) {
			if (b) {
				length++;
			}
		}
		// create dataset and renderer
		int[] colors = new int[] {Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN };
		PointStyle[] styles = new PointStyle[] {PointStyle.POINT, PointStyle.POINT, PointStyle.POINT, PointStyle.POINT };

		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(2);
		setRenderer(renderer, colors, styles, length);

		for (int i = 0; i < renderer.getSeriesRendererCount(); i++) {
			XYSeriesRenderer r = (XYSeriesRenderer) renderer.getSeriesRendererAt(i);
			r.setLineWidth(3f);
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		timeSeries = new ScaleTimeSeries[length];
		int id = 0;
		for (int i = 0; i < blocks.length; i += 2) {
			if (!blocks[i] && !blocks[i + 1]) {
				continue;
			} else {
				timeSeries[id] = new ScaleTimeSeries(labels.get(group, new LabelDTO(this)).getGroup()[i / 2].getTitle(), blocks[i] ? 0
						: 1);
				id++;
			}
		}

		for (int i = 0; i < length; i++) {
			 dataset.addSeries(timeSeries[i]);
		}

        mChartView = ChartFactory.getTimeChartView(this, dataset, renderer, "H:mm:ss");
        chartContainer.removeAllViews();
        chartContainer.addView(mChartView);
        mChartView.repaint();
	}

	/**
	 * setRenderer.
	 * @param renderer renderer
	 * @param colors colors
	 * @param styles styles
	 */
	private void setRenderer(final XYMultipleSeriesRenderer renderer, final int[] colors, final PointStyle[] styles, int length) {
		renderer.setChartTitle(labels.get(group, new LabelDTO(this)).getTitle());
		renderer.setXTitle(getString(R.string.x_axis_title));
		renderer.setYAxisAlign(Align.RIGHT, 1);
	    renderer.setYLabelsAlign(Align.LEFT, 1);
	    renderer.setYLabelsAlign(Align.RIGHT, 0);
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		//renderer.setMargins(new int[] {20, 30, 15, 20 });
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
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
			
			if (mChartView != null) {
				int id = 0;
				for (int i = 0; i < 4; i++) {
					fs[i] = dtos[i].getValueFloat();
					if (blocks[i * 2] || blocks[i * 2 + 1]) {
						timeSeries[id].add(new Date(), dtos[i].getValueFloat());
						id++;
					}
				}
				mChartView.repaint();
			}
			
		}

	}

	private void paint() {
		int z[] = { 0, 1, 2, 3, 4, 5, 6, 7 };
		int x[] = { 10, 18, 32, 21, 48, 60, 53, 80 };

		XYSeries xSeries = new XYSeries("X Series");

		for (int i = 0; i < z.length; i++) {
			xSeries.add(z[i], x[i]);
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(xSeries);

		XYSeriesRenderer Xrenderer = new XYSeriesRenderer();
		Xrenderer.setColor(Color.GREEN);
		Xrenderer.setPointStyle(PointStyle.DIAMOND);
		Xrenderer.setDisplayChartValues(true);
		Xrenderer.setLineWidth(2);
		Xrenderer.setFillPoints(true);

		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.setChartTitle("X Vs Y Chart");
		mRenderer.setXTitle("X Values");
		mRenderer.setYTitle("Y Values");
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setXLabels(0);
		mRenderer.setPanEnabled(false);
		mRenderer.setShowGrid(true);
		mRenderer.setClickEnabled(true);

		mRenderer.addSeriesRenderer(Xrenderer);

		GraphicalView mChartView = ChartFactory.getLineChartView(getBaseContext(), dataset, mRenderer);

		chartContainer.addView(mChartView);
		// mChartView.repaint();

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

		case MultiSelectionSpinner.OK_BUTTON_ID:
			if (blocks == null) {
				blocks = spinner.getBlocks();
			}
			synchronized (blocks) {
				blocks = spinner.getBlocks();
				LOG.debug("blocks {}", Arrays.toString(blocks));
				spinner.hideDialog();
				initGraph(blocks);
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
	private class ScaleTimeSeries extends XYSeries {

		/**
		 * serialVersionUID.
		 */
		private static final long serialVersionUID = 4020891366423167712L;

		/**
		 * constructor.
		 * @param title title
		 * @param scaleNumber scaleNumber
		 */
		public ScaleTimeSeries(final String title, final int scaleNumber) {
			super(title, scaleNumber);
		}

		/**
		 * add.
		 * @param x x
		 * @param y y
		 */
		public synchronized void add(final Date x, final double y) {
			super.add(x.getTime(), y);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected double getPadding(final double x) {
			return 1;
		}
	}

}
