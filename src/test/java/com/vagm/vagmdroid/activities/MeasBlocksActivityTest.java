package com.vagm.vagmdroid.activities;

import static com.vagm.vagmdroid.service.LabelServiceTest.BLOCK1_TITLE;
import static com.vagm.vagmdroid.service.LabelServiceTest.BLOCK2_TITLE;
import static com.vagm.vagmdroid.service.LabelServiceTest.BLOCK3_TITLE;
import static com.vagm.vagmdroid.service.LabelServiceTest.BLOCK4_TITLE;
import static com.vagm.vagmdroid.service.LabelServiceTest.GROUP1_TITLE;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_MEAS_BLOCKS_4GROUPS;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY1;
import static com.vagm.vagmdroid.service.TestConstatnts.BUFFER_STRING_ARRAY_NEGATIVE;
import static com.vagm.vagmdroid.service.TestConstatnts.DATA_STREAM_DTOS4;
import static com.vagm.vagmdroid.service.TestConstatnts.hexStringToByteArray;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowEnvironment;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;
import com.vagm.vagmdroid.service.BluetoothService.ServiceCommand;
import com.vagm.vagmdroid.service.ControllerInfoService;
import com.vagm.vagmdroid.service.LabelService;
import com.vagm.vagmdroid.service.LabelServiceTest;
import com.vagm.vagmdroid.service.PropertyService;

