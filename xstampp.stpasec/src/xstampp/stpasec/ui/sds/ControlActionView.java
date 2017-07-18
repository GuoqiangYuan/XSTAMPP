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

package xstampp.stpasec.ui.sds;

import java.util.Observable;
import java.util.UUID;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;

import messages.Messages;
import xstampp.astpa.model.controlaction.interfaces.IControlAction;
import xstampp.astpa.model.controlstructure.interfaces.IConnection;
import xstampp.astpa.model.controlstructure.interfaces.IRectangleComponent;
import xstampp.astpa.model.hazacc.ATableModel;
import xstampp.astpa.model.interfaces.IControlActionViewDataModel;
import xstampp.astpa.ui.ATableFilter;
import xstampp.astpa.ui.CommonTableView;
import xstampp.model.ObserverValue;
import xstampp.stpapriv.model.controlaction.ControlAction;
import xstampp.ui.common.ProjectManager;

/**
 * @author Jarkko Heidenwag
 * 
 */
public class ControlActionView extends CommonTableView<IControlActionViewDataModel> {

	/**
	 * @author Jarkko Heidenwag
	 * 
	 */
	public static final String ID = "stpasec.steps.step2_1"; //$NON-NLS-1$

	// the control action currently displayed in the text widget
	private ControlAction displayedControlAction;


