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

/**
 * 
 * The POJO containing information for a germplasm list data.
 *
 */
public class AdvancingSource {

    private ImportedGermplasm germplasm;
    private Integer plantsSelected;
    private Integer breedingMethodId;
    private boolean isCheck;
    private String methodType;

    public AdvancingSource(ImportedGermplasm germplasm, Integer plantsSelected, Integer breedingMethodId, boolean isCheck) {
        this.germplasm = germplasm;
        this.plantsSelected = plantsSelected;
        this.breedingMethodId = breedingMethodId;
        this.isCheck = isCheck;
    }
    
    /**
     * @return the germplasm
     */
    public ImportedGermplasm getGermplasm() {
        return germplasm;
    }
    
    /**
     * @param germplasm the germplasm to set
     */
    public void setGermplasm(ImportedGermplasm germplasm) {
        this.germplasm = germplasm;
    }
    
    /**
     * @return the plantsSelected
     */
    public Integer getPlantsSelected() {
        return plantsSelected;
    }
    
    /**
     * @param plantsSelected the plantsSelected to set
     */
    public void setPlantsSelected(Integer plantsSelected) {
        this.plantsSelected = plantsSelected;
    }
    
    /**
     * @return the breedingMethodId
     */
    public Integer getBreedingMethodId() {
        return breedingMethodId;
    }
    
    /**
     * @param breedingMethodId the breedingMethodId to set
     */
    public void setBreedingMethodId(Integer breedingMethodId) {
        this.breedingMethodId = breedingMethodId;
    }

    /**
     * @return the isCheck
     */
    public boolean isCheck() {
        return isCheck;
    }
    
    /**
     * @param isCheck the isCheck to set
     */
    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    /**
     * @return the methodType
     */
    public String getMethodType() {
        return methodType;
    }
    
    /**
     * @param methodType the methodType to set
     */
    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }
    
    
}
