package com.so.component.util;

import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Window;


/**
 * Superclass for popup windows. 
 * Popup windows that inherit from this class can be closed using the 'escape' key.
 */
public class PopupWindow extends Window {

  private static final long serialVersionUID = 1L;
  
  public PopupWindow() {
    
  }
  
  public PopupWindow(String caption) {
    super(caption);
  }

  @Override
  public void attach() {
    super.attach();
    // setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
  }
  
  @Override
  public void setParent(HasComponents parent) {
    super.setParent(parent);
  }
  
}
