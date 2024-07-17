package com.so.component.util;

import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;


/**
 * ConfirmationEvent
 */
public class ConfirmationEvent extends Event {

  private static final long serialVersionUID = 1L;
  
  private boolean confirmed;
  
  public ConfirmationEvent(Component source, boolean confirmed) {
    super(source);
    this.confirmed = confirmed;
  }
  
  public boolean isConfirmed() {
    return confirmed;
  }
}
