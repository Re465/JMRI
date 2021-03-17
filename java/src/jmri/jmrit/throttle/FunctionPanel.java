package jmri.jmrit.throttle;

import java.awt.*;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import jmri.DccThrottle;
import jmri.InstanceManager;
import jmri.LocoAddress;
import jmri.Throttle;
import jmri.jmrit.roster.Roster;
import jmri.jmrit.roster.RosterEntry;
import jmri.util.swing.WrapLayout;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JInternalFrame that contains buttons for each decoder function.
 */
public class FunctionPanel extends JInternalFrame implements FunctionListener, java.beans.PropertyChangeListener, AddressListener {

    private static final int DEFAULT_FUNCTION_BUTTONS = 17;
    private DccThrottle mThrottle;
    
    private JPanel mainPanel;
    private FunctionButton[] functionButtons;

    private AddressPanel addressPanel = null; // to access roster infos

    /**
     * Constructor
     */
    public FunctionPanel() {
        initGUI();
    }

    public void destroy() {
        if (addressPanel != null) {
            addressPanel.removeAddressListener(this);
        }
        if (mThrottle != null) {
            mThrottle.removePropertyChangeListener(this);
            mThrottle = null;
        }
    }

    public FunctionButton[] getFunctionButtons() {
        return Arrays.copyOf(functionButtons, functionButtons.length);
    }

    /**
     * Get notification that a function has changed state.
     *
     * @param functionNumber The function that has changed.
     * @param isSet          True if the function is now active (or set).
     */
    @Override
    public void notifyFunctionStateChanged(int functionNumber, boolean isSet) {
        if (mThrottle != null) {
            mThrottle.setFunction(functionNumber, isSet);
        }
    }

    /**
     * Get notification that a function's lockable status has changed.
     *
     * @param functionNumber The function that has changed (0-28).
     * @param isLockable     True if the function is now Lockable (continuously
     *                       active).
     */
    @Override
    public void notifyFunctionLockableChanged(int functionNumber, boolean isLockable) {
        log.debug("notifyFnLockableChanged: fNumber={} isLockable={} " ,functionNumber, isLockable);
        if (mThrottle != null) {
            // throttle can be null when loading throttle layout
            mThrottle.setFunctionMomentary(functionNumber, !isLockable);
        }
    }

    /**
     * Enable or disable all the buttons.
     * @param isEnabled true to enable, false to disable.
     */
    @Override
    public void setEnabled(boolean isEnabled) {
        for (FunctionButton functionButton : functionButtons) {
            functionButton.setEnabled(isEnabled);
        }
    }

    public void setEnabled() {
        setEnabled(mThrottle != null);
    }

    public void setAddressPanel(AddressPanel addressPanel) {
        this.addressPanel = addressPanel;
    }

    public void saveFunctionButtonsToRoster(RosterEntry rosterEntry) {
        log.debug("saveFunctionButtonsToRoster");
        if (rosterEntry == null) {
            return;
        }
        for (FunctionButton functionButton : functionButtons) {
            int functionNumber = functionButton.getIdentity();
            String text = functionButton.getButtonLabel();
            boolean lockable = functionButton.getIsLockable();
            String imagePath = functionButton.getIconPath();
            String imageSelectedPath = functionButton.getSelectedIconPath();
            if (functionButton.isDirty() && !text.equals(rosterEntry.getFunctionLabel(functionNumber))) {
                functionButton.setDirty(false);
                if (text.isEmpty()) {
                    text = null;  // reset button text to default
                }
                rosterEntry.setFunctionLabel(functionNumber, text);
            }
            if (rosterEntry.getFunctionLabel(functionNumber) != null ) {
                if( lockable != rosterEntry.getFunctionLockable(functionNumber)) {
                    rosterEntry.setFunctionLockable(functionNumber, lockable);
                }
                if ( imagePath.compareTo(rosterEntry.getFunctionImage(functionNumber)) != 0) {
                    rosterEntry.setFunctionImage(functionNumber, imagePath);
                }
                if ( imageSelectedPath.compareTo(rosterEntry.getFunctionSelectedImage(functionNumber)) != 0) {
                    rosterEntry.setFunctionSelectedImage(functionNumber, imageSelectedPath);
                }
            }
        }
        Roster.getDefault().writeRoster();
    }
    
