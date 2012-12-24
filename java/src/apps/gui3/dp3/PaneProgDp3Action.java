// PaneProgAction.java

package apps.gui3.dp3;

import jmri.jmrit.decoderdefn.DecoderFile;
import jmri.jmrit.decoderdefn.DecoderIndexFile;
import jmri.jmrit.roster.RosterEntry;
import jmri.jmrit.roster.Roster;
import jmri.jmrit.symbolicprog.*;
import jmri.util.JmriJFrame;
import jmri.jmrit.roster.IdentifyLoco;
import jmri.util.swing.JmriPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.Rectangle;

import org.jdom.Element;
import jmri.jmrit.XmlFile;

import  jmri.jmrit.symbolicprog.tabbedframe.*;

import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.ButtonGroup;
import javax.swing.border.TitledBorder;
import java.awt.Color;

/**
 * Swing action to create and register a
 * frame for selecting the information needed to
 * open a PaneProgFrame in service mode.
 * <P>
 * The name is a historical accident, and probably should have
 * included "ServiceMode" or something.
 * <P>
 * The resulting JFrame
 * is constructed on the fly here, and has no specific type.
 *
 * @see  jmri.jmrit.symbolicprog.tabbedframe.PaneOpsProgAction
 *
 * @author			Bob Jacobsen    Copyright (C) 2001
 * @version			$Revision: 17977 $
 */
public class PaneProgDp3Action 			extends jmri.util.swing.JmriAbstractAction implements jmri.ProgListener, jmri.jmrit.symbolicprog.tabbedframe.PaneContainer{

    Object o1, o2, o3, o4;
    JLabel statusLabel;
    jmri.jmrit.progsupport.ProgModeSelector modePane = new jmri.jmrit.progsupport.ProgServiceModeComboBox();

    static final java.util.ResourceBundle rbt = jmri.jmrit.symbolicprog.SymbolicProgBundle.bundle();

    public PaneProgDp3Action(String s, jmri.util.swing.WindowInterface wi) {
    	super(s, wi);
        init();
    }
     
 	public PaneProgDp3Action(String s, javax.swing.Icon i, jmri.util.swing.WindowInterface wi) {
    	super(s, i, wi);
        init();
    }
    
    public PaneProgDp3Action() {
        this("DecoderPro service programmer");
    }

    public PaneProgDp3Action(String s) {
        super(s);
        init();

    }
    
    void init(){
        statusLabel = new JLabel(rbt.getString("StateIdle"));
    }
    
    JmriJFrame f;
    CombinedLocoSelTreePane combinedLocoSelTree;
    
    public void actionPerformed(ActionEvent e) {

        if (log.isDebugEnabled()) log.debug("Pane programmer requested");

        if(f==null){
            // create the initial frame that steers
            f = new JmriJFrame("Create New Loco"); //rbt.getString("FrameServiceProgrammerSetup")
            f.getContentPane().setLayout(new BorderLayout());
            // ensure status line is cleared on close so it is normal if re-opened
            f.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(WindowEvent we){
                    statusLabel.setText(rbt.getString("StateIdle"));
                    f.windowClosing(we);}});

