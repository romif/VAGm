package com.vagm.vagmdroid.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vagm.vagmdroid.R;

/**
 * The Class FaultCodesServer.
 * 
 * @author roman_konovalov
 */
@Singleton
public class FaultCodesService {

    private static final String DTC_FOLDER = "DTC";

    private static final String ASSETS_SOURCE = "assets";

    /**
     * LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FaultCodesService.class);

    /**
     * context.
     */
    @Inject
    private Context context;

    /**
     * getDTC.
     * 
     * @param errorCode
     *            errorCode
     * @return DTC
     */
    public String getDTC(final int errorCode) {
        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            if (errorCode < 1012) {
                inputStream = FaultCodesService.class.getClassLoader().getResourceAsStream(
                        ASSETS_SOURCE + File.separator + DTC_FOLDER + File.separator + "dtc.txt");
            } else {
                if (errorCode < 2012) {
                    inputStream = FaultCodesService.class.getClassLoader().getResourceAsStream(
                            ASSETS_SOURCE + File.separator + DTC_FOLDER + File.separator + "dtc1.txt");
                } else {
                    if (errorCode < 16361) {
                        inputStream = FaultCodesService.class.getClassLoader().getResourceAsStream(
                                ASSETS_SOURCE + File.separator + DTC_FOLDER + File.separator + "dtc2.txt");
                    } else {
                        if (errorCode < 17878) {
                            inputStream = FaultCodesService.class.getClassLoader().getResourceAsStream(
                                    ASSETS_SOURCE + File.separator + DTC_FOLDER + File.separator + "dtc3.txt");
                        } else {
                            inputStream = FaultCodesService.class.getClassLoader().getResourceAsStream(
                                    ASSETS_SOURCE + File.separator + DTC_FOLDER + File.separator + "dtc4.txt");
                        }
                    }
                }
            }
            reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

            String st;
            while (((st = reader.readLine()) != null)) {
                if (Integer.parseInt(st.substring(0, 5)) == errorCode) {
                    return st.substring(8);
                }
            }

        } catch (final IOException ex) {
            LOG.error("Cannot read NA37.txt", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.error("Cannot close BufferedReader", e);
                }
            }
        }

        return context.getString(R.string.unknown_error);
    }

    /**
     * getErrorType.
     * 
     * @param type
     *            type
     * @return ErrorType
     */
    public String getErrorType(final int type) {
        switch (type) {

            case 1:
                return "сигнал на плюс";
    
            case 2:
                return "сигнал на массу";
    
            case 3:
                return "нет сигнала";
    
            case 4:
                return "механическое повреждение";
    
            case 5:
                return "вход открыт";
    
            case 6:
                return "слишком высокий уровень сигнала";
    
            case 7:
                return "слишком низкий уровень сигнала";
    
            case 8:
                return "выход из диапазона регулирования (больше верхнего предела)";
    
            case 9:
                return "выход из диапазона адаптации (больше верхнего предела)";
    
            case 10:
                return "выход из диапазона адаптации (меньше нижнего предела)";
    
            case 11:
                return "выход из диапазона регулирования (меньше нижнего предела)";
    
            case 12:
                return "выход из диапазона адаптации больше верхнего предела (под нагрузкой)";
    
            case 13:
                return "выход из диапазона адаптации меньшего нижнего предела (под нагрузкой)";
    
            case 14:
                return "выход из диапазона адаптации больше верхнего предела (на холостых оборотах)";
    
            case 15:
                return "выход из диапазона адаптации меньшего нижнего предела (на холостых оборотах)";
    
            case 16:
                return "сигнал за пределами допуска";
    
            case 17:
                return "диапазон регулирования";
    
            case 18:
                return "верхнее предельное значение";
    
            case 19:
                return "нижнее предельное значение";
    
            case 20:
                return "ошибка базовой установки";
    
            case 21:
                return "давление спереди увеличивается слишком долго";
    
            case 22:
                return "давление спереди уменьшается слишком долго";
    
            case 23:
                return "давление сзади увеличивается слишком долго";
    
            case 24:
                return "давление сзади уменьшается слишком долго";
    
            case 25:
                return "неопределенное состояние переключателя";
    
            case 26:
                return "выход открыт";
    
            case 27:
                return "недостоверный сигнал";
    
            case 28:
                return "короткое замыкание на плюс";
    
            case 29:
                return "короткое замыкание на массу";
    
            case 30:
                return "обрыв цепи/короткое замыкание на плюс";
    
            case 31:
                return "обрыв цепи/короткое замыкание на массу";
    
            case 32:
                return "значение сопротивления слишком велико";
    
            case 33:
                return "значение сопротивления слишком мало";
    
            case 34:
                return "вид ошибки не определен";
    
            case 35:
                return "";
    
            case 36:
                return "обрыв цепи";
    
            case 37:
                return "неисправен";
    
            case 38:
                return "выход не переключается/короткое замыкание на плюс";
    
            case 39:
                return "выход не переключается/короткое замыкание на массу";
    
            case 40:
                return "короткое замыкание на другой клапан";
    
            case 41:
                return "блокирован или отсутствует напряжение";
    
            case 42:
                return "слишком велико отклонение частоты вращения";
    
            case 43:
                return "закрыто";
    
            case 44:
                return "короткое замыкание";
    
            case 45:
                return "разъём";
    
            case 46:
                return "разгерметизация";
    
            case 47:
                return "подключен неправильно";
    
            case 48:
                return "электропитание";
    
            case 49:
                return "нет связи";
    
            case 50:
                return "не достигается раннее положение";
    
            case 51:
                return "не достигается позднее положение";
    
            case 52:
                return "напряжение питания слишком велико";
    
            case 53:
                return "слишком низкое напряжение питания";
    
            case 54:
                return "оборудование выбрано неправильно";
    
            case 55:
                return "адаптация не выполнена";
    
            case 56:
                return "в аварийном режиме";
    
            case 57:
                return "сбой в электрической цепи";
    
            case 58:
                return "не блокируется";
    
            case 59:
                return "не разблокируется";
    
            case 60:
                return "не включена блокировка замков SAFE";
    
            case 61:
                return "не выключена блокировка замков SAFE";
    
            case 62:
                return "настройки не выполнены или выполнены неправильно";
    
            case 63:
                return "отключение по температуре";
    
            case 67:
                return "не достигнуто номинальное значение";
    
            case 68:
                return "цилиндр 1";
    
            case 69:
                return "цилиндр 2";
    
            case 70:
                return "цилиндр 3";
    
            case 71:
                return "цилиндр 4";
    
            case 72:
                return "цилиндр 5";
    
            case 73:
                return "цилиндр 6";
    
            case 74:
                return "цилиндр 7";
    
            case 75:
                return "цилиндр 8";
    
            case 76:
                return "кл. 30 отсутствует";
    
            case 77:
                return "внутреннее напряжение питания";
    
            case 78:
                return "отсутствие сообщения.";
    
            case 79:
                return "считать данные из памяти неисправностей";
    
            case 80:
                return "в однопроводном режиме";
    
            case 81:
                return "открыто";
    
            case 82:
                return "запущен";
    
            case 192:
                return "в настоящее время не проверяется";
    
            case 193:
                return "нет доступа";
    
            case 194:
                return "коррекция не выполнена";
    
            default:
                return "";

        }
    }

}
