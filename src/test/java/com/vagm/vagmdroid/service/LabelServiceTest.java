package com.vagm.vagmdroid.service;

import android.test.AndroidTestCase;
import android.util.Log;
import android.util.SparseArray;

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
	public static final String ECU = "3C0959760";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		PropertyService.init();
	}

	/**
	 * testGetLabelFileName.
	 */
	public final void testGetLabelFileName1() {
		assertEquals("1K0-959-704-GEN2.lbl", LabelService.getLabelFileName("1K0959704E"));
	}

	/**
	 * testGetLabelFileName2.
	 */
	public final void testGetLabelFileName2() {
		assertEquals("1K0-920-xxx-17.lbl", LabelService.getLabelFileName("1P0920A23"));
	}

	/**
	 * testGetLabelFileName3.
	 */
	public final void testGetLabelFileName3() {
		assertEquals("3C0-959-760.lbl", LabelService.getLabelFileName("1T0959701Z"));
	}

	/**
	 * testGetLabels.
	 */
	public final void testGetLabels() {
		SparseArray<LabelDTO> array = LabelService.getLabels(ECU);
		for (int i=0; i<array.size();i++){
		Log.d("TEST", array.valueAt(i).toString());
		}
		assertEquals(10, array.size());
	}

}
