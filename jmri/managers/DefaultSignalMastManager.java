// DefaultSignalMastManager.java

package jmri.managers;

import jmri.*;
import jmri.managers.AbstractManager;


/**
 * Default implementation of a SignalMastManager.
 * <P>
 * Note that this does not enforce any particular system naming convention
 * at the present time.  They're just names...
 *
 * @author  Bob Jacobsen Copyright (C) 2009
 * @version	$Revision: 1.8 $
 */
public class DefaultSignalMastManager extends AbstractManager
    implements SignalMastManager, java.beans.PropertyChangeListener {

    public DefaultSignalMastManager() {
        super();
    }

    public String getSystemPrefix() { return "I"; }
    public char typeLetter() { return 'F'; }

    public SignalMast getSignalMast(String name) {
        if (name==null || name.length()==0) { return null; }
        SignalMast t = getByUserName(name);
        if (t!=null) return t;

        return getBySystemName(name);
    }

    public SignalMast provideSignalMast(String prefix, // nominally IF$shsm
                                        String signalSystem,
                                        String mastName,
                                        String[] heads) {
        String name = prefix+":"+signalSystem+":";
        for (String s : heads) {
            name += "("+jmri.util.StringUtil.parenQuote(s)+")";
        }
        return provideSignalMast(name);
    }

    public SignalMast provideSignalMast(String name) {
        SignalMast m = getSignalMast(name);
        if (m == null) {
            m = new jmri.implementation.SignalHeadSignalMast(name);

            register(m);
        }
        return m;
    }

    public SignalMast getBySystemName(String key) {
        return (SignalMast)_tsys.get(key);
    }

    public SignalMast getByUserName(String key) {
        return (SignalMast)_tuser.get(key);
    }


    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DefaultSignalMastManager.class.getName());
}

/* @(#)DefaultSignalMastManager.java */