            // add the Roster menu
            JMenuBar menuBar = new JMenuBar();
            JMenu j = new JMenu(rbt.getString("MenuFile"));
            j.add(new jmri.jmrit.decoderdefn.PrintDecoderListAction(rbt.getString("MenuPrintDecoderDefinitions"), f, false));
            j.add(new jmri.jmrit.decoderdefn.PrintDecoderListAction(rbt.getString("MenuPrintPreviewDecoderDefinitions"), f, true));
            menuBar.add(j);
            menuBar.add(new jmri.jmrit.roster.swing.RosterMenu(rbt.getString("MenuRoster"), jmri.jmrit.roster.swing.RosterMenu.MAINMENU, f));
            f.setJMenuBar(menuBar);
            final JPanel bottomPanel = new JPanel(new BorderLayout());
            // new Loco on programming track
            combinedLocoSelTree = new CombinedLocoSelTreePane(statusLabel){
                @Override
                    protected void startProgrammer(DecoderFile decoderFile, RosterEntry re,
                                                    String filename) {
                        String title = java.text.MessageFormat.format(rbt.getString("FrameServiceProgrammerTitle"),
                                                            new Object[]{"new decoder"});
                        if (re!=null) title = java.text.MessageFormat.format(rbt.getString("FrameServiceProgrammerTitle"),
                                                            new Object[]{re.getId()});
                        JFrame p = new PaneServiceProgFrame(decoderFile, re,
                                                     title, "programmers"+File.separator+"Comprehensive.xml",
                                                     modePane.getProgrammer());
                        if(editModeProg.isSelected()){
                            p = new PaneProgFrame(decoderFile, re,
                                             title, "programmers"+File.separator+"Comprehensive.xml",
                                             null, false){
                                protected JPanel getModePane() { return null; }
                            };
                        }
                        p.pack();
                        p.setVisible(true);
                    }
                    
                    protected void openNewLoco() {
                    // find the decoderFile object
                        DecoderFile decoderFile = DecoderIndexFile.instance().fileFromTitle(selectedDecoderType());
                        if (log.isDebugEnabled()) log.debug("decoder file: "+decoderFile.getFilename());
                        if(rosterIdField.getText().equals(jmri.jmrit.symbolicprog.SymbolicProgBundle.bundle().getString("LabelNewDecoder"))){
                            re = new RosterEntry();
                            re.setDecoderFamily(decoderFile.getFamily());
                            re.setDecoderModel(decoderFile.getModel());
                            re.setId(jmri.jmrit.symbolicprog.SymbolicProgBundle.bundle().getString("LabelNewDecoder"));
                            //re.writeFile(cvModel, iCvModel, variableModel );
                            // note that we're leaving the filename null
                            // add the new roster entry to the in-memory roster
                        } else {
                            saveRosterEntry();
                        }
                        // create a dummy RosterEntry with the decoder info
                        startProgrammer(decoderFile, re, null);
                    }

                @Override
                    protected JPanel layoutRosterSelection() { return null; }
                    
                @Override
                    protected JPanel layoutDecoderSelection() {
                        JPanel pan = super.layoutDecoderSelection();
                        dTree.removeTreeSelectionListener(dListener);
                        dTree.addTreeSelectionListener(dListener = new TreeSelectionListener() {
                            public void valueChanged(TreeSelectionEvent e) {
                                if (!dTree.isSelectionEmpty() && dTree.getSelectionPath()!=null &&
                                  // can't be just a mfg, has to be at least a family
                                  dTree.getSelectionPath().getPathCount()>2 &&
                                  // can't be a multiple decoder selection
                                  dTree.getSelectionCount()<2) {
                                    log.debug("Selection event with "+dTree.getSelectionPath().toString());
                                    if (locoBox != null) locoBox.setSelectedIndex(0);
                                    go2.setEnabled(true);
                                    go2.setRequestFocusEnabled(true);
                                    go2.requestFocus();
                                    go2.setToolTipText(rbt.getString("TipClickToOpen"));
                                    decoderFile = DecoderIndexFile.instance().fileFromTitle(selectedDecoderType());
                                    setUpRosterPanel();
                                } else {
                                    decoderFile = null;
                                    // decoder not selected - require one
                                    go2.setEnabled(false);
                                    go2.setToolTipText(rbt.getString("TipSelectLoco"));
                                    setUpRosterPanel();
                                }
                            }
                        });
                        return pan;
                    }
                    
                    protected void selectDecoder(int mfgID, int modelID, int productID) {
                        super.selectDecoder(mfgID, modelID, productID);
                        findDecoderAddress();
                    }
                    
                    
                    JRadioButton serviceModeProg;
                    JRadioButton editModeProg;
                    
                @Override
                    protected JPanel createProgrammerSelection(){
                        //p=jmri.InstanceManager.getDefault(jmri.UserPreferencesManager.class);
                        serviceModeProg = new JRadioButton("<HTML>Service Mode<br>(programming track)</HTML>");
                        editModeProg = new JRadioButton("Edit Only");
                        JPanel pane3a = new JPanel();
                        pane3a.setLayout(new BoxLayout(pane3a, BoxLayout.Y_AXIS));
                        // create the programmer box
                        
                        ButtonGroup modeGroup = new ButtonGroup();
                        modeGroup.add(serviceModeProg);
                        modeGroup.add(editModeProg);
                        
                        JPanel progModePane = new JPanel();
                        progModePane.add(serviceModeProg);
                        progModePane.add(editModeProg);
                        serviceModeProg.setSelected(true);
                        
                        if (jmri.InstanceManager.programmerManagerInstance()==null ||
                            !jmri.InstanceManager.programmerManagerInstance().isGlobalProgrammerAvailable()){
                            editModeProg.setSelected(true);
                            serviceModeProg.setEnabled(false);
                            iddecoder.setVisible(false);
                            modePane.setVisible(false);
                        }
                        
                        serviceModeProg.addActionListener(new ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent e) {
                                if(serviceModeProg.isSelected())
                                    iddecoder.setVisible(true);
                            }
                        });
                        
