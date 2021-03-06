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
package com.efficio.fieldbook.web.nursery.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.FieldbookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.efficio.fieldbook.service.api.WorkbenchService;
import com.efficio.fieldbook.web.AbstractBaseFieldbookController;
import com.efficio.fieldbook.web.nursery.bean.AdvancingNursery;
import com.efficio.fieldbook.web.nursery.bean.UserSelection;
import com.efficio.fieldbook.web.nursery.form.AdvancingNurseryForm;
import com.efficio.fieldbook.web.util.AppConstants;

/**
 * The Class AddOrRemoveTraitsController.
 */
@Controller
@RequestMapping(AdvancingController.URL)
public class AdvancingController extends AbstractBaseFieldbookController{

    /** The Constant URL. */
    public static final String URL = "/NurseryManager/advance/nursery";
    
    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(AdvancingController.class);
    
    /** The user selection. */
    @Resource
    private AdvancingNursery advancingNursery;
    
    /** The fieldbook middleware service. */
    @Resource
    private FieldbookService fieldbookMiddlewareService;
    
    /** The workbench data manager. */
    @Resource
    private WorkbenchService workbenchService;
    
    @Resource
    private UserSelection userSelection;
    
    /* (non-Javadoc)
     * @see com.efficio.fieldbook.web.AbstractBaseFieldbookController#getContentName()
     */
    @Override
    public String getContentName() {
        return "NurseryManager/advancingNursery";
    }
    
   
    /**
     * Shows the screen.
     *
     * @param form the form
     * @param model the model
     * @param session the session
     * @param nurseryId the nursery id
     * @return the string
     * @throws MiddlewareQueryException the middleware query exception
     */
    @RequestMapping(value="/{nurseryId}", method = RequestMethod.GET)
    public String show(@ModelAttribute("advancingNurseryform") AdvancingNurseryForm form
            , Model model, HttpSession session, @PathVariable int nurseryId) throws MiddlewareQueryException{
    	session.invalidate();
    	form.setMethodChoice("1");
    	form.setLineChoice("1");
    	form.setLineSelected("1");
    	form.setProjectId(this.getCurrentProjectId());
    	form.setPlotsWithPlantsSelected(fieldbookMiddlewareService.countPlotsWithPlantsSelectedofNursery(nurseryId));
    	//form.setBreedingMethods();
    	Study study = fieldbookMiddlewareService.getStudy(nurseryId);
    	List<Variable> varList = study.getConditions().getVariables();
    	form.setDefaultMethodId(Integer.toString(AppConstants.SINGLE_PLANT_SELECTION_SF.getInt()));
    	for(Variable var : varList){
    		if(var.getVariableType().getStandardVariable().getId() == TermId.BREEDING_METHOD_ID.getId() 
    				&& var.getValue() != null && !var.getValue().equalsIgnoreCase("") && !var.getValue().equalsIgnoreCase("0")){    			
    			form.setDefaultMethodId(var.getValue());
    		}
    	}
    	advancingNursery.setStudy(study);
    	form.setLocationUrl(AppConstants.LOCATION_URL.getString());
    	form.setBreedingMethodUrl(AppConstants.BREEDING_METHOD_URL.getString());
    	//long start = System.currentTimeMillis();
    	Workbook workbook = null;//fieldbookMiddlewareService.getNurseryDataSet(nurseryId);
    	//System.out.println("get nursery : " + (System.currentTimeMillis() - start));
    	userSelection.setWorkbook(workbook);
    	form.setNurseryId(Integer.toString(nurseryId));
    	return super.show(model);
    }
    @ResponseBody
    @RequestMapping(value="/load/{nurseryId}", method = RequestMethod.GET)
    public Map<String, String> showLoadNursery(@ModelAttribute("advancingNurseryform") AdvancingNurseryForm form
            , Model model, HttpSession session, @PathVariable int nurseryId) throws MiddlewareQueryException{
    	long start = System.currentTimeMillis();
    	Map<String, String> result = new HashMap<String, String>();
    	Workbook workbook = fieldbookMiddlewareService.getNurseryDataSet(nurseryId);    	
    	userSelection.setWorkbook(workbook);
    	//System.out.println("loading: " + (System.currentTimeMillis()-start));
    	return result;
    }
    
