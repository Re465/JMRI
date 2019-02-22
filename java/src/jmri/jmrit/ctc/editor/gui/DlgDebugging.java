package jmri.jmrit.ctc.editor.gui;

import jmri.jmrit.ctc.editor.code.AwtWindowProperties;
import jmri.jmrit.ctc.editor.code.CommonSubs;
import jmri.jmrit.ctc.editor.code.ProgramProperties;
import jmri.jmrit.ctc.ctcserialdata.OtherData;

/**
 *
 * @author Gregory J. Bedlek Copyright (C) 2018, 2019
 */
public class DlgDebugging extends javax.swing.JDialog {

    /**
     * Creates new form dlgProperties
     */
    private static final String FORM_PROPERTIES = "DlgDebugging";   // NOI18N
    private final AwtWindowProperties _mAwtWindowProperties;
    private final OtherData _mOtherData;
    private boolean _mClosedNormally = false;
    public boolean closedNormally() { return _mClosedNormally; }
    
    private String _mCTCDebugSystemReloadInternalSensorOrig;
    private String _mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensorOrig;
    
    private void initOrig(OtherData otherData) {
        _mCTCDebugSystemReloadInternalSensorOrig = otherData._mCTCDebugSystemReloadInternalSensor;
        _mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensorOrig = otherData._mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensor;
    }
    private boolean dataChanged() {
        if (!_mCTCDebugSystemReloadInternalSensorOrig.equals(_mCTCSystemReloadInternalSensor.getText())) return true;
        if (!_mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensorOrig.equals(_mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensor.getText())) return true;
        return false;
    }
    
    public DlgDebugging(java.awt.Frame parent, boolean modal, AwtWindowProperties awtWindowProperties, OtherData otherData) {
        super(parent, modal);
        initComponents();
        _mAwtWindowProperties = awtWindowProperties;
        _mOtherData = otherData;
        _mCTCSystemReloadInternalSensor.setText(otherData._mCTCDebugSystemReloadInternalSensor);
        _mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensor.setText(otherData._mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensor);
        initOrig(otherData);
        _mAwtWindowProperties.setWindowState(this, FORM_PROPERTIES);        
        this.getRootPane().setDefaultButton(_mSaveAndClose);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")  // NOI18N
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        _mSaveAndClose = new javax.swing.JButton();
        _mCTCSystemReloadInternalSensor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        _mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensor = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getMessage("TitleDlgDeb"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        _mSaveAndClose.setText(Bundle.getMessage("ButtonSaveClose"));
        _mSaveAndClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mSaveAndCloseActionPerformed(evt);
            }
        });

        jLabel3.setText(Bundle.getMessage("LabelDlgDebReload"));

        jLabel4.setText(Bundle.getMessage("InfoDlgDebReload"));

        jLabel1.setText(Bundle.getMessage("LabelDlgDebRules"));

        jLabel2.setText(Bundle.getMessage("InfoDlgDebRules1"));

        jLabel5.setText(Bundle.getMessage("InfoDlgDebRules2"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(_mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensor)
                            .addComponent(_mCTCSystemReloadInternalSensor)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(246, 246, 246)
                                .addComponent(_mSaveAndClose))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel5))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel2))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mCTCSystemReloadInternalSensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(_mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(_mSaveAndClose)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        _mAwtWindowProperties.saveWindowState(this, FORM_PROPERTIES);
        if (CommonSubs.allowClose(this, dataChanged())) dispose();
    }//GEN-LAST:event_formWindowClosing

    private void _mSaveAndCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mSaveAndCloseActionPerformed
        _mOtherData._mCTCDebugSystemReloadInternalSensor = _mCTCSystemReloadInternalSensor.getText();
        _mOtherData._mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensor = _mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensor.getText();
        _mClosedNormally = true;
        _mAwtWindowProperties.saveWindowState(this, FORM_PROPERTIES);
        dispose();
    }//GEN-LAST:event__mSaveAndCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField _mCTCDebug_TrafficLockingRuleTriggeredDisplayInternalSensor;
    private javax.swing.JTextField _mCTCSystemReloadInternalSensor;
    private javax.swing.JButton _mSaveAndClose;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    // End of variables declaration//GEN-END:variables
}
