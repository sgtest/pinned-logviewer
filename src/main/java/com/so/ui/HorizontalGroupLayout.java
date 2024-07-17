package com.so.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.GridLayout;

public class HorizontalGroupLayout extends GridLayout{
	private static final long serialVersionUID = 1L;
	private List<AbsoluteLayout> absoluteLayouts;

	public List<AbsoluteLayout> getAbsoluteLayouts() {
		return absoluteLayouts;
	}
/**
 * 水平布局组
 * @param widths 各列宽度的比例
 */
	public HorizontalGroupLayout(Integer[] widths){
		super();
//		this.setWidth("100%");
//		this.setHeight("300px");
		this.setSizeFull();
		if (widths.length>0){
			Integer totalWidth = 0;
			for (Integer width:widths){
				totalWidth=totalWidth+width;
			}
			this.setColumns(totalWidth);
			if (totalWidth>0){
				absoluteLayouts=new ArrayList<>();
				int i=0;
				for (Integer width:widths){
					AbsoluteLayout absoluteLayout=new AbsoluteLayout();
					absoluteLayout.setHeight("100%");
					absoluteLayout.setWidth("100%");
					absoluteLayouts.add(absoluteLayout);
					this.addComponent(absoluteLayout,i,0,i+width-1,0);
					i=i+width;
				}
//				Label blank=new Label();
//				this.addComponent(blank);
//				this.setExpandRatio(blank, 1);
				
			}
			
		}
		
	}

}
