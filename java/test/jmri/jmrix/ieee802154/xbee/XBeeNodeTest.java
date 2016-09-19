package jmri.jmrix.ieee802154.xbee;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * XBeeNodeTest.java
 *
 * Description:	tests for the jmri.jmrix.ieee802154.xbee.XBeeNode class
 *
 * @author	Paul Bender Copyright (C) 2016
 */
@RunWith(PowerMockRunner.class)
public class XBeeNodeTest{

    private XBeeTrafficController tc = null;

    @Test
    public void testCtor() {
        XBeeNode m = new XBeeNode();
        Assert.assertNotNull("exists", m);
    }

    @Test
    public void testCtorWithParamters() {
        byte pan[] = {(byte) 0x00, (byte) 0x42};
        byte uad[] = {(byte) 0x6D, (byte) 0x97};
        byte gad[] = {(byte) 0x00, (byte) 0x13, (byte) 0xA2, (byte) 0x00, (byte) 0x40, (byte) 0xA0, (byte) 0x4D, (byte) 0x2D};
        XBeeNode node = new XBeeNode(pan,uad,gad);
        node.setTrafficController(tc);
        Assert.assertNotNull("exists", node);
        Assert.assertEquals("Node PAN address high byte", pan[0], node.getPANAddress()[0]);
        Assert.assertEquals("Node PAN address low byte", pan[1], node.getPANAddress()[1]);
        Assert.assertEquals("Node user address high byte", uad[0], node.getUserAddress()[0]);
        Assert.assertEquals("Node user address low byte", uad[1], node.getUserAddress()[1]);
        for (int i = 0; i < gad.length; i++) {
            Assert.assertEquals("Node global address byte " + i, gad[i], node.getGlobalAddress()[i]);
        }
    }

    @Test
    public void testSetPANAddress() {
        // test the code to set the User address
        XBeeNode node = new XBeeNode();
        byte pan[] = {(byte) 0x00, (byte) 0x01};
        node.setPANAddress(pan);
        Assert.assertEquals("Node PAN address high byte", pan[0], node.getPANAddress()[0]);
        Assert.assertEquals("Node PAN address low byte", pan[1], node.getPANAddress()[1]);
    }

    @Test
    public void testSetUserAddress() {
        // test the code to set the User address
        XBeeNode node = new XBeeNode();
        byte uad[] = {(byte) 0x6D, (byte) 0x97};
        node.setUserAddress(uad);
        Assert.assertEquals("Node user address high byte", uad[0], node.getUserAddress()[0]);
        Assert.assertEquals("Node user address low byte", uad[1], node.getUserAddress()[1]);
    }

    @Test
    public void testSetGlobalAddress() {
        // test the code to set the User address
        XBeeNode node = new XBeeNode();
        byte gad[] = {(byte) 0x00, (byte) 0x13, (byte) 0xA2, (byte) 0x00, (byte) 0x40, (byte) 0xA0, (byte) 0x4D, (byte) 0x2D};
        node.setGlobalAddress(gad);
        for (int i = 0; i < gad.length; i++) {
            Assert.assertEquals("Node global address byte " + i, gad[i], node.getGlobalAddress()[i]);
        }
    }

    @Test
    @Ignore("needs further setup")
    public void testGetPreferedNameAsUserAddress() {
        byte pan[] = {(byte) 0x00, (byte) 0x42};
        byte uad[] = {(byte) 0x6D, (byte) 0x97};
        byte gad[] = {(byte) 0x00, (byte) 0x13, (byte) 0xA2, (byte) 0x00, (byte) 0x40, (byte) 0xA0, (byte) 0x4D, (byte) 0x2D};
        XBeeNode node = new XBeeNode(pan,uad,gad);
        Assert.assertEquals("Short Address Name","6D 97 ",node.getPreferedName());
    }

    @Test
    @Ignore("needs further setup")
    public void testGetPreferedNameAsGlobalAddress() {
        byte pan[] = {(byte) 0x00, (byte) 0x42};
        byte uad[] = {(byte) 0xFF, (byte) 0xFF};
        byte gad[] = {(byte) 0x00, (byte) 0x13, (byte) 0xA2, (byte) 0x00, (byte) 0x40, (byte) 0xA0, (byte) 0x4D, (byte) 0x2D};
        XBeeNode node = new XBeeNode(pan,uad,gad);
        Assert.assertEquals("Global Address Name","00 13 A2 00 40 A0 4D 2D ",node.getPreferedName());
    }

    @Test
    @Ignore("needs further setup")
    public void testGetPreferedNameAsNodeIdentifier() {
        byte pan[] = {(byte) 0x00, (byte) 0x42};
        byte uad[] = {(byte) 0xFF, (byte) 0xFF};
        byte gad[] = {(byte) 0x00, (byte) 0x13, (byte) 0xA2, (byte) 0x00, (byte) 0x40, (byte) 0xA0, (byte) 0x4D, (byte) 0x2D};
        XBeeNode node = new XBeeNode(pan,uad,gad);
        node.setIdentifier("Hello World");
        Assert.assertEquals("Identifier Name",node.getPreferedName(),"Hello World");
    }

    @Test
    @Ignore("needs further setup")
    public void testGetPreferedTransmitUserAddress() {
        byte pan[] = {(byte) 0x00, (byte) 0x42};
        byte uad[] = {(byte) 0x6D, (byte) 0x97};
        byte gad[] = {(byte) 0x00, (byte) 0x13, (byte) 0xA2, (byte) 0x00, (byte) 0x40, (byte) 0xA0, (byte) 0x4D, (byte) 0x2D};
        XBeeNode node = new XBeeNode(pan,uad,gad);
        Assert.assertEquals("Short Transmit Address",node.getXBeeAddress16(),node.getPreferedTransmitAddress());
    }

    @Test
    @Ignore("needs further setup")
    public void testGetPreferedTransmitGlobalAddress() {
        byte pan[] = {(byte) 0x00, (byte) 0x42};
        byte uad[] = {(byte) 0xFF, (byte) 0xFF};
        byte gad[] = {(byte) 0x00, (byte) 0x13, (byte) 0xA2, (byte) 0x00, (byte) 0x40, (byte) 0xA0, (byte) 0x4D, (byte) 0x2D};
        XBeeNode node = new XBeeNode(pan,uad,gad);
        Assert.assertEquals("Global Transmit Address",node.getXBeeAddress64(),node.getPreferedTransmitAddress());
    }

    @Test
    @Ignore("needs further setup")
    public void testGetPreferedTransmitGlobalAddressWithMaskRequired() {
        byte pan[] = {(byte) 0x00, (byte) 0x42};
        byte uad[] = {(byte) 0x0fffffff, (byte) 0x0ffffffe};
        byte gad[] = {(byte) 0x00, (byte) 0x13, (byte) 0xA2, (byte) 0x00, (byte) 0x40, (byte) 0xA0, (byte) 0x4D, (byte) 0x2D};
        XBeeNode node = new XBeeNode(pan,uad,gad);
        Assert.assertEquals("Global Transmit Address",node.getXBeeAddress64(),node.getPreferedTransmitAddress());
    }

    // The minimal setup for log4J
    @Before
    public void setUp() {
        //apps.tests.Log4JFixture.setUp();
        tc = new XBeeInterfaceScaffold();
    }

    @After
    public void tearDown() {
        //apps.tests.Log4JFixture.tearDown();
        tc=null;
    }

}
