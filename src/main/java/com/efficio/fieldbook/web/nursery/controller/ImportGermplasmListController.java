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

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.efficio.fieldbook.web.nursery.form.ImportGermplasmListForm;
import com.efficio.fieldbook.web.nursery.validation.ImportGermplasmListValidator;
import com.efficio.fieldbook.web.AbstractBaseFieldbookController;

@Controller
@RequestMapping(ImportGermplasmListController.URL)
public class ImportGermplasmListController extends AbstractBaseFieldbookController{

    public static final String URL = "/NurseryManager/importGermplasmList";

    @Override
    public String getContentName() {
        return "NurseryManager/importGermplasmList";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String show(@ModelAttribute("importGermplasmListForm") ImportGermplasmListForm form, Model model) {
    	return super.show(model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String showDetails(@ModelAttribute("importGermplasmListForm") ImportGermplasmListForm form, BindingResult result, Model model) {
    	
    	ImportGermplasmListValidator validator = new ImportGermplasmListValidator();
    	validator.validate(form, result);
    	//result.reject("importGermplasmListForm.file", "test error msg");    	
    	
        if (result.hasErrors()) {
            /**
             * Return the user back to form to show errors
             */
        	form.setHasError("1");
            return show(form,model);
        }else{
        	form.setHasError("0");
        }
        

    	//meaning everything is good, we redirect
        return "redirect:" + AddOrRemoveTraitsController.URL;
    	
    }

}