package com.vagm.vagmdroid.service;

import com.google.inject.Inject;
import com.vagm.vagmdroid.dto.DataStreamDTO;

import android.test.AndroidTestCase;
import android.util.Log;

/**
 * The Class DataStreamServiceTest.
 * @author Roman_Konovalov
 */
public class DataStreamServiceTest extends AndroidTestCase {

	/**
	 * DATA_STREAM_DTO.
	 */
	private static final DataStreamDTO DATA_STREAM_DTO = new DataStreamDTO("0.018", " Deg", 0.018f);

	/**
	 * DATA_STREAM_DTO_NA37_FIRST.
	 */
	private static final DataStreamDTO DATA_STREAM_DTO_NA37_FIRST = new DataStreamDTO("Идет адаптация", "", 0.0f);

	/**
	 * DATA_STREAM_DTO_NA37_MEDIUM.
	 */
	private static final DataStreamDTO DATA_STREAM_DTO_NA37_MEDIUM = new DataStreamDTO("\\Zl\\L-Рег.ВКЛ", "", 0.0f);

	/**
	 * DATA_STREAM_DTO_NA37_LAST.
	 */
	private static final DataStreamDTO DATA_STREAM_DTO_NA37_LAST = new DataStreamDTO("извлечен", "", 0.0f);

	/**
	 * dataStreamService.
	 */
	@Inject
	private DataStreamService dataStreamService;

	/**
	 * testEncodeGroupData.
	 */
	public final void testEncodeGroupData() {
		DataStreamDTO dto = dataStreamService.encodeGroupData(3, 3, 3);
		assertEquals(DATA_STREAM_DTO, dto);
	}

	/**
	 * testEncodeGroupDataNA37.
	 */
	public final void testEncodeGroupDataNA37() {
		DataStreamDTO dto = dataStreamService.encodeGroupData(37, 3, 1);
		assertEquals(DATA_STREAM_DTO_NA37_FIRST, dto);
		dto = dataStreamService.encodeGroupData(37, 3, 4);
		assertEquals(DATA_STREAM_DTO_NA37_MEDIUM, dto);
		dto = dataStreamService.encodeGroupData(37, 3, 1287);
		assertEquals(DATA_STREAM_DTO_NA37_LAST, dto);
		Log.d("TEST", dataStreamService.encodeGroupData(3, 3, 3).toString());
	}

}