    /**
     * Gets the breeding methods.
     *
     * @return the breeding methods
     */
    @ResponseBody
    @RequestMapping(value="/getBreedingMethods", method = RequestMethod.GET)
    public Map<String, String> getBreedingMethods() {
        Map<String, String> result = new HashMap<String, String>();
        
        try {
            List<Method> breedingMethods = fieldbookMiddlewareService.getAllBreedingMethods();
            Project project = new Project();
            project.setProjectId(Long.valueOf(this.getCurrentProjectId()));
            
              List<Integer> methodIds = workbenchService
                          .getFavoriteProjectMethods(getCurrentProjectId());
              List<Method> favoriteMethods = fieldbookMiddlewareService.getFavoriteBreedingMethods(methodIds);
            result.put("success", "1");
            result.put("allMethods", convertMethodsToJson(breedingMethods));
            result.put("favoriteMethods", convertMethodsToJson(favoriteMethods));
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(), e);
            result.put("success", "-1");
            result.put("errorMessage", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Gets the locations.
     *
     * @return the locations
     */
    @ResponseBody
    @RequestMapping(value="/getLocations", method = RequestMethod.GET)
    public Map<String, String> getLocations() {
        Map<String, String> result = new HashMap<String, String>();
        
        try {
            List<Long> locationsIds = workbenchService
                                .getFavoriteProjectLocationIds(getCurrentProjectId());
            List<Location> faveLocations = fieldbookMiddlewareService
                                .getFavoriteLocationByProjectId(locationsIds);
            List<Location> allLocations = fieldbookMiddlewareService.getAllLocations();
            result.put("success", "1");
            result.put("favoriteLocations", convertFaveLocationToJson(faveLocations));
            result.put("allLocations", convertFaveLocationToJson(allLocations));
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(), e);
            result.put("success", "-1");
        }
        
        return result;
    }
    
    /**
     * Convert favorite location to json.
     *
     * @param locations the locations
     * @return the string
     */
    private String convertFaveLocationToJson(List<Location> locations) {
        if (locations!= null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(locations);
            } catch(Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return "";
    }
    
    /**
     * Convert methods to json.
     *
     * @param breedingMethods the breeding methods
     * @return the string
     */
    private String convertMethodsToJson(List<Method> breedingMethods) {
        if (breedingMethods!= null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(breedingMethods);
            } catch(Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return "";
    }
    
    /**
     * Post advance nursery.
     *
     * @param form the form
     * @param result the result
     * @param model the model
     * @return the string
     * @throws MiddlewareQueryException the middleware query exception
     */
    @RequestMapping(method = RequestMethod.POST)
    public String postAdvanceNursery(@ModelAttribute("advancingNurseryform") AdvancingNurseryForm form
            , BindingResult result, Model model) throws MiddlewareQueryException{
        
        advancingNursery.setNamingConvention(form.getNamingConvention());
        advancingNursery.setSuffixConvention(form.getSuffixConvention());
        advancingNursery.setMethodChoice(form.getMethodChoice());
        advancingNursery.setBreedingMethodId(form.getBreedingMethodId());
        advancingNursery.setLineChoice(form.getLineChoice());
        advancingNursery.setLineSelected(form.getLineSelected());
        advancingNursery.setHarvestDate(form.getHarvestDate());
        advancingNursery.setHarvestLocationId(form.getHarvestLocationId());
        advancingNursery.setHarvestLocationAbbreviation(form.getHarvestLocationAbbreviation());

        return "redirect:" + SaveAdvanceNurseryController.URL;
    }
    
}