	/**
	 * @author Jarkko Heidenwag
	 * 
	 */
	public ControlActionView() {
    super(true);
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @author Jarkko Heidenwag
	 * @param parent
	 *            The parent composite
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.setDataModelInterface(ProjectManager.getContainerInstance()
				.getDataModel(this.getProjectID()));

		this.createCommonTableView(parent, Messages.ControlActions);

		this.getFilterTextField().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent ke) {
				ControlActionView.this.getFilter().setSearchText(
						ControlActionView.this.getFilterTextField().getText());
				ControlActionView.this.refreshView();
			}
		});

		this.setFilter(new ATableFilter());
		this.getTableViewer().addFilter(this.getFilter());

		Listener addControlActionListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				if ((event.type == SWT.KeyDown) && (event.keyCode != 'n')) {
					return;
				}
				ControlActionView.this.getFilter().setSearchText(""); //$NON-NLS-1$
				ControlActionView.this.getFilterTextField().setText(""); //$NON-NLS-1$
				ControlActionView.this.refreshView();
				ControlActionView.this.getDataInterface().addControlAction(
						"", ""); //$NON-NLS-1$
				int newID = ControlActionView.this.getDataInterface()
						.getAllControlActions().size() - 1;
				ControlActionView.this.updateTable();
				ControlActionView.this.refreshView();
				ControlActionView.this.getTableViewer().setSelection(
						new StructuredSelection(ControlActionView.this
								.getTableViewer().getElementAt(newID)), true);
				ControlActionView.this
						.getTitleColumn()
						.getViewer()
						.editElement(
								ControlActionView.this.getTableViewer()
										.getElementAt(newID), 1);
			}
		};

		this.getAddNewItemButton().addListener(SWT.Selection,
				addControlActionListener);

		this.getTableViewer().getTable()
				.addListener(SWT.KeyDown, addControlActionListener);

		// Listener for editing a title by pressing return
		Listener returnListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				if ((event.type == SWT.KeyDown)
						&& (event.keyCode == SWT.CR)
						&& (!ControlActionView.this.getTableViewer()
								.getSelection().isEmpty())) {
					int indexFirstSelected = ControlActionView.this
							.getTableViewer().getTable().getSelectionIndices()[0];
					ControlActionView.this
							.getTitleColumn()
							.getViewer()
							.editElement(
									ControlActionView.this.getTableViewer()
											.getElementAt(indexFirstSelected),
									1);
				}
			}
		};

		this.getTableViewer().getTable()
				.addListener(SWT.KeyDown, returnListener);

		// Listener for the Description
		this.getDescriptionWidget().addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
					Text text = (Text) e.widget;
					String description = text.getText();
					ControlActionView.this.getDataInterface()
							.setControlActionDescription(getCurrentSelection(), description);
			}
		});

		final EditingSupport titleEditingSupport = new CAEditingSupport(
				ControlActionView.this.getTableViewer());
		this.getTitleColumn().setEditingSupport(titleEditingSupport);

		// KeyListener for deleting control actions by selecting them and
		// pressing the delete key
		ControlActionView.this.getTableViewer().getControl()
				.addKeyListener(new KeyAdapter() {

					@Override
					public void keyReleased(final KeyEvent e) {
						if ((e.keyCode == SWT.DEL)
								|| ((e.stateMask == SWT.COMMAND) && (e.keyCode == SWT.BS))) {
							IStructuredSelection selection = (IStructuredSelection) ControlActionView.this
									.getTableViewer().getSelection();
							if (selection.isEmpty()) {
								return;
							}
							ControlActionView.this.deleteItems();
						}
					}
				});

		// Adding a right click context menu and the option to delete an entry
		// this way
		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(ControlActionView.this
				.getTableViewer().getControl());
		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (ControlActionView.this.getTableViewer().getSelection()
						.isEmpty()) {
					return;
				}
				if (ControlActionView.this.getTableViewer().getSelection() instanceof IStructuredSelection) {
					Action deleteControlAction = new Action(
							Messages.DeleteControlActions) {

						@Override
						public void run() {
							ControlActionView.this.deleteItems();
						}
					};
					manager.add(deleteControlAction);
				}
			}
		});
		menuMgr.setRemoveAllWhenShown(true);
		ControlActionView.this.getTableViewer().getControl().setMenu(menu);
		

		
		
		// the source column is for the unsafe control actions
		TableViewerColumn sourceColumn = new TableViewerColumn(
						this.getTableViewer(), SWT.CENTER);
		sourceColumn.getColumn().setText(
				"Source");
		getTableColumnLayout().setColumnData(
				sourceColumn.getColumn(),
				new ColumnWeightData(10, 100, true));

		sourceColumn.setLabelProvider(new ColumnLabelProvider() {

					@Override
					public String getText(Object element) {
						if (element instanceof IControlAction) {
							IRectangleComponent comp=ControlActionView.this.getDataInterface().
									getComponent(((IControlAction) element).getComponentLink());
							if(comp == null){
								return null;
							}
							IConnection conn = ControlActionView.this.getDataInterface().getConnection(comp.getRelative());
							if(conn == null){
								return null;
							}
							comp=ControlActionView.this.getDataInterface().getComponent(conn.getSourceAnchor().getOwnerId());
							return comp.getText();
						}
						return null;
					}
				});
		// the target column is for the unsafe control actions
		TableViewerColumn distanceColumn = new TableViewerColumn(
						this.getTableViewer(), SWT.CENTER);
		distanceColumn.getColumn().setText(
				"Destination");//$NON-NLS-1$
		getTableColumnLayout().setColumnData(
				distanceColumn.getColumn(),
				new ColumnWeightData(10, 100, true));

		distanceColumn.setLabelProvider(new ColumnLabelProvider() {

					@Override
					public String getText(Object element) {
						if (element instanceof IControlAction) {
							IRectangleComponent comp=ControlActionView.this.getDataInterface().
									getComponent(((IControlAction) element).getComponentLink());
							if(comp == null){
								return null;
							}
							IConnection conn = ControlActionView.this.getDataInterface().getConnection(comp.getRelative());
							if(conn == null){
								return null;
							}
							comp=ControlActionView.this.getDataInterface().getComponent(conn.getTargetAnchor().getOwnerId());
							return comp.getText();
						}
						return null;
					}
				});
		this.updateTable();
	}

	@Override
	protected void deleteEntry(ATableModel model) {
    resetCurrentSelection();
    this.getDataInterface().removeControlAction(model.getId());
	}
	
	private class CAEditingSupport extends AbstractEditingSupport {

		/**
		 * 
		 * @author Jarkko Heidenwag
		 * 
		 * @param viewer
		 *            the ColumnViewer
		 */
		public CAEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new TextCellEditor(ControlActionView.this.getTableViewer()
					.getTable());
		}

		@Override
		protected Object getValue(Object element) {
			return getValue(((ControlAction) element).getTitle());
		}

		@Override
		protected void setValue(Object element, Object value) {
			getDataInterface().setControlActionTitle(((ControlAction) element).getId(), String.valueOf(value));
		}
	}

	/**
	 * @author Jarkko Heidenwag
	 * 
	 */
	@Override
	public void updateTable() {
		ControlActionView.this.getTableViewer().setInput(
				this.getDataInterface().getAllControlActions());
	}
	
	@Override
	public void update(Observable dataModelController, Object updatedValue) {
		super.update(dataModelController, updatedValue);
		ObserverValue type = (ObserverValue) updatedValue;
		switch (type) {
		case CONTROL_ACTION:
			this.refreshView();
			break;
		default:
			break;
		}
	}
	@Override
	public String getId() {
		return ControlActionView.ID;
	}

	@Override
	public String getTitle() {
		return Messages.ControlActions;
	}


	@Override
	public void dispose() {
		this.getDataInterface().deleteObserver(this);
		super.dispose();
	}

	@Override
	protected void moveEntry(UUID id, boolean moveUp) {
		getDataInterface().moveEntry(false, moveUp, id, ObserverValue.CONTROL_ACTION);
	}
}