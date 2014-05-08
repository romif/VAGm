package com.vagm.vagmdroid.dto;

/**
 * The Class DataStreamDTO.
 * @author Roman_Konovalov
 */
public class DataStreamDTO {

	/**
	 * value.
	 */
	private String value;

	/**
	 * unit.
	 */
	private String unit;

	/**
	 * valueFloat.
	 */
	private float valueFloat;

	/**
	 * constructor.
	 */
	public DataStreamDTO() { }

	/**
	 * constructor.
	 * @param value value
	 * @param unit unit
	 * @param valueFloat valueFloat
	 */
	public DataStreamDTO(final String value, final String unit, final float valueFloat) {
		super();
		this.value = value;
		this.unit = unit;
		this.valueFloat = valueFloat;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(final String unit) {
		this.unit = unit;
	}

	/**
	 * @return the valueFloat
	 */
	public float getValueFloat() {
		return valueFloat;
	}

	/**
	 * @param valueFloat
	 *            the valueFloat to set
	 */
	public void setValueFloat(final float valueFloat) {
		this.valueFloat = valueFloat;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + Float.floatToIntBits(valueFloat);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DataStreamDTO other = (DataStreamDTO) obj;
		if (unit == null) {
			if (other.unit != null) {
				return false;
			}
		} else if (!unit.equals(other.unit)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		if (Float.floatToIntBits(valueFloat) != Float.floatToIntBits(other.valueFloat)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "DataStreamDTO [value=" + value + ", unit=" + unit + ", valueFloat=" + valueFloat + "]";
	}

}
