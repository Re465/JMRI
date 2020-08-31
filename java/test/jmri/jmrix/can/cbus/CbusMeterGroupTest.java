package jmri.jmrix.can.cbus;

import jmri.Meter;
import jmri.MeterGroup;
import jmri.jmrix.can.CanMessage;
import jmri.jmrix.can.CanReply;
import jmri.jmrix.can.CanSystemConnectionMemo;
import jmri.jmrix.can.TrafficControllerScaffold;
import jmri.jmrix.can.cbus.node.CbusNode;
import jmri.jmrix.can.cbus.node.CbusNodeTableDataModel;
import jmri.util.JUnitUtil;

import org.junit.Assert;
import org.junit.jupiter.api.*;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 * @author Steve Young Copyright (C) 2019
 */
public class CbusMeterGroupTest extends jmri.implementation.AbstractMeterGroupTestBase {

    private CanSystemConnectionMemo memo;
    private TrafficControllerScaffold tcis;

    @BeforeEach
    @Override
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.resetProfileManager();
        JUnitUtil.initConfigureManager();
        
        memo = new CanSystemConnectionMemo();
        tcis = new TrafficControllerScaffold();
        memo.setTrafficController(tcis);
        mm = new CbusMeterGroup(memo);
    }
    
    @AfterEach
    @Override
    public void tearDown() {
        mm = null;
        tcis.terminateThreads();
        tcis=null;
        memo.dispose();
        memo = null;
        JUnitUtil.tearDown();
    }
    
    public double getCurrent() {
        return mm.getMeterByName(MeterGroup.CurrentMeter).getMeter().getKnownAnalogValue();
    }
    
    public double getVoltage() {
        return mm.getMeterByName(MeterGroup.VoltageMeter).getMeter().getKnownAnalogValue();
    }

    @Test
    public void testEnableDisable(){
        
        Assert.assertEquals("no listener to start",0,tcis.numListeners());
        
        mm.enable();
        Assert.assertEquals("listening",1,tcis.numListeners());
        mm.disable();
        Assert.assertEquals("not listening",0,tcis.numListeners());
        
        CbusNodeTableDataModel nodeModel = new CbusNodeTableDataModel(memo, 3,CbusNodeTableDataModel.MAX_COLUMN);
        jmri.InstanceManager.setDefault(CbusNodeTableDataModel.class,nodeModel );
        Assert.assertEquals("node table listening",1,tcis.numListeners());
        mm.enable();
        Assert.assertEquals("mm listening",2,tcis.numListeners());
        mm.disable();
        Assert.assertEquals("mm not listening",1,tcis.numListeners());
        
        CbusNode testCs = nodeModel.provideNodeByNodeNum(777);
        testCs.setCsNum(0);
        Assert.assertEquals("node + node table listening",2,tcis.numListeners());
        
        mm.enable();
        Assert.assertEquals("multimeter listening",3,tcis.numListeners());
        mm.disable();
        Assert.assertEquals("mm not listening",2,tcis.numListeners());
        
        nodeModel.dispose();
        testCs.dispose();
        
    }
    
    @Test
    public void testMultiMCanReply(){
        
        CbusNodeTableDataModel nodeModel = new CbusNodeTableDataModel(memo, 3,CbusNodeTableDataModel.MAX_COLUMN);
        jmri.InstanceManager.setDefault(CbusNodeTableDataModel.class,nodeModel );
        
        CbusNode testCs = nodeModel.provideNodeByNodeNum(54321);
        testCs.setCsNum(0);
        
        mm.enable();
        
        CanReply r = new CanReply(tcis.getCanid());
        r.setNumDataElements(7);
        r.setElement(0, CbusConstants.CBUS_ACOF2);
        r.setElement(1, 0xd4); // nn 54322
        r.setElement(2, 0x32); // nn 54322
        r.setElement(3, 0x00); // en 1
        r.setElement(4, 0x01); // en 1
        r.setElement(5, 0x00); // 8mA
        r.setElement(6, 0x08); // 8mA
        ((CbusMeterGroup)mm).reply(r);
        
        Assert.assertEquals(0,getCurrent(),0.001 ); // wrong opc
        
        r.setElement(0, CbusConstants.CBUS_ACON2);
        ((CbusMeterGroup)mm).reply(r);
        
        Assert.assertEquals(0,getCurrent(),0.001 ); // wrong node
        
        r.setElement(2, 0x31); // nn 54321
        ((CbusMeterGroup)mm).reply(r);
        Assert.assertEquals(8,getCurrent(),0.001 );
        
        r.setElement(5, 0x12); // 4807mA
        r.setElement(6, 0xc7); // 4807mA
        ((CbusMeterGroup)mm).reply(r);
        Assert.assertEquals(4807,getCurrent(),0.001 );
        
        CanMessage m = new CanMessage(tcis.getCanid());
        m.setNumDataElements(7);
        m.setElement(0, CbusConstants.CBUS_ACON2);
        m.setElement(1, 0xd4); // nn 54321
        m.setElement(2, 0x31); // nn 54321
        m.setElement(3, 0x00); // en1
        m.setElement(4, 0x01); // en1
        m.setElement(5, 0x00); // 0mA
        m.setElement(6, 0x00); // 0mA
        
        ((CbusMeterGroup)mm).message(m);
        Assert.assertEquals(4807,getCurrent(),0.001 ); // CanMessage Ignored
        
        r = new CanReply(tcis.getCanid());
        r.setNumDataElements(7);
        r.setElement(0, CbusConstants.CBUS_ACON2);
        r.setElement(1, 0xd4); // nn 54321
        r.setElement(2, 0x31); // nn 54321
        r.setElement(3, 0x00); // en1
        r.setElement(4, 0x01); // en1
        r.setElement(5, 0x00); // 0mA
        r.setElement(6, 0x00); // 0mA
        
        ((CbusMeterGroup)mm).reply(r);
        Assert.assertEquals(0,getCurrent(),0.001 );
        
        // wrong event num
        r = new CanReply(tcis.getCanid());
        r.setNumDataElements(7);
        r.setElement(0, CbusConstants.CBUS_ACON2);
        r.setElement(1, 0xd4); // nn 54321
        r.setElement(2, 0x31); // nn 54321
        r.setElement(3, 0x00); // en3
        r.setElement(4, 0x03); // en3
        r.setElement(5, 0x12); // 4807mA
        r.setElement(6, 0xc7); // 4807mA
        
        ((CbusMeterGroup)mm).reply(r);
        Assert.assertEquals("Wrong event",0,getCurrent(),0.001 );
        r.setElement(4, 0x01); // en1
        r.setRtr(true);
        
        ((CbusMeterGroup)mm).reply(r);
        Assert.assertEquals(0,getCurrent(),0.001 );
        
        r.setExtended(true);
        r.setRtr(false);
        
        ((CbusMeterGroup)mm).reply(r);
        Assert.assertEquals(0,getCurrent(),0.001 );
        
        r.setExtended(false);
        ((CbusMeterGroup)mm).reply(r);
        Assert.assertEquals(4807,getCurrent(),0.001 );
        
        mm.disable();
        
        nodeModel.dispose();
        testCs.dispose();
        
    }
    
    @Test
    public void testMultiMVoltCanReply(){
        
        CbusNodeTableDataModel nodeModel = new CbusNodeTableDataModel(memo, 3,CbusNodeTableDataModel.MAX_COLUMN);
        jmri.InstanceManager.setDefault(CbusNodeTableDataModel.class,nodeModel );
        
        CbusNode testCs = nodeModel.provideNodeByNodeNum(54321);
        testCs.setCsNum(0);
        
        mm.enable();
        
        CanReply r = new CanReply(tcis.getCanid());
        r.setNumDataElements(7);
        r.setElement(0, CbusConstants.CBUS_ACON2);
        r.setElement(1, 0xd4); // nn 54321
        r.setElement(2, 0x31); // nn 54321
        r.setElement(3, 0x00); // en 2
        r.setElement(4, 0x02); // en 2
        r.setElement(5, 0x00); // 12.9V
        r.setElement(6, 0x81); // 12.9V
        System.out.format("testMultiMVoltCanReply: reply(12.9) volt%n");
        ((CbusMeterGroup)mm).reply(r);
        Assert.assertEquals(12.9,getVoltage(),0.001 );
        
        r = new CanReply(tcis.getCanid());
        r.setNumDataElements(7);
        r.setElement(0, CbusConstants.CBUS_ACON2);
        r.setElement(1, 0xd4); // nn 54321
        r.setElement(2, 0x31); // nn 54321
        r.setElement(3, 0x00); // en 2
        r.setElement(4, 0x02); // en 2
        r.setElement(5, 0x01); // 25.6V
        r.setElement(6, 0x00); // 25.6V
        ((CbusMeterGroup)mm).reply(r);
        Assert.assertEquals(25.6,getVoltage(),0.001 );
        
        r = new CanReply(tcis.getCanid());
        r.setNumDataElements(7);
        r.setElement(0, CbusConstants.CBUS_ACON2);
        r.setElement(1, 0xd4); // nn 54321
        r.setElement(2, 0x31); // nn 54321
        r.setElement(3, 0x00); // en2
        r.setElement(4, 0x01); // en2
        r.setElement(5, 0x00); // 0V
        r.setElement(6, 0x00); // 0V
        ((CbusMeterGroup)mm).reply(r);
        Assert.assertEquals(0,getCurrent(),0.001 );
        
        mm.disable();
        
        nodeModel.dispose();
        testCs.dispose();
        
    }
    
    @Test
    public void testSmallFuncs(){
        ((CbusMeterGroup)mm).requestUpdateFromLayout();
        Assert.assertEquals("ma units", mm.getMeterByName(MeterGroup.CurrentMeter).getMeter().getUnit(), Meter.Unit.Milli);
    }

    // private final static Logger log = LoggerFactory.getLogger(CbusMeterGroupTest.class);

}
