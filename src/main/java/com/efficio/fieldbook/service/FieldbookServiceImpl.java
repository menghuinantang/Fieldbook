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
package com.efficio.fieldbook.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Person;
import org.springframework.beans.factory.annotation.Autowired;

import com.efficio.fieldbook.service.api.FieldbookService;
import com.efficio.fieldbook.service.api.FileService;
import com.efficio.fieldbook.service.api.WorkbenchService;
import com.efficio.fieldbook.web.nursery.bean.AdvancingNursery;
import com.efficio.fieldbook.web.nursery.bean.ImportedGermplasm;
import com.efficio.fieldbook.web.nursery.bean.PossibleValuesCache;
import com.efficio.fieldbook.web.nursery.bean.SettingDetail;
import com.efficio.fieldbook.web.nursery.service.NamingConventionService;
import com.efficio.fieldbook.web.nursery.service.impl.NamingConventionServiceFactory;
import com.efficio.fieldbook.web.util.AppConstants;

/**
 * The Class FieldbookServiceImpl.
 */
public class FieldbookServiceImpl implements FieldbookService{
	
	/** The file service. */
	@Resource
    private FileService fileService;
	
	@Autowired
	private NamingConventionServiceFactory namingConventionServiceFactory;
	
    @Autowired
    private org.generationcp.middleware.service.api.FieldbookService fieldbookMiddlewareService;

    @Autowired
    private WorkbenchService workbenchService;
	
	@Resource
	private PossibleValuesCache possibleValuesCache;
	

	/* (non-Javadoc)
	 * @see com.efficio.fieldbook.service.api.FieldbookService#storeUserWorkbook(java.io.InputStream)
	 */
	@Override
    public String storeUserWorkbook(InputStream in) throws IOException {
        return getFileService().saveTemporaryFile(in);
    }
	
	/**
	 * Gets the file service.
	 *
	 * @return the file service
	 */
	public FileService getFileService() {
        return fileService;
    }
	
	/**
	 * Advance Nursery
	 */
	public List<ImportedGermplasm> advanceNursery(AdvancingNursery advanceInfo, Workbook workbook)
	        throws MiddlewareQueryException {

        String namingConvention = advanceInfo.getNamingConvention();

        NamingConventionService service = namingConventionServiceFactory.getNamingConventionService(namingConvention);

	    return service.advanceNursery(advanceInfo, workbook);
	}
	
	@Override
	public List<StandardVariableReference> filterStandardVariablesForSetting(int mode, Collection<SettingDetail> selectedList)
	throws MiddlewareQueryException {
		
		List<StandardVariableReference> result = new ArrayList<StandardVariableReference>();

		Set<Integer> selectedIds = new HashSet<Integer>();
		if (selectedList != null && !selectedList.isEmpty()) {
			for (SettingDetail settingDetail : selectedList) {
				selectedIds.add(settingDetail.getVariable().getCvTermId());
			}
		}

		List<Integer> storedInIds = getStoredInIdsByMode(mode);
		List<StandardVariableReference> dbList = fieldbookMiddlewareService.filterStandardVariablesByMode(storedInIds);
		
		if (dbList != null && !dbList.isEmpty()) {
			for (StandardVariableReference ref : dbList) {
				if (!selectedIds.contains(ref.getId())) {
					result.add(ref);
				}
			}
		}
		
		Collections.sort(result);

		return result;
	}
	
    private List<Integer> getStoredInIdsByMode(int mode) {
    	List<Integer> list = new ArrayList<Integer>();
        if (mode == AppConstants.SEGMENT_STUDY.getInt()) {
            list.addAll(PhenotypicType.STUDY.getTypeStorages());
            list.addAll(PhenotypicType.DATASET.getTypeStorages());
            list.addAll(PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages());
        } else if (mode == AppConstants.SEGMENT_PLOT.getInt()) {
            list.addAll(PhenotypicType.TRIAL_ENVIRONMENT.getTypeStorages());
            list.addAll(PhenotypicType.TRIAL_DESIGN.getTypeStorages());
            list.addAll(PhenotypicType.GERMPLASM.getTypeStorages());
        } else if (mode == AppConstants.SEGMENT_TRAITS.getInt()) {
            list.addAll(PhenotypicType.VARIATE.getTypeStorages());
        }
        return list;
    }
	
