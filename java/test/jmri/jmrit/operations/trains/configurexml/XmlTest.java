package jmri.jmrit.operations.trains.configurexml;

import org.junit.Assert;
import org.junit.Test;

import jmri.jmrit.operations.OperationsTestCase;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class XmlTest extends OperationsTestCase {

    @Test
    public void testCTor() {
        Xml t = new Xml();
        Assert.assertNotNull("exists", t);
    }

    // private final static Logger log = LoggerFactory.getLogger(XmlTest.class);

}