                        editModeProg.addActionListener(new ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent e) {
                                if(editModeProg.isSelected())
                                    iddecoder.setVisible(false);
                            }
                        });
                        
                        go2 = new JButton(/*rbt.getString("OPEN PROGRAMMER")*/"Open Comprehensive Programmer");
                        go2.addActionListener( new ActionListener() {
                                public void actionPerformed(java.awt.event.ActionEvent e) {
                                    if (log.isDebugEnabled()) log.debug("Open programmer pressed");
                                    openButton();
                                    //p.addComboBoxLastSelection(lastSelectedProgrammer, (String) programmerBox.getSelectedItem());
                                }
                            });
                        go2.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
                        go2.setEnabled(false);
                        go2.setToolTipText(rbt.getString("SELECT A LOCOMOTIVE OR DECODER TO ENABLE"));
                        bottomPanel.add(go2, BorderLayout.EAST);
                        //pane3a.add(go2);
                        return pane3a;
                    }
                };

            // load primary frame
            JPanel topPanel = new JPanel();
            topPanel.add(modePane);
            topPanel.add(new JSeparator(javax.swing.SwingConstants.HORIZONTAL));
            f.getContentPane().add(topPanel, BorderLayout.NORTH);
            //f.getContentPane().add(modePane);
            //f.getContentPane().add(new JSeparator(javax.swing.SwingConstants.HORIZONTAL));

            combinedLocoSelTree.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            f.getContentPane().add(combinedLocoSelTree, BorderLayout.CENTER);
            
            //f.getContentPane().add(new JSeparator(javax.swing.SwingConstants.HORIZONTAL));
            //basicRoster.setEnabled(false);
            statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            bottomPanel.add(statusLabel, BorderLayout.SOUTH);
            f.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

            f.pack();
            if (log.isDebugEnabled()) log.debug("Tab-Programmer setup created");
        } else {
            combinedLocoSelTree.resetSelections();
        }
        f.setVisible(true);
    }
    
    String lastSelectedProgrammer = this.getClass().getName()+".SelectedProgrammer";

    // never invoked, because we overrode actionPerformed above
    public JmriPanel makePanel() {
        throw new IllegalArgumentException("Should not be invoked");
    }
    
    JTextField rosterIdField = new JTextField(20);
    JTextField rosterAddressField = new JTextField(10);
    
    RosterEntry re;
    
    int teststatus = 0;
    
    void findDecoderAddress(){
        teststatus = 1;
        readCV(29);
    }
    
    DecoderFile decoderFile;
    boolean shortAddr = false;
    int cv29 = 0;
    int cv17 = -1;
    int cv18 = -1;
    int cv19 = 0;
    int cv1 = 0;
    int longAddress;
    int address;
    
    public void programmingOpReply(int value, int status) {
        switch(teststatus){
            case 1 : 
                     teststatus = 2;
                     cv29=value;
                     readCV(1);
                     break;
            case 2 : teststatus = 3;
                     cv1=value;
                     readCV(17);
                     break;
            case 3 : teststatus = 4;
                     cv17 = value;
                     readCV(18);
                     break;
            case 4 : teststatus = 5;
                     cv18 = value;
                     readCV(19);
                     break;
            case 5 : cv19 = value;
                     finishRead();
                     break;
        }
    }
    
    void finishRead(){
        if((cv29&0x20) == 0){
            shortAddr = true;
            address = cv1;
        }
        if(cv17!=-1 || cv18!=-1){
            longAddress = (cv17&0x3f)*256 + cv18;
            address = longAddress;    
        }
        if(progPane!=null){
            progPane.setVariableValue("Short Address", cv1);
            progPane.setVariableValue("Long Address", longAddress);
            progPane.setCVValue(29, cv29);
            progPane.setCVValue(19, cv19);
        }
    }
    
    protected void readCV(int cv) {
        jmri.Programmer p = jmri.InstanceManager.programmerManagerInstance().getGlobalProgrammer();
        if (p == null) {
            //statusUpdate("No programmer connected");
        } else {
            try {
                p.readCV(cv, this);
            } catch (jmri.ProgrammerException ex) {
                //statusUpdate(""+ex);
            }
        }
    }
    JPanel rosterPanel = null;//new JPanel();
    jmri.Programmer          mProgrammer;
    CvTableModel        cvModel      = null;
    IndexedCvTableModel iCvModel     = null;
    VariableTableModel  variableModel;
    DccAddressPanel dccAddressPanel;
    Element modelElem = null;
    ThisProgPane progPane = null;
    
    @SuppressWarnings("unchecked")
    synchronized void setUpRosterPanel(){
        if(rosterPanel==null){
            rosterPanel = new JPanel();
            rosterPanel.setLayout(new BorderLayout());
            JPanel p = new JPanel();
            p.add(new JLabel("Roster Id"));
            p.add(rosterIdField);
            rosterPanel.add(p, BorderLayout.NORTH);
            rosterIdField.setText(jmri.jmrit.symbolicprog.SymbolicProgBundle.bundle().getString("LabelNewDecoder"));
            saveBasicRoster = new JButton("Save");
            saveBasicRoster.addActionListener( new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    saveRosterEntry();
                }
            });
            TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black));
            border.setTitle("Create Basic Roster Entry");
            rosterPanel.setBorder(border);
            rosterPanel.setVisible(false);
            f.getContentPane().add(rosterPanel, BorderLayout.EAST);
        }
        if(progPane!=null){
            progPane.dispose();
            rosterPanel.remove(progPane);
            progPane = null;
            rosterPanel.revalidate();
            f.getContentPane().repaint();
            f.repaint();
            f.pack();

        }
        if (jmri.InstanceManager.programmerManagerInstance() != null &&
            jmri.InstanceManager.programmerManagerInstance().isGlobalProgrammerAvailable()){
            this.mProgrammer = jmri.InstanceManager.programmerManagerInstance().getGlobalProgrammer();
        }

        cvModel       = new CvTableModel(statusLabel, mProgrammer);
        iCvModel      = new IndexedCvTableModel(statusLabel, mProgrammer);

        variableModel = new VariableTableModel(statusLabel, new String[]  {"Name", "Value"},
                                                 cvModel, iCvModel);
        if(decoderFile!=null){
            Element decoderRoot = null;
            try {
                decoderRoot = decoderFile.rootFromName(DecoderFile.fileLocation+decoderFile.getFilename());
            } catch (Exception e) { log.error("Exception while loading decoder XML file: "+decoderFile.getFilename(), e); return; }
            modelElem = decoderFile.getModelElement();
            decoderFile.loadVariableModel(decoderRoot.getChild("decoder"), variableModel);
            rosterPanel.setVisible(true);
        } else {
            rosterPanel.setVisible(false);
            return;
        }
        Element programmerRoot;
        XmlFile pf = new XmlFile(){};  // XmlFile is abstract
        try {
            programmerRoot = pf.rootFromName("programmers"+File.separator+"Basic.xml");
            Element base;
            if ( (base = programmerRoot.getChild("programmer")) == null) {
                log.error("xml file top element is not programmer");
                return;
            }
            // for all "pane" elements in the programmer
            List<Element> paneList = base.getChildren("pane");
            if (log.isDebugEnabled()) log.debug("will process "+paneList.size()+" pane definitions");
            String name = jmri.util.jdom.LocaleSelector.getAttribute(paneList.get(0), "name");
            progPane = new ThisProgPane(this, name, paneList.get(0), cvModel, iCvModel, variableModel, modelElem);

            progPane.setVariableValue("Short Address", cv1);
            progPane.setVariableValue("Long Address", longAddress);
            progPane.setCVValue(29, cv29);
            progPane.setCVValue(19, cv19);
            rosterPanel.add(progPane, BorderLayout.CENTER);
            rosterPanel.revalidate();
            rosterPanel.setVisible(true);
            f.getContentPane().repaint();
            f.repaint();
            f.pack();
            return;
        } catch (Exception e) {
            log.error("exception reading programmer file: ", e);
            e.printStackTrace();
        }
    }
    
    JButton saveBasicRoster;
    
   void saveRosterEntry(){
        if(rosterIdField.getText().equals(jmri.jmrit.symbolicprog.SymbolicProgBundle.bundle().getString("LabelNewDecoder"))){
            return;
        }
        re = new RosterEntry();
        re.setDecoderFamily(decoderFile.getFamily());
        re.setDecoderModel(decoderFile.getModel());
        re.setId(rosterIdField.getText());
        re.setDccAddress(""+address);
        re.setLongAddress(!shortAddr);
        re.ensureFilenameExists();
        re.writeFile(cvModel, iCvModel, variableModel );
        Roster.instance().addEntry(re);
        Roster.writeRosterFile();
    }
    
    public boolean isBusy() { return false; }
    
    public void paneFinished() {}
    
    /**
     * Enable the read/write buttons.
     * <p>
     * In addition, if a programming mode pane is 
     * present, it's "set" button is enabled.
     * 
     * @param enable Are reads possible? If false, so not enable
     * the read buttons.
     */
    public void enableButtons(boolean enable) {}

    public void prepGlassPane(javax.swing.AbstractButton activeButton) {}

    public jmri.util.BusyGlassPane getBusyGlassPane() { return new jmri.util.BusyGlassPane(new ArrayList<JComponent>(), new ArrayList<Rectangle>(), rosterPanel, f);}
    
    class ThisProgPane extends PaneProgPane{
        
        public ThisProgPane(PaneContainer parent, String name, Element pane, CvTableModel cvModel, IndexedCvTableModel icvModel, VariableTableModel varModel, Element modelElem){
            super(parent, name, pane, cvModel, icvModel, varModel, modelElem);
            bottom.remove(readChangesButton);
            bottom.remove(writeChangesButton);
            writeAllButton.setText(jmri.jmrit.symbolicprog.SymbolicProgBundle.bundle().getString("ButtonWrite"));
            readAllButton.setText(jmri.jmrit.symbolicprog.SymbolicProgBundle.bundle().getString("ButtonRead"));
            bottom.add(saveBasicRoster);
            bottom.revalidate();
            readAllButton.removeItemListener(l2);
            readAllButton.addItemListener(l2 = new ItemListener() {
                public void itemStateChanged (ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        readAllButton.setText(rbt.getString("ButtonStopReadSheet"));
                        if (container.isBusy() == false) {
                            prepReadPane(false);
                            prepGlassPane(readAllButton);
                            container.getBusyGlassPane().setVisible(true);
                            readPaneAll();
                        }
                    } else {
                        stopProgramming();
                        readAllButton.setText(rbt.getString("ButtonRead"));
                        if (container.isBusy()) {
                            readAllButton.setEnabled(false);
                        }
                    }
                }
            });
            writeAllButton.removeItemListener(l4);
            writeAllButton.addItemListener(l4 = new ItemListener() {
                public void itemStateChanged (ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        writeAllButton.setText(rbt.getString("ButtonStopWriteSheet"));
                        if (container.isBusy() == false) {
                            prepWritePane(false);
                            prepGlassPane(writeAllButton);
                            container.getBusyGlassPane().setVisible(true);
                            writePaneAll();
                        }
                    } else {
                        stopProgramming();
                        writeAllButton.setText(rbt.getString("ButtonWrite"));
                        if (container.isBusy()) {
                            writeAllButton.setEnabled(false);
                        }
                    }
                }
            });
        }
        
        public void setCVValue(int cv, int value){
            if(_cvModel.getCvByNumber(cv)!=null){
                (_cvModel.getCvByNumber(cv)).setValue(value);
                (_cvModel.getCvByNumber(cv)).setState(AbstractValue.READ);
            }
        }
        
        public void setVariableValue(String variable, int value){
            if(_varModel.findVar(variable)!=null){
                _varModel.findVar(variable).setIntValue(value);
                _varModel.findVar(variable).setState(AbstractValue.READ);
            }
        }
        
        public void dispose(){
            bottom.remove(saveBasicRoster);
            super.dispose();
        }
    
    }
    
    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PaneProgAction.class.getName());

}

/* @(#)PaneProgAction.java */