    @Override
    public List<ValueReference> getAllPossibleValues(int id) throws MiddlewareQueryException {
        List<ValueReference> possibleValues = possibleValuesCache.getPossibleValues(id);
        if (possibleValues == null) {

            if (TermId.BREEDING_METHOD_ID.getId() == id) {
                possibleValues = getAllBreedingMethods();
            } else if (TermId.LOCATION_ID.getId() == id) {
                possibleValues = convertLocationsToValueReferences(fieldbookMiddlewareService.getAllLocations());
            } else if (TermId.PI_ID.getId() == id) {
                possibleValues = convertPersonsToValueReferences(fieldbookMiddlewareService.getAllPersons());
            } else if (TermId.NURSERY_TYPE.getId() == id) {
                possibleValues = fieldbookMiddlewareService.getAllNurseryTypes();
            } else {
                possibleValues = fieldbookMiddlewareService.getDistinctStandardVariableValues(id);
            }
            possibleValuesCache.addPossibleValues(id, possibleValues);
        }
        return possibleValues;
	}
	
    @Override
    public List<ValueReference> getAllPossibleValuesFavorite(int id, String projectId) throws MiddlewareQueryException {
        List<ValueReference> possibleValuesFavorite = null;
        if (possibleValuesFavorite == null) {
            if (TermId.BREEDING_METHOD_ID.getId() == id) {
                List<Integer> methodIds = workbenchService.getFavoriteProjectMethods(projectId);
                possibleValuesFavorite = getFavoriteBreedingMethods(methodIds);
            } else if (TermId.LOCATION_ID.getId() == id) {
                List<Long> locationIds = workbenchService.getFavoriteProjectLocationIds(projectId);
                possibleValuesFavorite = convertLocationsToValueReferences(fieldbookMiddlewareService
                        .getFavoriteLocationByProjectId(locationIds));
            }
        }
        return possibleValuesFavorite;
    }
	
    private List<ValueReference> getFavoriteBreedingMethods(List<Integer> projectIdList)
            throws MiddlewareQueryException {
        List<ValueReference> list = new ArrayList<ValueReference>();
        List<Method> methods = fieldbookMiddlewareService.getFavoriteBreedingMethods(projectIdList);
        if (methods != null && !methods.isEmpty()) {
            for (Method method : methods) {
                if (method != null) {
                    list.add(new ValueReference(method.getMid(), method.getMname(), method.getMname()));
                }
            }
        }
        return list;
    }
	
    private List<ValueReference> getAllBreedingMethods() throws MiddlewareQueryException {
        List<ValueReference> list = new ArrayList<ValueReference>();
        List<Method> methods = fieldbookMiddlewareService.getAllBreedingMethods();
        if (methods != null && !methods.isEmpty()) {
            for (Method method : methods) {
                if (method != null) {
                    list.add(new ValueReference(method.getMid(), method.getMname(), method.getMname()));
                }
            }
        }
        return list;
    }
	
    private List<ValueReference> convertLocationsToValueReferences(List<Location> locations) {
        List<ValueReference> list = new ArrayList<ValueReference>();
        if (locations != null && !locations.isEmpty()) {
            for (Location loc : locations) {
                if (loc != null) {
                    list.add(new ValueReference(loc.getLocid(), loc.getLname(), loc.getLname()));
                }
            }
        }
        return list;
    }
	
    @Override
    public List<ValueReference> getAllPossibleValuesByPSMR(String property, String scale, String method,
            PhenotypicType phenotypeType) throws MiddlewareQueryException {
        List<ValueReference> list = new ArrayList<ValueReference>();
        Integer standardVariableId = fieldbookMiddlewareService.getStandardVariableIdByPropertyScaleMethodRole(
                property, scale, method, phenotypeType);
        if (standardVariableId != null)
            list = getAllPossibleValues(standardVariableId.intValue());
        return list;
    }

    private List<ValueReference> convertPersonsToValueReferences(List<Person> persons) {
        List<ValueReference> list = new ArrayList<ValueReference>();
        if (persons != null && !persons.isEmpty()) {
            for (Person person : persons) {
                if (person != null) {
                    list.add(new ValueReference(person.getId(), person.getDisplayName()));
                }
            }
        }
        return list;
    }
}
