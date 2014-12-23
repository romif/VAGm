package com.vagm.vagmdroid.activities;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
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
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.dto.DataStreamDTO;
import com.vagm.vagmdroid.dto.LabelDTO;
import com.vagm.vagmdroid.exceptions.ControllerWrongResponseException;
import com.vagm.vagmdroid.service.BluetoothService;
import com.vagm.vagmdroid.service.BufferService;
import com.vagm.vagmdroid.service.ControllerInfoService;
import com.vagm.vagmdroid.service.LabelService;
import com.vagm.vagmdroid.util.ChartUtil;
import com.vagm.vagmdroid.widget.ChartSettingsButton;

/**
 * The Class GraphicActivity.
 * 
 * @author Roman_Konovalov
 */
public class GraphicActivity extends CustomAbstractActivity implements OnClickListener {

    /**
     * The Class ScaleTimeSeries.
     * 
     * @author Roman_Konovalov
     */
    public static class ScaleTimeSeries extends TimeSeries {

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
         * 
         * @param title
         *            title
         */
        public ScaleTimeSeries(final String title) {
            super(title);
        }

        /**
         * Gets Date.
         * 
         * @param index
         *            index
         * @return Date
         */
        public Date getDate(final int index) {
            return new Date((long) getX(index));
        }

        /**
         * Sets ScaleNumber.
         * 
         * @param scaleNumber
         *            scaleNumber
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
    private final Deque<float[]> dataList = new LinkedList<>();

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
    @InjectView(value = R.id.bChartSettings)
    private ChartSettingsButton bChartSettings;

    /**
     * blocks.
     */
    private boolean[] blocks = { true, false, false, false, false, false, false, false };

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
    private ChartUtil chartUtil;

    /**
     * bluetoothService.
     */
    @Inject
    private BluetoothService bluetoothService;

    /**
     * isRocord.
     */
    private boolean isRecording;

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
                isRecording = !isRecording;
                if (isRecording) {
                    bRec.setText(getString(R.string.stop));
                } else {
                    bRec.setText(getString(R.string.start));
                }
                break;
    
            case R.id.bSave:
                chartUtil.saveChart(timeSeries, blocksCount);
                break;
    
            case ChartSettingsButton.OK_BUTTON_ID:
            synchronized (blocks) {
                blocks = bChartSettings.getBlocks();
                LOG.debug("blocks {}", Arrays.toString(blocks));
                bChartSettings.hideDialog();
                initGraph();
                repaint();
            }
                break;
    
            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration myConfig) {
        super.onConfigurationChanged(myConfig);
        int orient = getResources().getConfiguration().orientation;
        switch (orient) {
            case Configuration.ORIENTATION_LANDSCAPE:
                graphicActivityControl.setVisibility(View.GONE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                graphicActivityControl.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        int orient = getResources().getConfiguration().orientation;
        if (orient == Configuration.ORIENTATION_LANDSCAPE) {
            graphicActivityControl.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    graphicActivityControl.setVisibility(View.GONE);
                }
            }, 3000);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (controllerInfoService.getGroup() < 0xFF) {
                controllerInfoService.setGroup(controllerInfoService.getGroup() + 1);
                LOG.debug("Request for group {}", controllerInfoService.getGroup());
                bluetoothService.write(controllerInfoService.getGroup());
                timeSeries = null;
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (controllerInfoService.getGroup() > 0x01) {
                controllerInfoService.setGroup(controllerInfoService.getGroup() - 1);
                LOG.debug("Request for group {}", controllerInfoService.getGroup());
                bluetoothService.write(controllerInfoService.getGroup());
                timeSeries = null;
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOG.debug("onCreate");
        setContentView(R.layout.activity_graphic);
        /*
         * controllerInfoService.setGroup(1);
         * controllerInfoService.setVagNumber("028906021GL");
         */
        labels = labelService.getLabels();
        setButtonOnClickListner((ViewGroup) findViewById(R.id.graphicLayout), this);
        bChartSettings.setEnabled(false);
        bRec.setText(getString(R.string.stop));
        isRecording = true;

        // test();

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
            if (blocks[i]) {
                timeSeries[i / 2].setScaleNumber(blocks[i + 1] ? 1 : 0);
                dataset.addSeries(timeSeries[i / 2]);
                length++;
            }
        }

        chartUtil.setRenderer(renderer, labels.get(controllerInfoService.getGroup(), new LabelDTO(this)).getTitle(),
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
     * initSpinners.
     * 
     * @param blockCount
     *            blockCount
     */
    private void initSpinners(final int blockCount) {
        String[] items = new String[blockCount];
        for (int i = 0; i < items.length; i++) {
            items[i] = labels.get(controllerInfoService.getGroup(), new LabelDTO(this)).getGroup()[i].getTitle();
        }
        bChartSettings.setItems(items);
        bChartSettings.setBlocks(blocks);
    }

    /**
     * initTimeSeries.
     * 
     * @param blockCount
     *            blockCount
     */
    private void initTimeSeries(final int blockCount) {
        timeSeries = new ScaleTimeSeries[blockCount];
        for (int i = 0; i < blockCount; i++) {
            timeSeries[i] = new ScaleTimeSeries(labels.get(controllerInfoService.getGroup(), new LabelDTO(this)).getGroup()[i].getTitle());
        }
    }

    /**
     * proceedMessage.
     * 
     * @param message
     *            buffer
     * @throws ControllerWrongResponseException
     *             if wrong response from controller occurs
     */
    protected void proceedMessage(final byte[] message) throws ControllerWrongResponseException {
        DataStreamDTO[] dtos = bufferService.getMeasBlocksInfo(message);

        if (timeSeries == null) {
            int blocksCount1 = 0; //TODO
            for (DataStreamDTO dto : dtos) {
                if (!dto.getValue().equals(getString(R.string.no_data))) {
                    blocksCount1++;
                }
            }
            this.blocksCount = blocksCount1;
            initSpinners(blocksCount1);
            initTimeSeries(blocksCount1);
            bChartSettings.setEnabled(true);
            initGraph();
        }

        if (dtos != null && isRecording) {
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

        renderer.setRange(new double[] { minX, maxX, range[2], range[3] });
        mChartView.repaint();
    }

/*    private void test() {
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

    }*/

}
