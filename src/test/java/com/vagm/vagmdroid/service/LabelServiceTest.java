package com.vagm.vagmdroid.service;

import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO1;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO2;
import static com.vagm.vagmdroid.service.TestConstatnts.ECU_INFO3;

import java.io.File;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.util.SparseArray;

import com.google.inject.Inject;
import com.vagm.vagmdroid.dto.LabelDTO;

/**
 * The Class LabelServiceTest.
 * @author Roman_Konovalov
 */
public class LabelServiceTest extends AndroidTestCase {

	/**
	 * GROUP1_TITLE.
	 */
	public static final String GROUP1_TITLE = "Current Values (Longitudinal && Backrest Rake Adjustment)";

	/**
	 * BLOCK1_TITLE.
	 */
	public static final String BLOCK1_TITLE = "Longitudinal Seat";

	/**
	 * BLOCK2_TITLE.
	 */
	public static final String BLOCK2_TITLE = "Longitudinal Seat";

	/**
	 * BLOCK3_TITLE.
	 */
	public static final String BLOCK3_TITLE = "Backrest Rake";

	/**
	 * BLOCK4_TITLE.
	 */
	public static final String BLOCK4_TITLE = "Backrest Rake";

	/**
	 * ECU.
	 */
	public static final String ECU = "028906021GL";

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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * testGetLabelFileName.
	 */
	public final void testGetLabelFileName1() {
		assertEquals("1K0-959-704-GEN2.lbl", labelService.getLabelFileName("1K0959704E"));
	}

	/**
	 * testGetLabelFileName2.
	 */
	public final void testGetLabelFileName2() {
		assertEquals("1K0-920-xxx-17.lbl", labelService.getLabelFileName("1P0920A23"));
	}

	/**
	 * testGetLabelFileName3.
	 */
	public final void testGetLabelFileName3() {
		assertEquals("3C0-959-760.lbl", labelService.getLabelFileName("1T0959701Z"));
	}

	/**
	 * testGetLabelFileName4.
	 */
	public final void testGetLabelFileName4() {
		assertEquals("028-906-021-AHU.lbl", labelService.getLabelFileName(ECU_INFO[1]));
	}

	/**
	 * testGetLabelFileName5.
	 */
	public final void testGetLabelFileName5() {
		assertEquals("8E0-614-111-EDS.lbl", labelService.getLabelFileName(ECU_INFO1[1]));
	}

	/**
	 * testGetLabelFileName6.
	 */
	public final void testGetLabelFileName6() {
		assertEquals("3B0-919-xxx-17.lbl", labelService.getLabelFileName(ECU_INFO2[1]));
	}

	/**
	 * testGetLabelFileName7.
	 */
	public final void testGetLabelFileName7() {
		assertEquals("3B0-959-79x-46.lbl", labelService.getLabelFileName(ECU_INFO3[1]));
	}

	/**
	 * testGetLabels.
	 */
	public final void testGetLabels() {
		String fileNAme = labelService.getLabelFileName(ECU);
		SparseArray<LabelDTO> array = labelService.getLabels(fileNAme);
		for (int i = 0; i < array.size(); i++) {
			Log.d("TEST", array.valueAt(i).toString());
		}
		assertEquals(11, array.size());
	}

	/**
	 * testAllLabels.
	 */
	@LargeTest
	public final void testAllLabels() {
		String[] fileNames = getAllFileNames();
		for (String fileName:fileNames) {
			if (fileName.length() < 10) {
				continue;
			}
			Log.d("TEST", fileName + ": records count: " + labelService.getLabels(fileName).size());
		}
	}

	/**
	 * getAllFileNames.
	 * @return AllFileNames
	 */
	private String[] getAllFileNames() {
		File file = new File(Environment.getExternalStorageDirectory() + File.separator + propertyService.getAppName() + "/labels");
		if (file.isDirectory()) {
			return file.list();
		}
		return null;
	}

}
