package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_MEAS_BLOCKS_4GROUPS;
import static com.vagm.vagmdroid.util.NumberUtil.hexStringToByteArray;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.util.ActivityController;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.ControllerInfoService;
import com.vagm.vagmdroid.service.LabelServiceTest;

/**
 * The Class GraphicActivityTest.
 * 
 * @author Roman_Konovalov
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class GraphicActivityTest {

    /**
     * activity.
     */
    private GraphicActivity activity;

    private ActivityController<GraphicActivity> activityController = Robolectric.buildActivity(GraphicActivity.class);;

    /**
     * controllerInfoService.
     */
    @Inject
    private ControllerInfoService controllerInfoService;

    /**
     * bClearCodes.
     */
    private Button bRec;

    /**
     * setUp.
     */
    @Before
    public void setUp() {
        ShadowLog.stream = System.out;
        controllerInfoService.setVagNumber(LabelServiceTest.ECU);
        controllerInfoService.setGroup(0);
        Intent intent = new Intent(Robolectric.getShadowApplication().getApplicationContext(), GraphicActivity.class);
        activity = activityController.withIntent(intent).create().get();
        bRec = (Button) activity.findViewById(R.id.bRec);
    }

    @Test
    public final void testCreateActivity() {
        assertThat(bRec.getText().toString()).isEqualTo(activity.getString(R.string.stop));
        assertThat((Button) activity.findViewById(R.id.bChartSettings)).isNotNull();
        assertThat((Button) activity.findViewById(R.id.bSave)).isNotNull();
        assertThat((Button) activity.findViewById(R.id.bGraphicBack)).isNotNull();
    }

    @Test
    public final void testLandscapeOrientation() throws InterruptedException {
        Robolectric.application.getResources().getConfiguration().orientation = Configuration.ORIENTATION_LANDSCAPE;
        Intent intent = new Intent(Robolectric.getShadowApplication().getApplicationContext(), GraphicActivity.class);
        activity = activityController.withIntent(intent).create().get();

        assertThat(activity.getResources().getConfiguration().orientation).isEqualTo(Configuration.ORIENTATION_LANDSCAPE);
        assertThat(activity.findViewById(R.id.graphicActivityControl).getVisibility()).isEqualTo(View.GONE);
    }

    @Test
    public final void testProceedMessage() {
        byte[] buffer = hexStringToByteArray(BUFFER_MEAS_BLOCKS_4GROUPS);
        activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

        assertThat(((LinearLayout) activity.findViewById(R.id.chart_container)).getChildAt(0)).isNotNull();
    }

}
