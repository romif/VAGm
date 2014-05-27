package com.vagm.vagmdroid.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import android.content.Context;

import com.google.inject.Inject;
import com.vagm.vagmdroid.R;

/**
 * The Class FaultCodesServiceTest.
 * @author roman_konovalov
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class FaultCodesServiceTest {

	/**
	 * faultCodesService.
	 */
	@Inject
	private FaultCodesService faultCodesService;

	/**
	 * context.
	 */
	@Inject
	private Context context;

	{
		ShadowLog.stream = System.out;
	}

	/**
	 * testGetDTC.
	 */
	@Test
	public final void testGetDTC() {
		assertEquals("Окончание вывода", faultCodesService.getDTC(0));
		assertEquals("Указатель емкости тяговой АКБ", faultCodesService.getDTC(751));
		assertEquals("\"Клавиша регулировки положения подголовника водительского сиденья, вверх-E214\"", faultCodesService.getDTC(1011));

		assertEquals("Инициализация не выполнена", faultCodesService.getDTC(1507));
		assertEquals("Лампы стоп-сигнала прицепа", faultCodesService.getDTC(2562));
		assertEquals("Расходомеры воздуха 1/2: недостоверный сигнал измерения нагрузки", faultCodesService.getDTC(17417));
		assertEquals("Ошибка в системе рециркуляции ОГ", faultCodesService.getDTC(18845));

	}

	/**
	 * testGetDTCNegative.
	 */
	@Test
	public final void testGetDTCNegative() {
		assertEquals(context.getString(R.string.unknown_error), faultCodesService.getDTC(-1));
	}

	/**
	 * testGetErrorType.
	 */
	@Test
	public final void testGetErrorType() {
		assertEquals("сигнал на плюс", faultCodesService.getErrorType(1));
		assertEquals("подключен неправильно", faultCodesService.getErrorType(47));
		assertEquals("коррекция не выполнена", faultCodesService.getErrorType(194));
		assertEquals("", faultCodesService.getErrorType(195));
	}

}
