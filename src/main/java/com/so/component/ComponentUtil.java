package com.so.component;

import com.so.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.so.ui.LogCheckView;
import com.so.entity.Menu;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

@Service
public class ComponentUtil implements ApplicationContextAware {

	
    private static Logger logger = LoggerFactory.getLogger(ComponentUtil.class);

    private static final String CLASSPATH = "com.so.component.";
    public static ApplicationContext applicationContext;

    public static Label SpaceLine(int height) {
        Label sp = new Label();
        sp.setHeight(height + "px");
        return sp;

    }

    public static Label createLabel(String caption, String width, String height, String color) {
        Label lb = new Label(caption);
        lb.setWidth(width);
        if (height != null) {
            lb.setHeight(height);
        }
        if (color.equals("gray")) {
            lb.addStyleName("gray-label");
        } else if (color.equals("white")) {
            lb.addStyleName("white-label");
        }
        return lb;
    }


    /**
     * createComponent:(工厂模式生成对应的CommonComponent)；<br/>
     * className；<br/>
     * CommonComponent)；<br/>
     * 
     * @author baiguomin
     */
    public static CommonComponent createComponent(Menu menu) {
        String className = null;
        if (menu == null || menu.getNameClass() == null) {
            return null;
        } else {
            className = menu.getNameClass();
        }
        Class<?> clazz = null;
        try {
            clazz = Class.forName(CLASSPATH + className);
        } catch (ClassNotFoundException e) {
            logger.error("className:ComponentFactory,methodName:createComponent,message:{}", e);
        }

        if (clazz == null)
            return null;

        CommonComponent bean = null;
        try {
            bean = (CommonComponent) applicationContext.getBean(clazz);
        } catch (BeansException e1) {
        	e1.printStackTrace();
            try {
                bean = (CommonComponent) clazz.newInstance();
                return bean;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            e1.printStackTrace();
        }
        if (bean == null) {
            try {
                bean = (CommonComponent) clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return bean;
    }
    /**
     * createComponent:(工厂模式生成对应的CommonComponent)；<br/>
     * className；<br/>
     * CommonComponent)；<br/>
     * 
     */
    public static CommonComponent createComponentUseClassName(String className) {
    	if (className == null) {
    		return null;
    	}
    	Class<?> clazz = null;
    	try {
    		clazz = Class.forName(CLASSPATH + className);
    	} catch (ClassNotFoundException e) {
    		logger.error("className:ComponentFactory,methodName:createComponent,message:{}", e);
    	}
    	
    	if (clazz == null)
    		return null;
    	
    	CommonComponent bean = null;
    	try {
    		bean = (CommonComponent) applicationContext.getBean(clazz);
    	} catch (BeansException e1) {
    		e1.printStackTrace();
    		try {
    			bean = (CommonComponent) clazz.newInstance();
    			return bean;
    		} catch (InstantiationException e) {
    			e.printStackTrace();
    		} catch (IllegalAccessException e) {
    			e.printStackTrace();
    		}
    		e1.printStackTrace();
    	}
    	if (bean == null) {
    		try {
    			bean = (CommonComponent) clazz.newInstance();
    		} catch (InstantiationException e) {
    			e.printStackTrace();
    		} catch (IllegalAccessException e) {
    			e.printStackTrace();
    		}
    	}
    	return bean;
    }

    
    public static CommonComponent getComponentByClassName(String className ){
        Class<?> clazz = null;
        try {
            clazz = Class.forName(CLASSPATH + className);
        } catch (ClassNotFoundException e) {
            logger.error("className:ComponentFactory,methodName:createComponent,message:{}", e);
        }

        if (clazz == null)
            return null;
       CommonComponent bean = null;
        try {
            bean = (CommonComponent) applicationContext.getBean(clazz);
        } catch (BeansException e1) {
        	e1.printStackTrace();
            try {
                bean = (CommonComponent) clazz.newInstance();
                return bean;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            e1.printStackTrace();
        }
        if (bean == null) {
            try {
                bean = (CommonComponent) clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return bean;

	}

	public static LogCheckView getView() {
		return ComponentUtil.applicationContext.getBean(LogCheckView.class);
	}


    /**
     * 创建带数据合法性检查的ComboBox
     * @param <T>
     * 
     * @param width
     *            组件宽度
     * @param noEmpty
     *            true表示不能为空
     * @return
     */

    public static <T> ComboBox<T> newComboBox(String lenth, boolean noEmpty) {
        ComboBox<T> combo = new ComboBox<T>();
        combo.setWidth(lenth);
        if (noEmpty) {
            combo.setEmptySelectionAllowed(false);
        } else {
            combo.setEmptySelectionAllowed(true);

        }
        return combo;
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		ComponentUtil.applicationContext = applicationContext;
	}

	public static String getCurrentUserName() {
		return VaadinSession.getCurrent().getAttribute("userName").toString();
	}
    public static User getCurrentUser() {
		return (User)VaadinSession.getCurrent().getAttribute("user");
	}
}
