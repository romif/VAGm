package com.vagm.vagmdroid.enums;

/**
 * The Class VAGmConstans.
 * @author Roman_Konovalov
 */
public final class VAGmConstans {

	/**
	 * Constructor.
	 */
	private VAGmConstans() {
	}

	/**
	 * ECU_LENGTH.
	 */
	public static final int ECU_LENGTH = 11;

	/**
	 * START_CONTROLLER_COMMUNICATION.
	 */
	public static final int START_CONTROLLER_COMMUNICATION = 0xFF;

	/**
	 * START_CONTROLLER_COMMUNICATION.
	 */
	public static final int EXIT_COMMAND = 0x06;

	/**
	 * START_MEAS_BLOCKS.
	 */
	public static final int START_MEAS_BLOCKS = 0x08;

	/**
	 * CONTROLLER_NO_ANSWER.
	 */
	public static final int CONTROLLER_NO_ANSWER = 0x01;

	/**
	 * VAG_BTI_INFO_RES.
	 */
	public static final int VAG_BTI_INFO_RES =  0xF6; // Info response

	/**
	 * VAG_BTI_ACT_RES.
	 */
	public static final int VAG_BTI_ACT_RES =   0xF5; // Actuators response

	/**
	 * VAG_BTI_DTC_RES.
	 */
	public static final int VAG_BTI_DTC_RES =   0xFC; // Diagnostic trouble codes response

	/**
	 * VAG_BTI_BS_RES.
	 */
	public static final int VAG_BTI_BS_RES =    0xF4; // Basic settings response

	/**
	 * VAG_BTI_ERROR.
	 */
	public static final int VAG_BTI_ERROR =     0x0A; // Error

	/**
	 * VAG_BTI_GROUP_RES.
	 */
	public static final int VAG_BTI_GROUP_RES = 0xE7;
}
