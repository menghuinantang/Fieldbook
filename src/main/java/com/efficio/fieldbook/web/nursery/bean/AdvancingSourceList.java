/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package com.efficio.fieldbook.web.nursery.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;

import com.efficio.fieldbook.web.util.AppConstants;

/**
 * 
 * The POJO for the Germplasm List when Advancing a Nursery.
 *
 */
public class AdvancingSourceList{

    private List<AdvancingSource> rows;
    private Integer nurseryBreedingMethodId;
    private Integer selectedMethodId;
    private String suffix;
    private String locationAbbreviation;
    String nurseryName;
    
    public AdvancingSourceList(Workbook workbook, AdvancingNursery advanceInfo, Study nursery) {
        if (advanceInfo != null) {
            this.suffix = advanceInfo.getSuffixConvention();
            this.selectedMethodId = getIntegerValue(advanceInfo.getBreedingMethodId());
            this.locationAbbreviation = advanceInfo.getHarvestLocationAbbreviation();
        }

        if (workbook != null) {
            Integer breedingMethodId = null;
            if (workbook.getStudyConditions() != null && !workbook.getStudyConditions().isEmpty()) {
                for (MeasurementVariable studyCondition : workbook.getStudyConditions()) {
                    if (studyCondition.getTermId() == TermId.BREEDING_METHOD_ID.getId() && NumberUtils.isNumber(studyCondition.getValue())) {
                        breedingMethodId = Integer.valueOf(studyCondition.getValue());
                        break;
                    }
                }
            }
            this.nurseryBreedingMethodId = breedingMethodId;
            
            if (workbook.getObservations() != null && !workbook.getObservations().isEmpty()) {
                this.rows = new ArrayList<AdvancingSource>();
                for (MeasurementRow row : workbook.getObservations()) {
                    ImportedGermplasm germplasm = new ImportedGermplasm();
                    germplasm.setCross(row.getMeasurementDataValue(
                            getHeaderLabel(workbook.getMeasurementDatasetVariablesMap(), TermId.CROSS.getId())));
                    germplasm.setDesig(row.getMeasurementDataValue(
                            getHeaderLabel(workbook.getMeasurementDatasetVariablesMap(), TermId.DESIG.getId())));
                    germplasm.setEntryCode(row.getMeasurementDataValue(
                            getHeaderLabel(workbook.getMeasurementDatasetVariablesMap(), TermId.ENTRY_CODE.getId())));
                    germplasm.setEntryId(getIntegerValue(row.getMeasurementDataValue(
                            getHeaderLabel(workbook.getMeasurementDatasetVariablesMap(), TermId.ENTRY_NO.getId()))));
                    germplasm.setGid(row.getMeasurementDataValue(
                            getHeaderLabel(workbook.getMeasurementDatasetVariablesMap(), TermId.GID.getId())));
                    germplasm.setSource(row.getMeasurementDataValue(
                            getHeaderLabel(workbook.getMeasurementDatasetVariablesMap(), TermId.SOURCE.getId())));
                    String check = row.getMeasurementDataValue(
                            getHeaderLabel(workbook.getMeasurementDatasetVariablesMap(), TermId.CHECK.getId()));
                    boolean isCheck = check != null && !"".equals(check);
                    Integer plantsSelected = getIntegerValue(row.getMeasurementDataValue(
                            getHeaderLabel(workbook.getMeasurementDatasetVariablesMap(), TermId.PLANTS_SELECTED.getId())));
                    Integer methodId = getIntegerValue(row.getMeasurementDataValue(
                                getHeaderLabel(workbook.getMeasurementDatasetVariablesMap(), TermId.BREEDING_METHOD_ID.getId())));
                    this.rows.add(new AdvancingSource(germplasm, plantsSelected, methodId, isCheck));
                }
            }
        }
        if (nursery != null) {
            if (nursery.getConditions() != null && nursery.getConditions().size() > 0) {
                Variable breedingMethod = nursery.getConditions().findById(TermId.BREEDING_METHOD_ID.getId());
                if (breedingMethod != null && breedingMethod.getValue() != null && NumberUtils.isNumber(breedingMethod.getValue())) {
                    this.nurseryBreedingMethodId = Integer.valueOf(breedingMethod.getValue());
                }
            }
            this.nurseryName = nursery.getName();
        }
    }
    
    private Integer getIntegerValue(String value) {
        Integer integerValue = null;
        
        if (NumberUtils.isNumber(value)) {
            integerValue = Double.valueOf(value).intValue();
        }
        
        return integerValue;
    }
    
    private String getHeaderLabel(Map<String, MeasurementVariable> variablesMap, int id) {
        String label = "";
        /*
        if (variables != null && !variables.isEmpty()) {
            for (MeasurementVariable variable : variables) {
                if (variable.getTermId() == id) {
                    label = variable.getName();
                }
            }
        }
        */
        String termId = Integer.toString(id);
        if(variablesMap != null){
        	MeasurementVariable variable = variablesMap.get(termId);
        	if(variable != null){
        		label = variable.getName();
        	}
        }
        return label;
    }
    
    public boolean isBulk() {
        return this.selectedMethodId != null && 
                (this.selectedMethodId.intValue() == AppConstants.SELECTED_BULK_SF.getInt()
                    || this.selectedMethodId.intValue() == AppConstants.RANDOM_BULK_SF.getInt()
                    || this.selectedMethodId.intValue() == AppConstants.RANDOM_BULK_CF.getInt());
    }
    
    /**
     * @return the rows
     */
    public List<AdvancingSource> getRows() {
        return rows;
    }
    
    /**
     * @param rows the rows to set
     */
    public void setRows(List<AdvancingSource> rows) {
        this.rows = rows;
    }
    
    /**
     * @return the breedingMethodId
     */
    public Integer getNurseryBreedingMethodId() {
        return nurseryBreedingMethodId;
    }
    
    /**
     * @param breedingMethodId the breedingMethodId to set
     */
    public void setNurseryBreedingMethodId(Integer breedingMethodId) {
        this.nurseryBreedingMethodId = breedingMethodId;
    }
    
    /**
     * @return the selectedMethodId
     */
    public Integer getSelectedMethodId() {
        return selectedMethodId;
    }

    /**
     * @param selectedMethodId the selectedMethodId to set
     */
    public void setSelectedMethodId(Integer selectedMethodId) {
        this.selectedMethodId = selectedMethodId;
    }
    
    /**
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }
    
    /**
     * @param suffix the suffix to set
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
    /**
     * @return the locationAbbreviation
     */
    public String getLocationAbbreviation() {
        return locationAbbreviation;
    }
    
    /**
     * @param locationAbbreviation the locationAbbreviation to set
     */
    public void setLocationAbbreviation(String locationAbbreviation) {
        this.locationAbbreviation = locationAbbreviation;
    }
    
    /**
     * @return the nurseryName
     */
    public String getNurseryName() {
        return nurseryName;
    }
    
    /**
     * @param nurseryName the nurseryName to set
     */
    public void setNurseryName(String nurseryName) {
        this.nurseryName = nurseryName;
    }
    
}

