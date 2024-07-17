package com.so.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Page;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class CustomErrorHandler implements ErrorHandler {

	private static final long serialVersionUID = -2024738135613837445L;

	private static final Logger logger = LoggerFactory.getLogger(CustomErrorHandler.class);

	@Override
	public void error(ErrorEvent event) {
		// Finds the original source of the error/exception
		AbstractComponent component = DefaultErrorHandler.findAbstractComponent(event);
		if (component != null) {
			ErrorMessage errorMessage = getErrorMessageForException(event.getThrowable());
			if (errorMessage != null) {
				logger.error("报错信息："+errorMessage);
				logger.error("详细错误信息：",event.getThrowable());
				// component.setComponentError(errorMessage);
				// Notification.show(errorMessage.getFormattedHtmlMessage(),
				// Notification.Type.WARNING_MESSAGE);
//				new Notification(null, errorMessage.getFormattedHtmlMessage(), Type.WARNING_MESSAGE, true)
//						.show(Page.getCurrent());
				new Notification(null, "系统繁忙，请稍后重试", Type.WARNING_MESSAGE, true)
				.show(Page.getCurrent());
				return;
			}
		}
		DefaultErrorHandler.doDefault(event);
	}

	private static ErrorMessage getErrorMessageForException(Throwable t) {

//	    PersistenceException persistenceException = getCauseOfType(t, PersistenceException.class);
//	    可根据异常类型获取不同message；persistenceException.getLocalizedMessage()
		RuntimeException runtimeException = getCauseOfType(t, RuntimeException.class);
		if (runtimeException !=null) {
			return new UserError(runtimeException.getLocalizedMessage(), AbstractErrorMessage.ContentMode.TEXT, ErrorLevel.ERROR);
		}
		logger.info("系统繁忙，请稍后重试");
	      return new UserError("系统繁忙，请稍后重试", AbstractErrorMessage.ContentMode.TEXT, ErrorLevel.ERROR);
	    
	}

	private static <T extends Throwable> T getCauseOfType(Throwable th, Class<T> type) {
		while (th != null) {
			if (type.isAssignableFrom(th.getClass())) {
				return (T) th;
			} else {
				th = th.getCause();
			}
		}
		return null;
	}

}
