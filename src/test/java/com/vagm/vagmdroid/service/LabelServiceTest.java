package com.vagm.vagmdroid.service;

import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO1;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO2;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO3;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowEnvironment;
import org.robolectric.shadows.ShadowLog;

import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import com.google.inject.Inject;
import com.vagm.vagmdroid.dto.LabelDTO;
import com.vagm.vagmdroid.dto.LabelDTO.Group;

/**
 * The Class LabelServiceTest.
 * 
 * @author Roman_Konovalov
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class LabelServiceTest {

    /**
     * GROUP1_TITLE.
     */
    public static final String GROUP1_TITLE = "Injected Quantity";

    /**
     * BLOCK1_TITLE.
     */
    public static final String BLOCK1_TITLE = "Engine speed";

    /**
     * BLOCK2_TITLE.
     */
    public static final String BLOCK2_TITLE = "Injection Quantity";

    /**
     * BLOCK3_TITLE.
     */
    public static final String BLOCK3_TITLE = "Mod. Piston Displ.";

    /**
     * BLOCK4_TITLE.
     */
    public static final String BLOCK4_TITLE = "Coolant Temp.";

    /**
     * ECU.
     */
    public static final String ECU = "028906021GL";

    /**
     * LABEL_FILE.
     */
    public static final String LABEL_FILE = "028-906-021-AHU.lbl";

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

    {
        ShadowLog.stream = System.out;
    }

    /**
     * testGetLabelFileName.
     */
    @Test
    public final void testGetLabelFileName1() {
        assertEquals("1K0-959-704-GEN2.lbl", labelService.getLabelFileName("1K0959704E"));
    }

    /**
     * testGetLabelFileName2.
     */
    @Test
    public final void testGetLabelFileName2() {
        assertEquals("1K0-920-xxx-17.lbl", labelService.getLabelFileName("1P0920A23"));
    }

    /**
     * testGetLabelFileName3.
     */
    @Test
    public final void testGetLabelFileName3() {
        assertEquals("3C0-959-760.lbl", labelService.getLabelFileName("1T0959701Z"));
    }

    /**
     * testGetLabelFileName4.
     */
    @Test
    public final void testGetLabelFileName4() {
        assertEquals("028-906-021-AHU.lbl", labelService.getLabelFileName(ECU_INFO[1]));
    }

    /**
     * testGetLabelFileName5.
     */
    @Test
    public final void testGetLabelFileName5() {
        assertEquals("8E0-614-111-EDS.lbl", labelService.getLabelFileName(ECU_INFO1[1]));
    }

    /**
     * testGetLabelFileName6.
     */
    @Test
    public final void testGetLabelFileName6() {
        assertEquals("3B0-919-xxx-17.lbl", labelService.getLabelFileName(ECU_INFO2[1]));
    }

    /**
     * testGetLabelFileName7.
     */
    @Test
    public final void testGetLabelFileName7() {
        assertEquals("3B0-959-79x-46.lbl", labelService.getLabelFileName(ECU_INFO3[1]));
    }

    /**
     * testGetLabels.
     * 
     * @throws IOException
     *             IOException
     */
    @Test
    public final void testGetLabels() throws IOException {
        copyFiles();
        String fileName = labelService.getLabelFileName(ECU);
        SparseArray<LabelDTO> array = labelService.getLabels(fileName);
        for (int i = 0; i < array.size(); i++) {
            Log.d("TEST", array.valueAt(i).toString());
        }
        assertEquals("Injected Quantity", array.valueAt(0).getTitle());
        Group group = array.valueAt(0).getGroup()[0];
        assertEquals("Engine speed", group.getTitle());
        assertEquals("861-945", group.getDescription());
        assertEquals(15, array.size());
    }

    /**
     * testAllLabels.
     * 
     * @throws IOException
     *             IOException
     */
    @Test
    public final void testAllLabels() throws IOException {
        copyFiles();
        String[] fileNames = getAllFileNames();
        for (String fileName : fileNames) {
            if (fileName.length() < 10) {
                continue;
            }
            Log.d("TEST", fileName + ": records count: " + labelService.getLabels(fileName).size());
        }
    }

    /**
     * getAllFileNames.
     * 
     * @return AllFileNames
     * @throws IOException
     *             IOException
     */
    private String[] getAllFileNames() throws IOException {
        File file = new File("assets" + File.separator + "labels");
        if (file.isDirectory()) {
            return file.list();
        }
        return null;
    }

    /**
     * copyFiles.
     * 
     * @throws IOException
     *             IOException
     */
    private void copyFiles() throws IOException {
        File file = new File("assets" + File.separator + "labels");
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File destinationDirectory = new File(ShadowEnvironment.getExternalStorageDirectory(), File.separator + propertyService.getAppName()
                + File.separator + "labels");
        destinationDirectory.mkdirs();
        for (String fileName : file.list()) {
            File destinationFile = new File(destinationDirectory, fileName);
            destinationFile.createNewFile();
            FileUtils.copyFile(new File("assets" + File.separator + "labels" + File.separator + fileName), destinationFile);
        }
    }

}