    /**
     * Place and initialize all the buttons.
     */
    private void initGUI() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new WrapLayout(FlowLayout.CENTER, 2, 2));
        resetFnButtons();
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getViewport().setOpaque(false); // container already gets this done (for play/edit mode)
        scrollPane.setOpaque(false);
        Border empyBorder = new EmptyBorder(0,0,0,0); // force look'n feel, no border
        scrollPane.setViewportBorder( empyBorder ); 
        scrollPane.setBorder( empyBorder );
        scrollPane.setWheelScrollingEnabled(false); // already used by speed slider
        setContentPane(scrollPane);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Make sure that all function buttons are being displayed if buttons label
     * loaded from a roster entry, update buttons accordingly
     */
    public void resetFnButtons() {
        final ThrottlesPreferences preferences = InstanceManager.getDefault(ThrottleFrameManager.class).getThrottlesPreferences();
        mainPanel.removeAll();
        if (this.mThrottle == null) {
            functionButtons = new FunctionButton[DEFAULT_FUNCTION_BUTTONS];
        } else {
            functionButtons = new FunctionButton[mThrottle.getFunctions().length];
        }
        // Buttons names, ids,
        for (int i = 0; i < functionButtons.length; i++) {
            functionButtons[i] = new FunctionButton();
            mainPanel.add(functionButtons[i]);            
            functionButtons[i].setThrottle(mThrottle);
            functionButtons[i].setIdentity(i);
            functionButtons[i].addFunctionListener(this);
            functionButtons[i].setButtonLabel( i<3 ?
                Bundle.getMessage(Throttle.getFunctionString(i))
                : Throttle.getFunctionString(i) );
            functionButtons[i].setDisplay(true);
            if ((i < 3) && preferences.isUsingExThrottle() && preferences.isUsingFunctionIcon()) {
                switch (i) {
                    case 0:
                        functionButtons[i].setIconPath("resources/icons/throttles/Light.png");
                        functionButtons[i].setSelectedIconPath("resources/icons/throttles/LightOn.png");
                        break;
                    case 1:
                        functionButtons[i].setIconPath("resources/icons/throttles/Bell.png");
                        functionButtons[i].setSelectedIconPath("resources/icons/throttles/BellOn.png");
                        break;
                    case 2:
                        functionButtons[i].setIconPath("resources/icons/throttles/Horn.png");
                        functionButtons[i].setSelectedIconPath("resources/icons/throttles/HornOn.png");
                        break;
                    default:
                        break;
                }
            } else {
                functionButtons[i].setIconPath(null);
                functionButtons[i].setSelectedIconPath(null);
            }
            functionButtons[i].updateLnF();

            // always display f0, F1 and F2
            if (i < 3) {
                functionButtons[i].setVisible(true);
            }
        }
        setFnButtons();
        repaint();
    }

    // Update buttons value from slot + load buttons definition from roster if any
    private void setFnButtons() {
        final ThrottlesPreferences preferences = InstanceManager.getDefault(ThrottleFrameManager.class).getThrottlesPreferences();
        if (mThrottle != null) {
            if (addressPanel == null) {
                return;
            }
            RosterEntry rosterEntry = addressPanel.getRosterEntry();
            if (rosterEntry != null) {
                log.debug("RosterEntry found: {}", rosterEntry.getId());
            }
            for (int i = 0; i < functionButtons.length; i++) {
                functionButtons[i].setEnabled(true);
                functionButtons[i].setIdentity(i); // full reset of function
                functionButtons[i].setThrottle(mThrottle);
                functionButtons[i].setState(mThrottle.getFunction(i)); // reset button state
                functionButtons[i].setIsLockable(!mThrottle.getFunctionMomentary(i));
                if (rosterEntry != null) { // from here, update button text with roster data
                    String text = rosterEntry.getFunctionLabel(i);
                    if (text != null) {
                        functionButtons[i].setDisplay(true);
                        functionButtons[i].setButtonLabel(text);
                        if (preferences.isUsingExThrottle() && preferences.isUsingFunctionIcon()) {
                            functionButtons[i].setIconPath(rosterEntry.getFunctionImage(i));
                            functionButtons[i].setSelectedIconPath(rosterEntry.getFunctionSelectedImage(i));
                        } else {
                            functionButtons[i].setIconPath(null);
                            functionButtons[i].setSelectedIconPath(null);
                        }
                        functionButtons[i].setIsLockable(rosterEntry.getFunctionLockable(i));
                        functionButtons[i].updateLnF();
                    } else if (preferences.isUsingExThrottle()
                            && preferences.isHidingUndefinedFuncButt()) {
                        functionButtons[i].setDisplay(false);
                        functionButtons[i].setVisible(false);
                    }
                }                
            }
        }
    }

    /**
     * Update the state of this panel if any of the functions change.
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(java.beans.PropertyChangeEvent e) {
        if (mThrottle!=null){
            for (int i = 0; i < mThrottle.getFunctions().length; i++) {
                if (e.getPropertyName().equals(Throttle.getFunctionString(i))) {
                    setButtonByFuncNumber(i,false,(Boolean) e.getNewValue());
                } else if (e.getPropertyName().equals(Throttle.getFunctionMomentaryString(i))) {
                    setButtonByFuncNumber(i,true,!(Boolean) e.getNewValue());
                }
            }
        }
    }
    
    private void setButtonByFuncNumber(int function, boolean lockable, boolean newVal){
        for (FunctionButton button : functionButtons) {
            if (button.getIdentity() == function) {
                if (lockable) {
                    button.setIsLockable(newVal);
                } else {
                    button.setState(newVal);
                }
            }
        }
    }

    /**
     * Collect the prefs of this object into XML Element.
     * <ul>
     * <li> Window prefs
     * <li> Each button has id, text, lock state.
     * </ul>
     *
     * @return the XML of this object.
     */
    public Element getXml() {
        Element me = new Element("FunctionPanel"); // NOI18N
        java.util.ArrayList<Element> children = new java.util.ArrayList<>(1 + functionButtons.length);
        children.add(WindowPreferences.getPreferences(this));
        for (FunctionButton functionButton : functionButtons) {
            children.add(functionButton.getXml());
        }
        me.setContent(children);
        return me;
    }

    /**
     * Set the preferences based on the XML Element.
     * <ul>
     * <li> Window prefs
     * <li> Each button has id, text, lock state.
     * </ul>
     *
     * @param e The Element for this object.
     */
    public void setXml(Element e) {
        Element window = e.getChild("window");
        WindowPreferences.setPreferences(this, window);

        java.util.List<Element> buttonElements = e.getChildren("FunctionButton");

        if (buttonElements != null && buttonElements.size() > 0) {
            // just in case
            if ( buttonElements.size() > functionButtons.length) {
                mainPanel.removeAll();
                functionButtons = new FunctionButton[buttonElements.size()];                
                for (int i = 0; i < functionButtons.length; i++) {
                    functionButtons[i] = new FunctionButton();
                    mainPanel.add(functionButtons[i]);            
                    functionButtons[i].setThrottle(mThrottle);
                }
            }
            int i = 0;
            for (Element buttonElement : buttonElements) {                
                functionButtons[i++].setXml(buttonElement);
            }
        }
    }

    /**
     * Get notification that a throttle has been found as we requested.
     *
     * @param t An instantiation of the DccThrottle with the address requested.
     */
    @Override
    public void notifyAddressThrottleFound(DccThrottle t) {
        log.debug("Throttle found");
        mThrottle = t;        
        mThrottle.addPropertyChangeListener(this);
        resetFnButtons();
        setEnabled(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyAddressReleased(LocoAddress la) {
        log.debug("Throttle released");        
        if (mThrottle != null) {
            mThrottle.removePropertyChangeListener(this);
        }
        mThrottle = null;
        resetFnButtons(); 
        setEnabled(false);
    }

    /**
     * Ignored.
     * {@inheritDoc}
     */
    @Override
    public void notifyAddressChosen(LocoAddress l) {
    }

    /**
     * Ignored.
     * {@inheritDoc}
     */
    @Override
    public void notifyConsistAddressChosen(int newAddress, boolean isLong) {
    }

    /**
     * Ignored.
     * {@inheritDoc}
     */
    @Override
    public void notifyConsistAddressReleased(int address, boolean isLong) {
    }

    /**
     * Ignored.
     * {@inheritDoc}
     */
    @Override
    public void notifyConsistAddressThrottleFound(DccThrottle throttle) {
    }

    private final static Logger log = LoggerFactory.getLogger(FunctionPanel.class);
}
