/*******************************************************************************
 * Copyright (c) 2013 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam
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

package acast.controlstructure.controller.editparts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IClippingStrategy;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Translatable;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.swt.SWT;

import acast.controlstructure.controller.policys.CSConnectionDeleteEditPolicy;
import acast.controlstructure.figure.ConnectionFigure;
import acast.controlstructure.figure.IAnchorFigure;
import acast.model.controlstructure.components.CSConnection;
import acast.model.controlstructure.interfaces.IAnchor;
import acast.model.controlstructure.interfaces.IConnection;
import acast.model.interfaces.IControlStructureEditorDataModel;

/**
 * 
 * Editpart for connections.
 * 
 * @author Aliaksei Babkovich
 * @version 1.0
 */
public class CSConnectionEditPart extends AbstractConnectionEditPart implements IRelative {

	private static final float DASH = 4;
	private IAnchorFigure targetAnchor;
	private IAnchorFigure sourceAnchor;
	private IControlStructureEditorDataModel dataModel;
	private final UUID ownID;
	private final String stepId;
	private List<IConnectable> members;

	/**
	 * This constructor is used to load a connection EditPart from a given model
	 * 
	 * @author Lukas Balzer
	 * 
	 * @param model
	 *            The DataModel which contains all model classes
	 * @param source
	 *            the source anchor which is given from the Anchor Model
	 * @param target
	 *            the target anchor which is given from the Anchor Model
	 * @param id
	 *            the UUID which is given from the model
	 * @param stepId
	 *            this steps id
	 */
	public CSConnectionEditPart(IControlStructureEditorDataModel model,
			IAnchorFigure source, IAnchorFigure target, UUID id, String stepId) {
		super();
		this.members = new ArrayList<>();
		this.stepId = stepId;
		this.registerAccessibility();
		this.activate();
		this.dataModel = model;
		this.ownID = id;
		this.sourceAnchor = source;
		this.targetAnchor = target;
		

	}
	
	@Override
	protected IFigure createFigure() {

		PolylineConnection connection = new ConnectionFigure(getId());
		connection.addMouseMotionListener(this);
		connection.setLayoutManager(new XYLayout());
		connection.setLineWidth(1);
		connection.setTolerance(15);
		switch (((CSConnection) this.getModel()).getConnectionType()) {
		case ARROW_SIMPLE: {
			connection.setLineStyle(SWT.LINE_SOLID);
			break;
		}
		case ARROW_DASHED: {
			connection.setLineDash(new float[] { CSConnectionEditPart.DASH });
			break;
		}
		default:
			return null;
		}
		return connection;
	}

	
	@Override
	public void refresh() {
		if (this.dataModel.getConnection(((CSConnection) this.getModel())
				.getId()) == null) {
			this.deactivate();
			this.getViewer().getEditPartRegistry().remove(this);
		} else {
			
			super.refresh();
		}
		this.refreshChildren();
		this.getViewer().getControl().redraw();
		
		
		for (Object child : this.getChildren()) {
			((IControlStructureEditPart) child).refresh();
		}
	}

	@Override
	protected List getModelChildren() {
		return new ArrayList<>();
	}
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new CSConnectionDeleteEditPolicy(this.dataModel, this.stepId));
		this.installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
	}

	@Override
	public IFigure getFeedback() {
		return this.getConnectionFigure().getFeedback();
	}
	@Override
	public IFigure getFeedback(IConnectable member) {
		return this.getConnectionFigure().getFeedback(member);
	}
	@Override
	public IFigure getFeedback(Rectangle bounds) {
		return this.getConnectionFigure().getFeedback(bounds);
	}
	@Override
	protected ConnectionAnchor getTargetConnectionAnchor() {
		IAnchor target = this.dataModel.getConnection(this.ownID)
				.getTargetAnchor();
		this.targetAnchor.updateAnchor(target);
		this.getFigure().revalidate();
		return this.targetAnchor;
	}

	@Override
	protected ConnectionAnchor getSourceConnectionAnchor() {
		IAnchor source = this.dataModel.getConnection(this.ownID)
				.getSourceAnchor();
		this.sourceAnchor.updateAnchor(source);
		this.getFigure().revalidate();
		return this.sourceAnchor;
	}

	/**
	 * This getter is called to get the Target Anchor the connection is
	 * momentarily connected with
	 * 
	 * @author Lukas Balzer
	 * 
	 * @return The Anchor at which the connection ends
	 */
	public ConnectionAnchor getTargetAnchor() {
		return this.targetAnchor;
	}

	/**
	 * This getter is called to get the Source Anchor the connection is
	 * momentarily connected with
	 * 
	 * @author Lukas Balzer
	 * 
	 * @return The Anchor at which the connection starts
	 */
	public ConnectionAnchor getSourceAnchor() {
		return this.sourceAnchor;
	}

	@Override
	public UUID getId() {
		return ((IConnection) this.getModel()).getId();
	}

	@Override
	public void translateToRoot(Translatable t) {
		// does nothing by default

	}
	@Override
	public void mouseDragged(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent me) {
//		getConnectionFigure().disableFeedback();
		
	}
	@Override
	public void mouseHover(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseMoved(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ConnectionFigure getConnectionFigure() {
		// TODO Auto-generated method stub
		return (ConnectionFigure) super.getConnectionFigure();
	}

	@Override
	public void eraseFeedback() {
		getConnectionFigure().eraseFeedback();
	}

	@Override
	public void setMember(IConnectable member) {
		this.members.add(member);
	}

	@Override
	public void updateFeedback() {
		getConnectionFigure().updateFeedback();
		
	}

}

class RectangleClipping implements IClippingStrategy{
	private Rectangle clippingRect;
	public RectangleClipping(Rectangle rect) {
		this.clippingRect = rect;
	}
	@Override
	public Rectangle[] getClip(IFigure childFigure) {
		return new Rectangle[]{this.clippingRect};
	}
	
}
