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
import java.util.List;

import javax.annotation.Resource;

import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.efficio.fieldbook.web.nursery.bean.ImportedGermplasm;
import com.efficio.fieldbook.web.nursery.bean.ImportedGermplasmList;
import com.efficio.fieldbook.web.nursery.bean.ImportedGermplasmMainInfo;
import com.efficio.fieldbook.web.nursery.bean.UserSelection;
import com.efficio.fieldbook.web.nursery.form.ImportGermplasmListForm;
import com.efficio.fieldbook.web.nursery.service.ImportGermplasmFileService;
import com.efficio.fieldbook.web.nursery.validation.ImportGermplasmListValidator;
import com.efficio.fieldbook.web.AbstractBaseFieldbookController;

/**
 * This controller handles the 2nd step in the nursery manager process.
 * 
 * @author Daniel Jao
 */
@Controller
@RequestMapping(ImportGermplasmListController.URL)
public class ImportGermplasmListController extends AbstractBaseFieldbookController{
    
    private static final Logger LOG = LoggerFactory.getLogger(ImportGermplasmListController.class);
    
    /** The Constant URL. */
    public static final String URL = "/NurseryManager/importGermplasmList";
    public static final String PAGINATION_TEMPLATE = "/NurseryManager/showGermplasmPagination";
    
    /** The user selection. */
    @Resource
    private UserSelection userSelection;
    
    @Resource
    private GermplasmListManager germplasmListManager;

    /** The import germplasm file service. */
    @Resource
    private ImportGermplasmFileService importGermplasmFileService;
    
    /* (non-Javadoc)
     * @see com.efficio.fieldbook.web.AbstractBaseFieldbookController#getContentName()
     */
    @Override
    public String getContentName() {
        return "NurseryManager/importGermplasmList";
    }
    
    /* (non-Javadoc)
     * @see com.efficio.fieldbook.web.AbstractBaseFieldbookController#getUserSelection()
     */
    public UserSelection getUserSelection() {
        return this.userSelection;
    }
    
    /**
     * Show the main import page
     *
     * @param form the form
     * @param model the model
     * @return the string
     */
    @RequestMapping(method = RequestMethod.GET)
    public String show(@ModelAttribute("importGermplasmListForm") ImportGermplasmListForm form
            , Model model) {
        //this set the necessary info from the session variable
        form.setImportedGermplasmMainInfo(getUserSelection().getImportedGermplasmMainInfo());
        if(getUserSelection().getImportedGermplasmMainInfo() != null 
                && getUserSelection().getImportedGermplasmMainInfo().getImportedGermplasmList() != null){
            //this would be use to display the imported germplasm info
            form.setImportedGermplasm(getUserSelection().getImportedGermplasmMainInfo()
                    .getImportedGermplasmList().getImportedGermplasms());
            form.setCurrentPage(1);
        }
    	return super.show(model);
    }
    
    /**
     * Get for the pagination of the list
     *
     * @param form the form
     * @param model the model
     * @return the string
     */
    @RequestMapping(value="/page/{pageNum}", method = RequestMethod.GET)
    public String getPaginatedList(@PathVariable int pageNum
            , @ModelAttribute("importGermplasmListForm") ImportGermplasmListForm form, Model model) {
        //this set the necessary info from the session variable
        form.setImportedGermplasmMainInfo(getUserSelection().getImportedGermplasmMainInfo());
        if(getUserSelection().getImportedGermplasmMainInfo() != null 
                && getUserSelection().getImportedGermplasmMainInfo().getImportedGermplasmList() != null){
            //this would be use to display the imported germplasm info
            form.setImportedGermplasm(getUserSelection().getImportedGermplasmMainInfo()
                    .getImportedGermplasmList().getImportedGermplasms());
            form.setCurrentPage(pageNum);
        }
        return super.showAjaxPage(model, PAGINATION_TEMPLATE);
    }