/**
 * The Class MeasBlocksActivityTest.
 * 
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
     * labelService.
     */
    @Inject
    private LabelService labelService;

    /**
     * context.
     */
    @Inject
    private Context context;

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
     * bGo1.
     */
    private Button bGo1;

    /**
     * bGo2.
     */
    private Button bGo2;

    /**
     * bUp1.
     */
    private Button bUp1;

    /**
     * bDn1.
     */
    private Button bDn1;

    /**
     * groupInput1.
     */
    private TextView groupInput1;

    /**
     * bUp2.
     */
    private Button bUp2;

    /**
     * bDn2.
     */
    private Button bDn2;

    /**
     * groupInput2.
     */
    private TextView groupInput2;

    /**
     * controllerInfoService.
     */
    @Inject
    private ControllerInfoService controllerInfoService;

    /**
     * setUp.
     * 
     * @throws IOException
     *             IOException
     */
    @Before
    public void setUp() throws IOException {
        ShadowLog.stream = System.out;
        controllerInfoService.setVagNumber(LabelServiceTest.ECU);
        copyFile();
        Intent intent = new Intent(Robolectric.getShadowApplication().getApplicationContext(), ControllerActivity.class);
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
        bGo1 = (Button) activity.findViewById(R.id.bGo1);
        bUp1 = (Button) activity.findViewById(R.id.bUp1);
        bDn1 = (Button) activity.findViewById(R.id.bDn1);
        bGo2 = (Button) activity.findViewById(R.id.bGo2);
        bUp2 = (Button) activity.findViewById(R.id.bUp2);
        bDn2 = (Button) activity.findViewById(R.id.bDn2);
        groupInput1 = (TextView) activity.findViewById(R.id.groupInput1);
        groupInput2 = (TextView) activity.findViewById(R.id.groupInput2);

    }

    /**
     * testGetHandler.
     * 
     * @throws IOException
     *             IOException
     */
    @SuppressLint("NewApi")
    @Test
    public final void testGetHandler() throws IOException {
        assertThat(groupInput1.getText().toString(), equalTo("001"));
        assertThat(groupInput2.getText().toString(), equalTo("001"));
        assertThat(group1Title.getText().toString(), equalTo(context.getText(R.string.group1)));

        bGo1.callOnClick();
        byte[] buffer = hexStringToByteArray(BUFFER_MEAS_BLOCKS_4GROUPS);
        activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

        assertThat(block11.getText().toString(), equalTo(DATA_STREAM_DTOS4[0].getValue() + DATA_STREAM_DTOS4[0].getUnit()));
        assertThat(block12.getText().toString(), equalTo(DATA_STREAM_DTOS4[1].getValue() + DATA_STREAM_DTOS4[1].getUnit()));
        assertThat(block13.getText().toString(), equalTo(DATA_STREAM_DTOS4[2].getValue() + DATA_STREAM_DTOS4[2].getUnit()));
        assertThat(block14.getText().toString(), equalTo(DATA_STREAM_DTOS4[3].getValue() + DATA_STREAM_DTOS4[3].getUnit()));
        assertThat(group1Title.getText().toString(), equalTo(GROUP1_TITLE));
        assertThat(block11Label.getText().toString(), equalTo(BLOCK1_TITLE));
        assertThat(block12Label.getText().toString(), equalTo(BLOCK2_TITLE));
        assertThat(block13Label.getText().toString(), equalTo(BLOCK3_TITLE));
        assertThat(block14Label.getText().toString(), equalTo(BLOCK4_TITLE));
    }

    /**
     * testGetHandlerNegative.
     * 
     * @throws IOException
     */
    @Test
    public final void testGetHandlerNegative() {
        byte[] buffer = hexStringToByteArray(BUFFER_STRING_ARRAY1[1]);
        activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();
    }

    /**
     * testHandleMessageConnectionLost.
     */
    @Test
    public final void testHandleMessageConnectionLost() {
        activity.getHandler().obtainMessage(ServiceCommand.CONNECTION_LOST.ordinal(), -1, -1).sendToTarget();

        // postcondition
        Toast toast = ShadowToast.getLatestToast();
        assertNotNull("Should be error dialog", toast);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.connection_lost)));
    }

    /**
     * testHandleMessageControllerNotAnswer.
     */
    @Test
    public final void testHandleMessageControllerNotAnswer() {
        byte[] buffer = hexStringToByteArray(BUFFER_STRING_ARRAY_NEGATIVE[2]);
        activity.getHandler().obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), buffer.length, -1, buffer).sendToTarget();

        // postcondition
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull("Should be error dialog", alert);
        ShadowAlertDialog sAlert = Robolectric.shadowOf(alert);
        assertThat(sAlert.getMessage().toString(), equalTo(activity.getString(R.string.controller_not_answer)));
    }

    /**
     * testButtonsUp1Click.
     */
    @Test
    @SuppressLint("NewApi")
    public final void testButtons1Click() {
        bUp1.callOnClick();
        assertThat(groupInput1.getText().toString(), equalTo("002"));
        bDn1.callOnClick();
        assertThat(groupInput1.getText().toString(), equalTo("001"));
        bDn1.callOnClick();
        assertThat(groupInput1.getText().toString(), equalTo("001"));
        groupInput1.setText("255");
        bUp1.callOnClick();
        assertThat(groupInput1.getText().toString(), equalTo("255"));
    }

    /**
     * testButtonsUp1Click.
     */
    @Test
    @SuppressLint("NewApi")
    public final void testButtons2Click() {
        bUp2.callOnClick();
        assertThat(groupInput2.getText().toString(), equalTo("002"));
        bDn2.callOnClick();
        assertThat(groupInput2.getText().toString(), equalTo("001"));
        bDn2.callOnClick();
        assertThat(groupInput2.getText().toString(), equalTo("001"));
        groupInput2.setText("255");
        bUp2.callOnClick();
        assertThat(groupInput2.getText().toString(), equalTo("255"));
    }

    /**
     * copyFile.
     * 
     * @throws IOException
     *             IOException
     */
    private void copyFile() throws IOException {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File destinationDirectory = new File(ShadowEnvironment.getExternalStorageDirectory(), File.separator + "VAGm" + File.separator
                + "labels");
        destinationDirectory.mkdirs();
        String destinationFileName = labelService.getLabelFileName(LabelServiceTest.ECU);
        File destinationFile = new File(destinationDirectory, destinationFileName);
        destinationFile.createNewFile();
        FileUtils.copyFile(new File("assets" + File.separator + "labels" + File.separator + destinationFileName), destinationFile);
    }

    /**
     * testIncorrectInput.
     */
    @SuppressLint("NewApi")
    @Test
    public final void testIncorrectInput() {
        groupInput1.setText("qwerty");
        bGo1.callOnClick();
        Toast toast = ShadowToast.getLatestToast();
        assertNotNull("Should be error dialog", toast);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.wrong_number)));

        groupInput2.setText("qwerty");
        bGo2.callOnClick();
        toast = ShadowToast.getLatestToast();
        assertNotNull("Should be error dialog", toast);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(activity.getString(R.string.wrong_number)));
    }

}
