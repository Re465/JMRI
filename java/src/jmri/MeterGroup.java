package jmri;

import java.util.Collection;

import javax.annotation.CheckForNull;

/**
 * Interface for displaying (and controlling where appropriate) Current,
 * Voltage, and other status data from the layout.
 *
 * @author Daniel Bergqvist Copyright (C) 2020
 */
public interface MeterGroup extends NamedBean {

    public static final String PROPERTY_METER_ADDED = "Meter added";
    public static final String PROPERTY_METER_REMOVED = "Meter removed";
    
    public static final String VoltageMeter = "Voltage";
    public static final String VoltageMeterDescr = Bundle.getMessage("VoltageMeter");
    
    public static final String CurrentMeter = "Current";
    public static final String CurrentMeterDescr = Bundle.getMessage("CurrentMeter");
    
    
    /**
     * An interface with info about a meter
     */
    public interface MeterInfo {
        
        /**
         * The name of the meter.
         * <p>
         * All meters used by a single MeterGroup must have unique names. This
         * string should not be internationalized.
         * @return the name of the meter
         */
        public String getName();
        
        /**
         * The description of the meter.
         * <p>
         * This string should be internationalized.
         * @return the description
         */
        public String getDescription();
        
        /**
         * Get the meter.
         * @return the meter
         */
        public Meter getMeter();
    }
    
    
    /**
     * Get a collection of all the meters registered in this multimeter.
     * @return the list of meters
     */
    public Collection<MeterInfo> getMeters();
    
    /**
     * Get a meter by name.
     * @param name the name of the meter
     * @return the meter or null if there is no meter with this name
     */
    @CheckForNull
    public MeterInfo getMeterByName(String name);
    
    /**
     * Add a meter to the meter group
     * @param name the name of the meter (not translated)
     * @param descr the description of the meter (translated)
     * @param meter the meter
     */
    public void addMeter(final String name, final String descr, final Meter meter);
    
    /**
     * Remove the meter from the group
     * @param name the name of the meter
     */
    public void removeMeter(String name);
    
    /**
     * Remove the meter from the group
     * @param meter the meter
     */
    public void removeMeter(Meter meter);

    /**
     * Enable all meters in this group
     */
    public void enable();

    /**
     * Disable all meters in this group
     */
    public void disable();
    
    /**
     * Remove references to and from this object, so that it can eventually be
     * garbage-collected.
     */
    @Override
    public void dispose();
    
}
