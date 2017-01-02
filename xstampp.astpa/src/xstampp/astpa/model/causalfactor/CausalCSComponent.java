/*******************************************************************************
 * Copyright (c) 2013, 2016 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner
 * Institute of Software Technology, Software Engineering Group
 * University of Stuttgart, Germany
 *  
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package xstampp.astpa.model.causalfactor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import xstampp.astpa.model.causalfactor.interfaces.ICausalComponent;
import xstampp.astpa.model.causalfactor.interfaces.ICausalFactor;
import xstampp.astpa.model.controlaction.safetyconstraint.ICorrespondingUnsafeControlAction;
import xstampp.astpa.model.controlstructure.components.Component;
import xstampp.astpa.model.controlstructure.components.ComponentType;
import xstampp.astpa.model.controlstructure.interfaces.IRectangleComponent;
import xstampp.astpa.model.hazacc.HazAccController;
import xstampp.astpa.model.sds.interfaces.ISafetyConstraint;
import xstampp.model.AbstractLtlProvider;

@XmlRootElement(name="causalComponent")
public class CausalCSComponent implements ICausalComponent{
  private String text;
  private List<CausalFactor> factors;
  
  
  @Override
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  @Override
  public UUID getId() {
    // TODO Auto-generated method stub
    return null;
  }
  /**
   * @param factors the factors to set
   */
  public UUID addCausalFactor() {
    CausalFactor factor = new CausalFactor(new String());
    if(this.factors == null){
      this.factors = new ArrayList<>();
    }
    this.factors.add(factor);
    return factor.getId();
  }
  
  public boolean removeCausalFactor(UUID id){
    if(this.factors != null){
      for (int i = 0; i < factors.size(); i++) {
        if(factors.get(i).getId().equals(id)){
          return factors.remove(i) != null;
        }
      }
    }
    return false;
  }
  public CausalFactor getCausalFactor(UUID factorId){
    for (CausalFactor factor : factors) {
      if(factor.getId().equals(factorId)){
        return factor;
      }
    }
    return null;
  }
  
  @Override
  public List<ICausalFactor> getCausalFactors() {
    List<ICausalFactor> factors = new ArrayList<>();
    for (CausalFactor causalFactor : this.factors) {
      factors.add(causalFactor);
    }
    return factors;
  }
  @Override
  public ComponentType getComponentType() {
    // TODO Auto-generated method stub
    return null;
  }

  public void prepareForExport(HazAccController hazAccController,
                               IRectangleComponent child, 
                               List<AbstractLtlProvider> allRefinedRules,
                               List<ICorrespondingUnsafeControlAction> allUnsafeControlActions,
                               List<CausalSafetyConstraint> constraints) {
    this.text = child.getText();
    for (CausalFactor causalFactor : factors) {
      causalFactor.prepareForExport(hazAccController, allRefinedRules, allUnsafeControlActions,constraints);
    }
  }

  public void prepareForSave(Map<UUID, List<UUID>> hazardLinksMap, HazAccController hazAccController, IRectangleComponent child, List<AbstractLtlProvider> allRefinedRules, List<ICorrespondingUnsafeControlAction> allUnsafeControlActions, List<CausalSafetyConstraint> constraints) {
    this.text = null;
    for (CausalFactor causalFactor : factors) {
      causalFactor.prepareForSave(hazardLinksMap,hazAccController, allRefinedRules, allUnsafeControlActions,constraints);
    }
  }
 
}