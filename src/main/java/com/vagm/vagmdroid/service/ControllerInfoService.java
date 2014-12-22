package com.vagm.vagmdroid.service;

import com.google.inject.Singleton;

/**
 * The Class ControllerInfoService.
 * 
 * @author Roman_Konovalov
 */
@Singleton
public class ControllerInfoService {

    /**
     * boudRate.
     */
    private String boudRate;

    /**
     * vagNumber.
     */
    private String vagNumber;

    /**
     * component.
     */
    private String component;

    /**
     * group.
     */
    private Integer group;

    /**
     * @return the boudRate
     */
    public String getBoudRate() {
        return boudRate;
    }

    /**
     * @param boudRate
     *            the boudRate to set
     */
    public void setBoudRate(final String boudRate) {
        this.boudRate = boudRate;
    }

    /**
     * @return the vagNumber
     */
    public String getVagNumber() {
        return vagNumber;
    }

    /**
     * @param vagNumber
     *            the vagNumber to set
     */
    public void setVagNumber(final String vagNumber) {
        this.vagNumber = vagNumber;
    }

    /**
     * @return the component
     */
    public String getComponent() {
        return component;
    }

    /**
     * @param component
     *            the component to set
     */
    public void setComponent(final String component) {
        this.component = component;
    }

    /**
     * @return the group
     */
    public Integer getGroup() {
        return group;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroup(final Integer group) {
        this.group = group;
    }

    /**
     * clear.
     */
    public void clear() {
        setBoudRate(null);
        setVagNumber(null);
        setComponent(null);
        setGroup(null);
    }

}
