/*******************************************************************************
 * Copyright (c) 2013, 2017 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam
 * Grahovac, Jarkko Heidenwag, Benedikt Markt, Jaqueline Patzek, Sebastian
 * Sieber, Fabian Toth, Patrick Wickenhäuser, Aliaksei Babkovich, Aleksander
 * Zotov).
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package xstampp.astpa.controlstructure.controller.commands;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import xstampp.astpa.controlstructure.CSEditor;
import xstampp.astpa.controlstructure.figure.FigureConstraints;
import xstampp.astpa.controlstructure.figure.TextFieldFigure;
import xstampp.astpa.model.controlstructure.interfaces.IRectangleComponent;
import xstampp.astpa.model.interfaces.IControlStructureEditorDataModel;

/**
 *
 *
 *
 * @author Aliaksei Babkovich, Lukas Balzer
 * @version 1.0
 */
public class ComponentFormatCommand extends
ControlStructureAbstractCommand {

	private IRectangleComponent model;
	private Rectangle layout;
	private Rectangle oldLayout;
	private int heightConstraint;
	private int widthConstraint;

	/**
	 *
	 * @author Lukas Balzer
	 * @param model
	 *            The dataModel which contains all model classes
	 * @param stepID
	 *            the stepEditor ID
	 */
  public ComponentFormatCommand(UUID rootId,
      IControlStructureEditorDataModel model, String stepID) {
    super(rootId,model,stepID);
		setMinConstraint(TextFieldFigure.TEXTBOX_FIGURE_DEFSIZE);
	}

	@Override
	public void execute() {
	  super.execute();
	  getDataModel().lockUpdate();
	  
		this.getDataModel().changeComponentLayout(this.model.getId(),
				this.layout, this.getStepID().equals(CSEditor.ID));

	}

	private void formatComponent(IRectangleComponent component, final boolean step0) {
	  List<IRectangleComponent> children = component.getChildren(step0);
	  Collections.sort(children, new Comparator<IRectangleComponent>() {

      @Override
      public int compare(IRectangleComponent o1, IRectangleComponent o2) {
        Rectangle o1rect = o1.getLayout(step0);
        double pyt1 = Math.sqrt(Math.pow(o1rect.y,2) + Math.pow(o1rect.x,2));
        Rectangle o2rect = o2.getLayout(step0);
        double pyt2 = Math.sqrt(Math.pow(o2rect.y,2) + Math.pow(o2rect.x,2));
        return (int) (pyt1 - pyt2);
      }
    });
	  
  }
	
	private void fitGrid(List<IRectangleComponent> children, FigureConstraints constraint) {
	  int rows = 2;
	  int cols = children.size() / rows;
	  if(cols > 4) {
	    cols /= 2;
	    rows *= 2;
	  }
    for (IRectangleComponent component : children) {
      
    }
  }
	/**
	 * set the minimum width/height for the component
	 *
	 * @author Lukas Balzer
	 *
	 * @param constraint
	 *            the minimum width and height to which the component can be
	 *            scaled
	 */
	public void setMinConstraint(Dimension constraint) {
		this.widthConstraint = constraint.width;
		this.heightConstraint = constraint.height;
	}

	/**
	 *
	 * @author Lukas Balzer
	 *
	 * @param rect
	 *            the new layout constraint
	 */
	public void setConstraint(Rectangle rect) {
		this.layout = new Rectangle();
//		this.layout.width = Math.max(rect.width, this.widthConstraint);
//		this.layout.height = Math.max(rect.height, this.heightConstraint);
		this.layout.width = rect.width;
		this.layout.height =rect.height;
		this.layout.x = Math.max(rect.x, 0);
		this.layout.y = Math.max(rect.y, 0);
	}

	/**
	 *
	 * @author Lukas
	 *
	 * @param layout
	 *            the layout
	 */
	public void setOldLayout(Rectangle layout) {
		this.oldLayout = layout;
	}

	@Override
	public boolean canExecute() {
		return true;
	}

	/**
	 *
	 * @author Lukas Balzer
	 *
	 * @param model
	 *            the new model
	 */
	public void setModel(Object model) {
		this.model = (IRectangleComponent) model;
		this.oldLayout = ((IRectangleComponent) model).getLayout(this
				.getStepID().equals(CSEditor.ID));

	}

	@Override
	public void undo() {
		this.getDataModel().changeComponentLayout(this.model.getId(),
				this.oldLayout, this.getStepID().equals(CSEditor.ID));
	}

}
