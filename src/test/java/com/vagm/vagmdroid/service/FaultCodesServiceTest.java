package com.vagm.vagmdroid.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import com.google.inject.Inject;

/**
 * The Class FaultCodesServiceTest.
 * @author roman_konovalov
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "AndroidManifest.xml")
public class FaultCodesServiceTest {
	
	@Inject
	private FaultCodesService faultCodesService;
	
	{
		ShadowLog.stream = System.out;
	}

	/**
	 * Test method for {@link com.vagm.vagmdroid.service.FaultCodesService#getDTC(int)}.
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
	 * Test method for {@link com.vagm.vagmdroid.service.FaultCodesService#getErrorType(int)}.
	 */
	@Test
	public final void testGetErrorType() {
		assertEquals("сигнал на плюс", faultCodesService.getErrorType(1));
		assertEquals("подключен неправильно", faultCodesService.getErrorType(47));
		assertEquals("коррекция не выполнена", faultCodesService.getErrorType(194));
		assertEquals("", faultCodesService.getErrorType(195));
	}

}
