package export;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import messages.Messages;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import xstampp.astpa.haz.controlaction.interfaces.IControlAction;
import xstampp.astpa.model.DataModelController;
import xstampp.astpa.model.controlaction.IValueCombie;
import xstampp.astpa.model.controlaction.interfaces.IHAZXControlAction;
import xstampp.model.IDataModel;
import xstampp.model.ILTLProvider;
import xstpa.ui.View;

public class XCSVExportJob extends Job {
	/**
	 * 
	 * @author Lukas Balzer
	 */
	public static final ArrayList<String> STEPS = new ArrayList<String>(){

		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			this.add(Messages.ContextTables);
			this.add(Messages.LTLFormulasTable);
			this.add("Refined Rules Table");
			this.add(Messages.RefinedSafetyConstraintsTable);
			this.add(Messages.RefinedUnsafeControlActions);
		}
		
	};
	public static final int CONTEXT_TABLES = 1 << 0;
	public static final int LTL_FORMULAS = 1 << 1;
	public static final int RULES_TABLE = 1 << 2;
	public static final int REFINED_CONSTRAINTS = 1 << 3;
	public static final int REFINED_UCA = 1 << 4;

	private DataModelController controller;
	private int tableConstant;
	private String filepath;
	private String seperator = ";";

	public XCSVExportJob(String name, String filePath, char seperator2,
			IDataModel model, int tableConstant) {
		super(name);

		Assert.isLegal(model instanceof DataModelController,
						"This Export can only be executed for a data model of type DataModelController");
		this.controller = (DataModelController) model;
		this.tableConstant = tableConstant;
		this.filepath = filePath;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
			controller.prepareForExport();
		
		try {
				
				PrintWriter writer = new PrintWriter(filepath, "UTF-8");
				if((tableConstant & CONTEXT_TABLES) != 0){
					getContextTableString(writer);
				}if((tableConstant & RULES_TABLE) != 0){
					getRulesTableString(writer);
				}if((tableConstant & REFINED_CONSTRAINTS) != 0){
					getRefinedConstraintsString(writer);
				}if((tableConstant & LTL_FORMULAS) != 0){
					getLTLTableString(writer);
				}if((tableConstant & REFINED_UCA) != 0){
					getRUCATableString(writer);
				}
				writer.close();
				controller.prepareForSave();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	private void getContextTableString(PrintWriter writer){
		writer.println(Messages.ContextTables + " of project " + controller.getProjectName());
		writer.println();
		for(IControlAction controlAction: controller.getAllControlActions()){
			//Context Table for context provided
			writer.println("Context Table of control action " + controlAction.getTitle() + "in context provided");
			
			writer.print(Messages.ControlAction + seperator);
			for(UUID variableID : controller.getCAProvidedVariables(controlAction.getId())){
				writer.print(seperator);
			}
			writer.print("Hazardous " + seperator);
			writer.print("when" + seperator);
			writer.print("provided");
			writer.println();
			
			writer.print(controlAction.getTitle() + seperator);
			for(UUID variableID : controller.getCAProvidedVariables(controlAction.getId())){
				writer.print(controller.getComponent(variableID).getText() + seperator);
			}
			writer.print("at any time" + seperator);
			writer.print("too early" + seperator);
			writer.print("too late");
			writer.println();
			for(IValueCombie combie: controller.getIvaluesWhenCAProvided(controlAction.getId())){
				if(combie.getValueList().size() != controller.getCAProvidedVariables(controlAction.getId()).size()){
					continue;
				}
				writer.print(controlAction.getTitle() + seperator);
				for(UUID valueID : combie.getValueList()){
					writer.print(controller.getComponent(valueID).getText() + seperator);
				}
				for(String type: new String[]{IValueCombie.TYPE_ANYTIME,IValueCombie.TYPE_TOO_EARLY,IValueCombie.TYPE_TOO_LATE}){
					if(combie.isCombiHazardous(type)){
						writer.print("yes" + seperator);
					}else{
						writer.print("no" + seperator);
					}
				}
				writer.println();
			}
			writer.println();
			
			writer.println("Context Table of control action " + controlAction.getTitle() + "in context not provided");
			writer.print(controlAction.getTitle() + seperator);
			for(UUID variableID : controller.getCANotProvidedVariables(controlAction.getId())){
				writer.print(controller.getComponent(variableID).getText() + seperator);
			}
			writer.print("Hazardous");
			writer.println();
			
			for(IValueCombie combie: controller.getIValuesWhenCANotProvided(controlAction.getId())){
				if(combie.getValueList().size() != controller.getCANotProvidedVariables(controlAction.getId()).size()){
					continue;
				}
				writer.print(controlAction.getTitle() + seperator);
				for(UUID valueID : combie.getValueList()){
					writer.print(controller.getComponent(valueID).getText() + seperator);
				}
				if(combie.isCombiHazardous(IValueCombie.TYPE_NOT_PROVIDED)){
					writer.print("yes" + seperator);
				}else{
					writer.print("no" + seperator);
				}
				writer.println();
			}
			writer.println();
		}
	}

	private void getLTLTableString(PrintWriter writer){
		writer.println(Messages.LTLFormulasTable +" of project " + controller.getProjectName());
		writer.print("ID" +seperator);
		writer.println("LTL Formulas");
		for(ILTLProvider provider: controller.getLTLPropertys()){
			writer.print("SSR1." +provider.getNumber() +seperator);
			writer.println(provider.getLtlProperty());
		}
		writer.println();
	}
	private void getRulesTableString(PrintWriter writer){
		writer.println(View.REFINED_RULES + " of project " + controller.getProjectName());
		writer.print("ID" +seperator);
		writer.print("Type" +seperator);
		writer.println(View.REFINED_RULES);
		for(ILTLProvider provider: controller.getLTLPropertys()){
			writer.print("RSR1."+provider.getNumber() +seperator);
			writer.print(provider.getType() +seperator);
			writer.println(provider.getRefinedSafetyConstraint());
		}
		writer.println();
	}
	private void getRUCATableString(PrintWriter writer){
		writer.println(Messages.RefinedUnsafeControlActions + " of project " + controller.getProjectName());
		ArrayList<ILTLProvider> list_notProvided;
		ArrayList<ILTLProvider> list_provided;
		ArrayList<ILTLProvider> list_wrongProvided;

		writer.print(Messages.ControlAction);
		writer.print("Hazardous if not provided");
		writer.print("Hazardous if provided");
		writer.print("Hazardous if wrong provided");
		for(IHAZXControlAction action : controller.getAllControlActionsU()){

			list_notProvided = new ArrayList<>();
			list_provided = new ArrayList<>();
			list_wrongProvided = new ArrayList<>();
			for(ILTLProvider provider: action.getAllRefinedRules()){
				if(provider.getType().equals(IValueCombie.TYPE_NOT_PROVIDED)){
					list_notProvided.add(provider);
				}else if(provider.getType().equals(IValueCombie.TYPE_ANYTIME)){
					list_provided.add(provider);
				}else if(provider.getType().equals(IValueCombie.TYPE_TOO_EARLY)){
					list_wrongProvided.add(provider);
				}else if(provider.getType().equals(IValueCombie.TYPE_TOO_LATE)){
					list_wrongProvided.add(provider);
				}
			}
			writer.println();
			writer.println(action.getTitle() + seperator + seperator + seperator + seperator);
			int loopSize = Math.max(list_notProvided.size(), list_provided.size());
			loopSize = Math.max(loopSize, list_wrongProvided.size());
			for(int i=0;i<loopSize;i++){
				
				writer.print(seperator);
				writer.print(getRucaID(list_notProvided, i));
				writer.print(getRucaID(list_provided, i));
				writer.print(getRucaID(list_wrongProvided, i));
				
				writer.println();
				writer.print(seperator);
				//print line of ruca descriptions
				writer.print(getRUCA(list_notProvided, i));
				writer.print(getRUCA(list_provided, i));
				writer.print(getRUCA(list_wrongProvided, i));
				
				writer.println();
				writer.print(seperator);
				//
				writer.print(getLinks(list_notProvided, i));
				writer.print(getLinks(list_provided, i));
				writer.print(getLinks(list_wrongProvided, i));
				writer.println();
				writer.println(seperator + seperator + seperator + seperator);
				
			}
		}
		
		writer.println();
	}
	private String getLinks(List<ILTLProvider> list, int index){
		if(index < list.size() && list.get(index).getLinks() != null){
			return list.get(index).getLinks() + seperator;
		}
		return seperator;
		
	}
	private String getRUCA(List<ILTLProvider> list, int index){
		if(index < list.size()){
			return list.get(index).getRefinedUCA() + seperator;
		}
		return seperator;
		
	}
	private String getRucaID(List<ILTLProvider> list, int index){
		if(index < list.size()){
			return "RUCA1." +list.get(index).getNumber() + seperator;
		}
		return seperator;
		
	}
	private void getRefinedConstraintsString(PrintWriter writer){
		writer.println(Messages.RefinedSafetyConstraintsTable + " of project " + controller.getProjectName());
		writer.print("ID" +seperator);
		writer.println("Refined Safety Constraint");
		for(ILTLProvider provider: controller.getLTLPropertys()){
			writer.print("SC3." +provider.getNumber() +seperator);
			writer.println(provider.getRefinedSafetyConstraint());
		}
		writer.println();
	}

}