    /**
     * Process the imported file and just show the information again
     *
     * @param form the form
     * @param result the result
     * @param model the model
     * @return the string
     */
    @RequestMapping(method = RequestMethod.POST)
    public String showDetails(@ModelAttribute("importGermplasmListForm") ImportGermplasmListForm form
            , BindingResult result, Model model) {
    	
    	ImportGermplasmListValidator validator = new ImportGermplasmListValidator();
    	validator.validate(form, result);
    	//result.reject("importGermplasmListForm.file", "test error msg");    	
    	getUserSelection().setImportValid(false);
        if (result.hasErrors()) {
            /**
             * Return the user back to form to show errors
             */
        	form.setHasError("1");
            return show(form,model);
        }else{
        	try{
        		ImportedGermplasmMainInfo mainInfo =importGermplasmFileService
        		        .storeImportGermplasmWorkbook(form.getFile());
        		mainInfo = importGermplasmFileService.processWorkbook(mainInfo);
        		
        		if(mainInfo.getFileIsValid()){
        			form.setHasError("0");
        			getUserSelection().setImportedGermplasmMainInfo(mainInfo);
        			getUserSelection().setImportValid(true);
        			form.setImportedGermplasmMainInfo(getUserSelection().getImportedGermplasmMainInfo());
        			form.setImportedGermplasm(getUserSelection().getImportedGermplasmMainInfo()
        			        .getImportedGermplasmList().getImportedGermplasms());
        			form.setCurrentPage(1);
        			//after this one, it goes back to the same screen, 
        			// but the list should already be displayed
        		}else{
        			//meaning there is error
        			form.setHasError("1");
        			for(String errorMsg : mainInfo.getErrorMessages()){
        				result.rejectValue("file", errorMsg);  
        			}
        			
        		}
        	}catch(Exception e){
                LOG.error(e.getMessage(), e);
        	}
        	
        	
        }
        return show(form,model);
    	
    }
    
    /**
     * Goes to the Next screen.  Added validation if a germplasm list was properly uploaded
     *
     * @param form the form
     * @param result the result
     * @param model the model
     * @return the string
     * @throws MiddlewareQueryException 
     */
    @RequestMapping(value="/next", method = RequestMethod.POST)
    public String nextScreen(@ModelAttribute("importGermplasmListForm") ImportGermplasmListForm form
            , BindingResult result, Model model) throws MiddlewareQueryException {
    	
    	if(getUserSelection().isImportValid()){
    		

    		//this would validate and add CHECK factor if necessary
    		importGermplasmFileService.validataAndAddCheckFactor(form.getImportedGermplasm(), getUserSelection().getImportedGermplasmMainInfo().getImportedGermplasmList().getImportedGermplasms(), userSelection);
    		
    		
    		//getUserSelection().setImportedGermplasmMainInfo(form)
    		
    	    return "redirect:" + AddOrRemoveTraitsController.URL;
    	}
    	else{
    	    form.setHasError("1");
    	    result.reject("error.no.import.germplasm.list", "Please import germplasm");
    	    return show(form,model);
    	}
    	
    }

    @RequestMapping(value="/displayGermplasmDetails/{listId}", method = RequestMethod.GET)
    public String displayGermplasmDetails(@PathVariable Integer listId, @ModelAttribute("importGermplasmListForm") ImportGermplasmListForm form, 
            Model model) {
        
        try {
            ImportedGermplasmMainInfo mainInfo = new ImportedGermplasmMainInfo();
            mainInfo.setAdvanceImportType(true);
            form.setImportedGermplasmMainInfo(mainInfo);
            int count = (int) germplasmListManager.countGermplasmListDataByListId(listId);
            List<GermplasmListData> data = germplasmListManager.getGermplasmListDataByListId(listId, 0, count);
            List<ImportedGermplasm> list = transformGermplasmListDataToImportedGermplasm(data);
            form.setImportedGermplasm(list);
            
            ImportedGermplasmList importedGermplasmList = new ImportedGermplasmList();
            importedGermplasmList.setImportedGermplasms(list);
            mainInfo.setImportedGermplasmList(importedGermplasmList);
            
            form.setCurrentPage(1);
            getUserSelection().setImportedGermplasmMainInfo(mainInfo);
            getUserSelection().setImportValid(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.showAjaxPage(model, PAGINATION_TEMPLATE);
    }
    
    
    private List<ImportedGermplasm> transformGermplasmListDataToImportedGermplasm(List<GermplasmListData> data) {
        List<ImportedGermplasm> list = new ArrayList<ImportedGermplasm>();
        if (data != null && data.size() > 0) {
            for (GermplasmListData aData : data) {
                ImportedGermplasm germplasm = new ImportedGermplasm();
                germplasm.setCheck(null);
                germplasm.setCross(null);
                germplasm.setDesig(aData.getDesignation());
                germplasm.setEntryCode(aData.getEntryCode());
                germplasm.setEntryId(aData.getEntryId());
                germplasm.setGid(aData.getGid().toString());
                germplasm.setSource(aData.getSeedSource());
                
                list.add(germplasm);
            }
        }
        return list;
    }
}
