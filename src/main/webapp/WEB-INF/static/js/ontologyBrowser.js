/*
function doSearchTree(){
	var result = searchOntologyTreeNodeWithName(treeDivId, $('#srch-term').val());
	$("#page-message").html("");
	if(result == null){   		    	       	    
    	$("#page-message").html("<div class='alert alert-danger'>"+ seasrchErrorMessage +"</div>");
	}else{
		console.log(result);
	}
} 
*/

function doOntologyTreeHighlight(treeName, nodeKey){
	
	$("#"+treeName).dynatree("getTree").activateKey(nodeKey);
	$('#'+treeName).find("*").removeClass('highlight');
	//then we highlight the nodeKey and its parents
	var elem = nodeKey.split("_");
	var count = 0;
	var key = "";
	var standardVariableKey = "";
	for(count = 0 ; count < elem.length ; count++){
		if(key != '')
			key = key + "_";
		
		key = key + elem[count];
		$('.'+key).addClass('highlight');
	}
	
	var node = $("#"+treeName).dynatree("getTree").getNodeByKey(nodeKey);
	
	if(node != null && node.data.lastChildren == true){
		//call ajax
		standardVariableKey = elem[elem.length-1];		
		processTab(node.data.title, standardVariableKey);
	}else{
		clearAndAppendOntologyDetailsTab('', '');
	}
}

function searchOntologyTreeNodeWithName(treeName, name) {
    if (name == null) {
        return null;
    }

    
    var searchFrom = $('#'+treeName).dynatree("getRoot");
    

    var match = null;

    searchFrom.visit(function (node) {
    	if(node.data.includeInSearch == true){
	        if (node.data.title.toUpperCase().indexOf(name.toUpperCase()) != -1) {
	        	if(match == null){
	        		match = new Array();
	        	}
	            match[match.length] = node;
	            //return false; // Break if found
	        }
	    }
    });
    return match;
    
    	
};

function formatResult(myItem) {
    return myItem.text;
 }

function format(myItem) {
	return "<p><strong>"+myItem.text+"</strong> ("+myItem.type+") <br /> "+myItem.parentTitle+"</p>";
}

function displayOntologyTree(treeName, treeData, searchTreeData, searchDivId){
	//for triggering the start of search type ahead
	if(treeData == null){
		return;
	}
	termSuggestions = $.parseJSON(searchTreeData);	
	termSuggestions_obj = [];
	termSuggestionsMap = {};
		//initialize the arrays that would contain json data for the combos
			$.each(termSuggestions, function( index, value ) {
				var obj = { 		
						
						  'text' : value.value,	
						  'parentTitle' : value.parentTitle,
						  'type' : value.type,
						  'key': value.key,
						  'id': value.key
					};
				termSuggestions_obj.push(obj);  
				termSuggestionsMap[obj.key] = obj;
			});
	
	//getOntologySuffix(id)
	$('#search-term').select2({
        query: function (query) {	
          var data = {results: sortByKey(termSuggestions_obj, "text")}, i, j, s;
          // return the array that matches
          data.results = $.grep(data.results,function(item,index) {
            return ($.fn.select2.defaults.matcher(query.term,item.text));
          
          });
          /*
          if (data.results.length === 0){
        	  data.results.unshift({id:query.term,text:query.term});	        	 
          }
          */
            query.callback(data);
        },
        escapeMarkup: function(m) {
            // Do not escape HTML in the select options text
            return m;
         },
         formatResult: format,
         formatSelection: formatResult

    }).on("change", function(){
    	var data = $('#search-term').select2('data');
    	
    	 doOntologyTreeHighlight(treeDivId, data.key);
    	 
    });
	
	/*
	$('#'+searchDivId).typeahead('destroy');
	//console.log('true');
	var searchTypeAhead = $('#'+searchDivId).typeahead({
   	  local:  $.parseJSON(searchTreeData),
   	  limit : 20,   	
   	  template: '<p><strong>{{value}}</strong> ({{type}}) <br /> {{parentTitle}}</p>',
   	  engine: Hogan,   	
   	});
   	
   	searchTypeAhead.on('typeahead:selected',function(evt,data){   	    
   	    doOntologyTreeHighlight(treeDivId, data.key);
   	    return false;
   	    });
   	searchTypeAhead.on('typeahead:autocomplete',function(evt,data){   	    
   	    doOntologyTreeHighlight(treeDivId, data.key);
   	    return false;
   	    });
   	*/
	
	var json = $.parseJSON(treeData);
	
	$("#" + treeName).dynatree({
	      checkbox: false,
	      // Override class name for checkbox icon:
	      classNames: {
				container: "fbtree-container",
				expander: "fbtree-expander",
				nodeIcon: "fbtree-icon",
				combinedIconPrefix: "fbtree-ico-",
				focused: "fbtree-focused",
				active: "fbtree-active"
			},
	      selectMode: 1,
	      children: json,
	      onActivate: function(node) {
	     // Display list of selected nodes
	        var selNodes = node.tree.getSelectedNodes();
	        // convert to title/key array
	        var selKeys = $.map(selNodes, function(node){
	             return "[" + node.data.key + "]: '" + node.data.title + "'";
	        });
	        //$('.fbtree-focused').addClass("highlight");
	        doOntologyTreeHighlight(treeName, node.data.key);
	      },
	      onSelect: function(select, node) {
	        // Display list of selected nodes
	    	  /*
	        var s = node.tree.getSelectedNodes().join(", ");
	        //alert("onSelect" + s);
	        
	        if(node.data.lastChildren == true){
	        	alert("Trigger Ajax 2");
	        	//$('.'+node.data.key).addClass("highlight");
	        	
	        	
	        }
	        */
	        //$('.fbtree-focused').addClass("highlight");
	        doOntologyTreeHighlight(treeName, node.data.key);
	      },
	      onDblClick: function(node, event) {
	        node.toggleSelect();
	      },
	      onKeydown: function(node, event) {
	        if( event.which == 32 ) {
	          node.toggleSelect();
	          return false;
	        }
	      },
	    });

}

//Tab functions
function processTab(variableName, variableId) {
	viewTabs(variableName, variableId);
	
}

function showSelectedTab(selectedTabName) {
	$("#ontology-tab-headers").show();
	var tabs = $("#ontology-tabs").children();
	for (var i = 0; i < tabs.length; i++) {
		if (tabs[i].id == selectedTabName) {
			$("#" + tabs[i].id + "-li").addClass("active");
			$("#" + tabs[i].id).show();
		} else {
			$("#" + tabs[i].id + "-li").removeClass("active");
			$("#" + tabs[i].id).hide();
		}
	}
}

function clearAndAppendOntologyDetailsTab(variableName, html){
	if(html != ''){
		$("#ontology-detail-tabs").empty().append(html);		
		var varDetails = variableDetailHeader + " " + variableName;
		$("#variable-details").html(varDetails);
	}		
	else{
		// we dont clear if there is an information tab
		if($('#addVariablesSettingBody').length == 0){
			$("#ontology-detail-tabs").empty();
			$("#variable-details").html('');	
			$('#ontology-detail-tabs').empty().append($('.variable-detail-info').html());
		}else{
			//we set the reminder
			$('#ontology-detail-tabs').empty().append($('.variable-detail-info').html());
			
			if($("#heading-modal").text() == addNurseryLevelSettings)
					$('#reminder-placeholder').html(reminderNursery);
			else if($("#heading-modal").text() == addPlotLevelSettings)
					$('#reminder-placeholder').html(reminderPlot);
			else if($("#heading-modal").text() == addBaselineTraits)
					$('#reminder-placeholder').html(reminderTraits);
					
				
		}
		
		
	}
		
}

function viewTabs(variableName, variableId) {
	if(isSearchTab == true)
		return;
	isSearchTab = true;
	Spinner.toggle();
	$.ajax({
		url: ontologyUrl + "details/" + variableId,
		type: "get",
		async: true,
		//dataType: "json",
		success: function(html) {
			clearAndAppendOntologyDetailsTab(variableName, html);	
			
			
			if($('#selectedStdVarId').length != 0){
				
				$('#selectedStdVarId').val(variableId);
				
			}
			if($('#selectedName').length != 0){
				
				$('#selectedName').val(variableName);
				
			}
			isSearchTab = false;
		},
		error: function(jqXHR, textStatus, errorThrown){ 
			console.log("The following error occured: " + textStatus, errorThrown); 
		},
		complete: function() {
			Spinner.toggle();
		}
	});
}

//save function for adding ontology
function doSave(combo) {
	if (validateCombo(combo)) {
		//get form data
		var $form = $("#addVariableForm");
		serializedData = $form.serialize();
		
		Spinner.toggle();
		
		$.ajax({
			url: ontologyUrl + "addVariable/" + combo,
			type: "post",
			dataType: "json",
			data: serializedData,
		    success: function(data){
			    if (data.status == "1") {
			    	//add the newly inserted ontology in its corresponding dropdown
			    	recreateCombo(combo, data);	
			    	showSuccessMessage(data.successMessage);
			    	
			    	if(data.addedNewTrait == "1"){
			    		//we need to recreate the combo for the traitClass			    		
			    		var newData = {id:data.traitId, name:data.traitName, definition:data.traitDefinition};
			    		recreateCombo('TraitClass', newData);
			    		
			    	}
			    	//$("#comboTraitClass").select2('data', dataVal).trigger('change');
		       	} else {
		       		showMessage(data.errorMessage);
		       	}
		   }, 
		   error: function(jqXHR, textStatus, errorThrown){
				console.log("The following error occured: " + textStatus, errorThrown);
		   }, 
		   complete: function(){ 
			   Spinner.toggle();
		   } 
		});
		
		$("#page-message-modal").html("");
	}
}



function setCorrespondingTraitClass(propertyId){
	var dataVal = {id:'',text:'',description:''}; //default value
	if(isInt(propertyId)){
		$.ajax({
			url: ontologyUrl+"retrieve/trait/property/" + propertyId,
			type: "GET",
			dataType: "json",
			data: "",
		    success: function(data){
			    if (data.status == "1") {
			    	//set trait class and crop ontology id of selected property
			    	if(data.traitId != ''){
			    		var count = 0;
				    	for(count = 0 ; count < traitClassesSuggestions_obj.length ; count++){
				    		if(traitClassesSuggestions_obj[count].id == data.traitId){
				    			dataVal = traitClassesSuggestions_obj[count];			    			
				    			break;
				    		}			    			
				    	}
			    	}
			    	$("#cropOntologyId").val(data.cropOntologyId);
			    	$("#comboManagePropTraitClass").select2('data', dataVal).trigger('change');
			    	
		       	}
		   }
		   
		});
	}
}

function getOntologySuffix(id){
	return (id > -1 ? " (Shared)" : ""); 
}

//function to create the select2 combos
function initializeVariable(variableSuggestions, variableSuggestions_obj, description, name, allowTypedValues) {
	
	if (name.indexOf('TraitClass') > -1 && variableSuggestions_obj.length == 1 || variableSuggestions_obj.length == 0) {
		//initialize the arrays that would contain json data for the combos
		if (description == "description") {
			$.each(variableSuggestions, function( index, value ) {
				variableSuggestions_obj.push({ 'id' : value.id,
					  'text' : value.name + getOntologySuffix(value.id),
					  'description' : value.description
				});  
		  		
			});
		} else if (name == "Property"){		
			$.each(variableSuggestions, function( index, value ) {
				variableSuggestions_obj.push({ 'id' : value.id,
					  'text' : value.name + getOntologySuffix(value.id),
					  'description' : value.definition,
					  'traitId' : value.isAId,
					  'cropOntologyId' : value.cropOntologyId
				});  
		  		
			});
		} else {
			$.each(variableSuggestions, function( index, value ) {
				variableSuggestions_obj.push({ 'id' : value.id,
					  'text' : value.name + getOntologySuffix(value.id),
					  'description' : value.definition
				});  
		  		
			});
		}
	}
	//create the select2 combo
	//if combo to create is the variable name, add an onchange event to fill up all the fields of the selected variable
	if (name == "VariableName") {
		$("#combo" + name).select2({
	        query: function (query) {	
	          var data = {results: sortByKey(variableSuggestions_obj, "text")}, i, j, s;
	          // return the array that matches
	          data.results = $.grep(data.results,function(item,index) {
	            return ($.fn.select2.defaults.matcher(query.term,item.text));
	          
	          });
	          if (data.results.length === 0){
	        	  data.results.unshift({id:query.term,text:query.term});	        	 
	          }
	          
	            query.callback(data);
	        }
	
	    }).on("change", function(){
	    	getStandardVariableDetails($("#combo" + name).select2("data").id, $("#combo" + name).select2("data").text);
	    	var tempId = $("#combo" + name).select2("data").id
	    	if(tempId == $("#combo" + name).select2("data").text){
	    		enableFieldsForUpdate();
	    		$("#traitClassBrowserTree").dynatree("enable");
	    	}
	    	
	    });
	} else {
		//if combo to create is one of the ontology combos, add an onchange event to populate the description based on the selected value
		$("#combo" + name).select2({
	        query: function (query) {
	          var data = {results: sortByKey(variableSuggestions_obj, "text")}, i, j, s;
	          // return the array that matches
	          data.results = $.grep(data.results,function(item,index) {
	            return ($.fn.select2.defaults.matcher(query.term,item.text));
	          
	          });
	          if(allowTypedValues == true){
	        	  if (data.results.length === 0) 
	        		  data.results.unshift({id:query.term,text:query.term});  
	          }
	            
	            query.callback(data);
	            
	        }
	
	    }).on("change", function(){
	    	$("#" + lowerCaseFirstLetter(name) + "Description").val($("#combo"+name).select2("data").description);
	    	if(name == 'TraitClass'){
	    		filterPropertyCombo(treeDivId, "comboTraitClass", "traitClassDescription", $("#comboTraitClass").select2("data").id, true);
	    	}
	    	else if (name == 'Property') {
	    		$("#cropOntologyDisplay").html($("#combo"+name).select2("data").cropOntologyId);
	    	}
	    	if (name.match("^Manage")) {
	    		$("#" + "page-message-" + lowerCaseFirstLetter(name) + "-modal").html("");
	    		if ($("#combo"+name).select2("data").description) { 
	    			//edit mode
			    	$("#" + lowerCaseFirstLetter(name) + "Id").val($("#combo"+name).select2("data").id);
			    	$("#" + lowerCaseFirstLetter(name) + "Name").val($("#combo"+name).select2("data").text.replace(" (Shared)", ""));
		    		$("#btnAdd" + name).hide();
		    		$("#btnUpdate" + name).show();
		    		$("#btnDelete" + name).show();
		    		$("#" + lowerCaseFirstLetter(name) + "NameText").html($("#combo"+name).select2("data").text.replace(" (Shared)", ""));
		    		
		    		//add the loading of the linked variables here
		    		if (allowTypedValues) {
		    			retrieveLinkedVariables(name, $("#combo"+name).select2("data").id);
		    		}
		    		
		    		if(name == 'ManageTraitClass'){
		    			//setCorrespondingTraitClass($("#combo"+name).select2("data").id);
		    			var count = 0;
		    			var traitId = $("#combo"+name).select2("data").id;
		    			if(traitId != null && traitId != ''){
		    				var nodeKeyFull = getNodeKeyFromTraitClass(traitId,'manageParentTraitClassBrowserTree')
		    				
		    				var elem = nodeKeyFull.split("_");
		    				var count = 0;
		    				var prevTraitId;
		    				for(count = 0 ; count < elem.length ; count++){		    					
		    					
		    					if(traitId == elem[count])
		    						break;
		    					else
		    						prevTraitId = elem[count];
		    				}
		    				
		    				for(count = 0 ; count < traitClassesSuggestions_obj.length ; count++){
					    		if(traitClassesSuggestions_obj[count].id == prevTraitId){
					    			dataVal = traitClassesSuggestions_obj[count];			    			
					    			break;
					    		}			    			
					    	}
					    	
					    	$("#comboManageParentTraitClass").select2('data', dataVal).trigger('change');

		    			}
		    			
				    	
		    		}
		    		
		    		
		    		if(name == 'ManageProperty'){
		    			setCorrespondingTraitClass($("#combo"+name).select2("data").id);
		    			if (parseInt($("#combo"+name).select2("data").id) > 0) {
		    				disablePropertyFields();
		    			} else {
		    				enablePropertyFields();
		    			}
		    		}
		    		
	    		} else { //add mode
	    			if(name != 'ManageProperty')
	    				clearForm(lowerCaseFirstLetter(name) + "Form");
	    			
			    	$("#" + lowerCaseFirstLetter(name) + "Id").val('');
			    	$("#" + lowerCaseFirstLetter(name) + "Name").val($("#combo"+name).select2("data").id);
		    		$("#btnAdd" + name).show();
		    		$("#btnUpdate" + name).hide();
		    		$("#btnDelete" + name).hide();
		    		$("#" + lowerCaseFirstLetter(name) + "NameText").html($("#combo"+name).select2("data").id);
		    		$("#manageLinkedVariableList").html("");
		    		
	    		}
	    	}	    	
	    });
	}
}

function disablePropertyFields() {
	$("#comboManagePropTraitClass").select2("disable", true);
	$("#managePropertyDescription").attr("disabled", "disabled");
	$("#managePropTraitClassBrowserTree").dynatree("disable");
}

function enablePropertyFields() {
	$("#comboManagePropTraitClass").select2("enable", true);
	$("#managePropertyDescription").removeAttr("disabled");
	$("#managePropTraitClassBrowserTree").dynatree("enable");
}

function retrieveLinkedVariables(ontologyType, ontologyId){
	//console.log(ontologyType + " = " + ontologyId);
	Spinner.toggle();
	$.ajax({
		url: ontologyUrl + "retrieve/linked/variable/" + ontologyType + "/"+ontologyId,
		type: "get",
		success: function(html) {
			$("#manageLinkedVariableList").empty().append(html);			
			Spinner.toggle();
		}
	});
	
}


function lowerCaseFirstLetter(string)
{
    return string.charAt(0).toLowerCase() + string.slice(1);
}

function loadOntologyCombos(){
	//create combos
	initializeVariable(variableNameSuggestions, variableNameSuggestions_obj, "description", "VariableName", true);
	
	traitClassesSuggestions_obj.push({ 'id' : 0,
		  'text' : "-- All --",
		  'description' : "All",
	});  
	
	initializeVariable(traitClassesSuggestions, traitClassesSuggestions_obj, "description", "TraitClass", false);
	initializeVariable(propertySuggestions, propertySuggestions_obj, "definition", "Property", false);
	initializeVariable(methodSuggestions, methodSuggestions_obj, "definition", "Method", false);
	initializeVariable(scaleSuggestions, scaleSuggestions_obj, "definition", "Scale", false);
}		

function loadTraitOntologyCombos(){
	//re create combos	
	traitClassesSuggestions_obj.push({ 'id' : 0,
		  'text' : "-- All --",
		  'description' : "All",
	});  
	//initialize main tree
	initializeVariable(traitClassesSuggestions, traitClassesSuggestions_obj, "description", "TraitClass", false);
	//initialize main tree
	loadTraitClassTree("traitClassBrowserTree", "comboTraitClass", "traitClassDescription", treeClassData, 'comboTraitClass');
	
	
	//initializeVariable(traitClassesSuggestions, traitClassesSuggestions_obj, "definition", "ManageTraitClass", true);
	//initializeVariable(traitClassesSuggestions, traitClassesSuggestions_obj, "definition", "ManageParentTraitClass", false);
	//loadTraitClassTree("manageParentTraitClassBrowserTree", "comboManageParentTraitClass", "manageParentTraitClassDescription", treeClassData, "comboManageParentTraitClass");
	//loadTraitClassTree("managePropTraitClassBrowserTree", "comboManagePropTraitClass", "managePropTraitClassDescription", treeClassData, "comboManagePropTraitClass");
	/*
	$("#comboManageParentTraitClass").on("change", function() {
		filterPropertyCombo("manageParentTraitClassBrowserTree", "comboManageParentTraitClass", "manageParentTraitClassDescription", 
				$("#comboManageParentTraitClass").val(), true);
	});
	*/
	$("#traitClassBrowserTree").dynatree("getTree").reload();
	//$("#manageParentTraitClassBrowserTree").dynatree("getTree").reload();

}

function clearFields() {
	$(".form-control").val("");
	$(".select2").select2("val", "");
	$("#page-message-modal").html("");
}

function recreateCombo(combo, data) {
	var suggestions_obj = [];
	var description = null;
	
	//add the new data in the collection
	if (combo == "TraitClass") {
		traitClassesSuggestions_obj.push({ 'id' : data.id,
			  'text' : data.name + getOntologySuffix(data.id),
			  'description' : data.definition
		});
		suggestions_obj = sortByKey(traitClassesSuggestions_obj, "text");
	} else if (combo == "Property") {
		propertySuggestions_obj.push({ 'id' : data.id,
			  'text' : data.name + getOntologySuffix(data.id),
			  'description' : data.definition
		});
		suggestions_obj = sortByKey(propertySuggestions_obj, "text");
	} else if (combo == "Method") {
		methodSuggestions_obj.push({ 'id' : data.id,
			  'text' : data.name + getOntologySuffix(data.id),
			  'description' : data.definition
		});
		suggestions_obj = sortByKey(methodSuggestions_obj, "text");
	} else {
		scaleSuggestions_obj.push({ 'id' : data.id,
			  'text' : data.name + getOntologySuffix(data.id),
			  'description' : data.definition
		});
		suggestions_obj = sortByKey(scaleSuggestions_obj, "text");
	}
	
	//set description field to empty
	description = $("#"+lowerCaseFirstLetter(combo)+"Description"); 
	description.val("");
	
	//recreate the dropdown
	$("#combo" + combo).select2({
			query: function (query) {
	              var data = {results: suggestions_obj}, i, j, s;
	              // return the array that matches
	              data.results = $.grep(data.results,function(item,index) {
	                return ($.fn.select2.defaults.matcher(query.term,item.text));
	              
	              });
	              
	              if (data.results.length === 0) data.results.unshift({id:query.term,text:query.term});
	              
	                query.callback(data);
	                
	            }		
	});
	var newData = { 'id' : data.id,
			  'text' : data.name + getOntologySuffix(data.id),
			  'description' : data.definition
		}
	description.val(data.definition);
	$("#combo"+combo).select2('data', newData);//no need to trigger change.trigger('change');
}

//check if the selected item is an existing record 
function itemExists(combo) {
	return $("#combo"+combo).select2("data").id != $("#combo"+combo).select2("data").text && $("#combo"+combo).select2("data").description != undefined;
}

function showSuccessMessage(message) {
	$("#page-message").html(
		    "<div class='alert alert-success'>"+ message +"</div>"
	);
	setTimeout("hideSuccessMessage()", 3000);
}

function hideSuccessMessage(){
	$('#page-message .alert-success').fadeOut(1000);
}

//check if any of the required fields is empty
function requiredFieldsEmpty() {
	return $("#comboVariableName").select2("data") == null || $("#dataType").val() == "" || $("#role").val() == "" || 
	$("#comboTraitClass").select2("data") == null || $("#comboProperty").select2("data") == null || 
	$("#comboMethod").select2("data") == null || $("#comboScale").select2("data") == null;
}

//check if the values selected in the combo is a new entry
function comboValuesInvalid() {	
	return ($("#comboTraitClass").select2("data").id == $("#comboTraitClass").select2("data").text && 
    		$("#comboTraitClass").select2("data").description == undefined) || 
    	   ($("#comboProperty").select2("data").id == $("#comboProperty").select2("data").text && 
			$("#comboProperty").select2("data").description == undefined) || 
		   ($("#comboMethod").select2("data").id == $("#comboMethod").select2("data").text && 
			$("#comboMethod").select2("data").description == undefined) || 
		   ($("#comboScale").select2("data").id == $("#comboScale").select2("data").text && 
			$("#comboScale").select2("data").description == undefined);
}

function sortByKey(array, key) {
    return array.sort(function(a, b) {
        var x = a[key].toLowerCase(); var y = b[key].toLowerCase();
        return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    });
}

function doTraitClassTreeHighlight(treeName, comboName, descriptionName, nodeKey){
	$("#"+treeName).dynatree("getTree").activateKey(nodeKey);
	$('#'+treeName).find("*").removeClass('highlight');
	//then we highlight the nodeKey and its parents
	var elem = nodeKey.split("_");
	var count = 0;
	var key = "";
	var traitClassId = "";
	for(count = 0 ; count < elem.length ; count++){
		if(key != '')
			key = key + "_";
		
		key = key + elem[count];
		$('.'+key).addClass('highlight');
	}
	
	var node = $("#"+treeName).dynatree("getTree").getNodeByKey(nodeKey);
	
	traitClassId = elem[elem.length-1];
	
	filterPropertyCombo(treeName, comboName, descriptionName, traitClassId, false);
	
	if(treeName == 'managePropTraitClassBrowserTree'){
    	$('#managePropTraitClassId').val($('#comboManagePropTraitClass').select2('data').id);
    	$('#managePropTraitClassName').val($('#comboManagePropTraitClass').select2('data').text);
    }
}

function getNodeKeyFromTraitClass(traitClassId, treeName){

	var rootNode = $("#"+treeName).dynatree("getRoot");

	//console.log(rootNode.data.key);

	var children=rootNode.getChildren() ;
	var i = 0;
	var nodeKey = '';
	for(i=0;i<children.length;i++){
		//console.log("child key:"+children[i].data.key);
		nodeKey = getTreeChildren(children[i], traitClassId);
		if(nodeKey != ''){
			break;
		}
	}
	return nodeKey;
}
function getTreeChildren(child, traitClassId){
	//console.log("parent child key:"+child.data.key);
	var nodeKey = "";
	if( child.data.key.indexOf(traitClassId) != -1){
		//console.log("FOUND");
		return child.data.key;
	}
	
	var children=child.getChildren();
	if(children != null){
		//console.log("Children Length:"+children.length);
		var i = 0;
		for(i=0;i<children.length;i++){
			//console.log("child key:"+children[i].data.key);	
			if(children[i].data.key.indexOf(traitClassId) != -1){
				//console.log("FOUND");
				return children[i].data.key;
			}
			if(children[i].getChildren() != null)
				nodeKey = getTreeChildren(children[i], traitClassId);
			
			if(nodeKey != ""){
				break;
			}
		}
	}
	return nodeKey;
}

function filterPropertyCombo(treeName, comboName, descriptionName, traitClassId, isFromDropDown){
	//console.log("Load property of trait class id: "+traitClassId);
	if(isFromDropDown){
		$('#'+treeName).find("*").removeClass('highlight');
		//if(traitClassId != 0){
			var nodeKey = getNodeKeyFromTraitClass(traitClassId, treeName);
			//console.log("Activate: "+ nodeKey);
			//console.log(json);
			//we need to highlight the tree
			$("#"+treeName).dynatree("getTree").activateKey(nodeKey);
			
			//then we highlight the nodeKey and its parents		
			
			if(nodeKey != ''){
				var elem = nodeKey.split("_");
				var count = 0;
				var key = "";
				for(count = 0 ; count < elem.length ; count++){
					if(key != '')
						key = key + "_";
					
					key = key + elem[count];
					$('.'+key).addClass('highlight');
				}
			}
		//}
		
		
		
	}else {
		var counter = 0;
		for(counter = 0 ; counter < traitClassesSuggestions_obj.length ; counter++){
			if(traitClassId == traitClassesSuggestions_obj[counter].id){
				var dataVal = traitClassesSuggestions_obj[counter];
				//console.log(dataVal);
				$("#" + comboName).select2('data', dataVal);
				$("#" + descriptionName).val(dataVal.description);
				break;
			}
		}	
	}
	if(treeName != 'managePropTraitClassBrowserTree'){
		//we filter the property combo
		var suggestions_obj = [];
		if(traitClassId == 0){
			suggestions_obj = sortByKey(propertySuggestions_obj, "text");
		}		
		else{
			//we filter by specific
			var count = 0;
			for(count = 0 ; count < propertySuggestions_obj.length ; count++){
				if(traitClassId == propertySuggestions_obj[count].traitId){
					suggestions_obj[suggestions_obj.length] = propertySuggestions_obj[count];
				}
			}
		}
		
		$("#comboProperty").select2({
			query: function (query) {
	              var data = {results: suggestions_obj}, i, j, s;
	              // return the array that matches
	              data.results = $.grep(data.results,function(item,index) {
	                return ($.fn.select2.defaults.matcher(query.term,item.text));
	              
	              });
	              
	              //if (data.results.length === 0) data.results.unshift({id:query.term,text:query.term});
	              
	                query.callback(data);
	                
	            }
		
	});
		$("#propertyDescription").val("");
		$("#cropOntologyDisplay").html("");
	}
	
}
function loadTraitClassTree(treeName, comboName, descriptionName, treeData, dropDownId){
	//for triggering the start of search type ahead
			
	var json = $.parseJSON(treeData);
	
	
	$("#" + treeName).dynatree({
	      checkbox: false,
	      // Override class name for checkbox icon:
	      classNames: {
				container: "fbtree-container",
				expander: "fbtree-expander",
				nodeIcon: "fbtree-icon",
				combinedIconPrefix: "fbtree-ico-",
				focused: "fbtree-focused",
				active: "fbtree-active"
			},
	      selectMode: 1,
	      children: json,
	      onActivate: function(node) {
	     // Display list of selected nodes
	    	  if(!$(this).hasClass('ui-dynatree-disabled')){
	    		  var selNodes = node.tree.getSelectedNodes();
	  	        // convert to title/key array
		  	        var selKeys = $.map(selNodes, function(node){
		  	             return "[" + node.data.key + "]: '" + node.data.title + "'";
		  	        });
		  	       
		  	        doTraitClassTreeHighlight(treeName, comboName, descriptionName, node.data.key);
	    	  }
	        
	      
	        
	    	
	      },
	      onSelect: function(select, node) {
	        // Display list of selected nodes		
	    	  if(!$(this).hasClass('ui-dynatree-disabled')){
	        doTraitClassTreeHighlight(treeName, comboName, descriptionName, node.data.key);
	    	  }
	      },
	      onDblClick: function(node, event) {
	    	  if(!$(this).hasClass('ui-dynatree-disabled')){
	    		  node.toggleSelect();
	    	  }
	      },
	      onKeydown: function(node, event) {
	        if( event.which == 32 ) {
	        	if(!$(this).hasClass('ui-dynatree-disabled')){
	        		node.toggleSelect();
	        	}
	          return false;
	        }
	      },
	    });

}

//function to retrieve the standard variable details of the selected variable
function getStandardVariableDetails(variableId, text) {
	resetCategoricalValues();
	if(isInt(variableId) && variableId != text){
		Spinner.toggle();
		$.ajax({
			url: ontologyUrl + "retrieve/variable/" + variableId,
			type: "GET",
			cache: false,
			dataType: "json",
			data: "",
		    success: function(data){
			    if (data.status == "1") {
			    	populateFields(data, variableId);
		       	}
			    
			    Spinner.toggle();
		   }
		   
		});
	} else {
		//save the variable name in a hidden field for saving new standard variables
		$("#variableId").val("");
		$("#newVariableName").val($("#comboVariableName").select2("data").text);
		setVisibleButtons(true, false, false);
		$("#role").removeAttr("disabled");
		setDeleteOperation(0);
	}
	$("#page-message").html("");
}

function populateFields(data, variableId) {
	//set values of fields
	$("#variableId").val(variableId);
	$("#newVariableName").val(data.name);
	$("#variableDescription").val(data.description);
	$('#role').select2('destroy');
	$("#role").val(data.role).attr("disabled","disabled");
	$('#role').select2();
	$("#cropOntologyDisplay").html(data.cropOntologyDisplay);
	setComboValues(traitClassesSuggestions_obj, data.traitClass, "TraitClass");
	setComboValues(propertySuggestions_obj, data.property, "Property");
	setComboValues(methodSuggestions_obj, data.method, "Method");
	setComboValues(scaleSuggestions_obj, data.scale, "Scale");
	$("#dataType").val(data.dataType).trigger("change");
	$("#dataTypeId").val(data.dataType);
	if (isInt(data.minValue)) {
		$("#minValue").val(parseInt(data.minValue));
	} else {
		$("#minValue").val(data.minValue);
	}
	if (isInt(data.maxValue)) {
		$("#maxValue").val(parseInt(data.maxValue));
	} else {
		$("#maxValue").val(data.maxValue);
	}
	populateCategoricalValues(data.validValues);
	
	setVisibleButtons(false, true, true);
	setDeleteOperation(2);
	
	//disable other fields except valid values if selected variable is from central db
	if (parseInt(variableId) > 0) {
		disableFieldsForCentralUpdate();
		$("#traitClassBrowserTree").dynatree("disable");
	}else{
		enableFieldsForUpdate();
		$("#traitClassBrowserTree").dynatree("enable");
	}
	
}

function populateCategoricalValues(data) {
	if (data != "") {
		var validValues = $.parseJSON(data);
		for (var i = 0; i < validValues.length; i++) {
			addCatVar(validValues[i].name, validValues[i].description, validValues[i].id);
		}
	} 
}

function setComboValues(suggestions_obj, id, name) {
	var dataVal = {id:'',text:'',description:''}; //default value
	if(id != ''){
		var count = 0;
		//find the matching ontology value in the array given
    	for(count = 0 ; count < suggestions_obj.length ; count++){
    		if(suggestions_obj[count].id == id){
    			dataVal = suggestions_obj[count];			    			
    			break;
    		}			    			
    	}
	}
	//set the selected value of the ontology combo
	$("#combo" + name).select2('data', dataVal).trigger('change');
}

function setVisibleButtons(addButton, updateButton, deleteButton){
	setVisibility(addButton, "#addVariable");
	setVisibility(updateButton, "#updateVariable");
	setVisibility(deleteButton, "#deleteVariable");
}

function setVisibility(isVisible, buttonId) {
	if (isVisible) {
		$(buttonId).show();
	} else {
		$(buttonId).hide();
	}
}

function setDeleteOperation(val) {
	$("#isDelete").val(val);
}

function loadOntologyModal(ontologyName){
		Spinner.toggle();
		$.ajax(
		         { url: ontologyUrl + ontologyName,
		           type: "GET",
		           data: "",
		           success: function(html) {
		        	   
		             $("#manageOntologyModal"+" .modal-content").empty().append(html);
		               
		             $('#manageOntologyModal').modal('show');
		             $.fn.modal.Constructor.prototype.enforceFocus = function () {};
		             Spinner.toggle();
		           }
		         }
		       );
	}

function showErrorMessageInModal(messageDivId, message) {
	//console.log(message);
	$("#" + messageDivId).html(
			"<div class='alert alert-danger'>"+ message +"</div>"
	);
}
function showSuccessMessageInModal(message) {
	$("#page-message-modal").html(
		    "<div class='alert alert-success'>"+ message +"</div>"
	);
	setTimeout("hideSuccessMessageInModal()", 3000);
}

function hideSuccessMessageInModal(){
	$('#page-message-modal .alert-success').fadeOut(1000);
}

function validateTraitClass() {
	return ($("#manageTraitClassName").val() && $("#manageParentTraitClassId").val() && $("#manageParentTraitClassId").val() != "0");
}

function validateProperty() {
	return ($("#managePropertyName").val() && $("#managePropTraitClassId").val() && $("#managePropTraitClassId").val() != "0");
}

function validateScale() {
	return ($("#manageScaleName").val());
}

function validateMethod() {
	return ($("#manageMethodName").val());
}

function findIndexOfOntology(suggestions_obj, data) {
	for (var i = 0; i < suggestions_obj.length; i++) {
	    if (suggestions_obj[i].id == data.id) {
	        return i;
	    }
	}
	return -1;
}

function recreate(combo, suggestions_obj) {
	$("#combo" + combo).select2({
		query: function (query) {
              var data = {results: suggestions_obj}, i, j, s;
              // return the array that matches
              data.results = $.grep(data.results,function(item,index) {
                return ($.fn.select2.defaults.matcher(query.term,item.text));
              
              });
              if (data.results.length === 0) data.results.unshift({id:query.term,text:query.term});
              
                query.callback(data);
            }		
	});
}

function recreateComboAfterDelete(combo, data) {
	var description = null;
	var index = 0;

	//add the new data in the collection
	if (combo == "VariableName") {
		index = findIndexOfDeletedVariable(variableNameSuggestions_obj, data);
		variableNameSuggestions_obj.splice(index, 1);
		recreate(combo, variableNameSuggestions_obj);
	} else if (combo == "ManageTraitClass") {		
		index = findIndexOfOntology(traitClassesSuggestions_obj, data);
		traitClassesSuggestions_obj.splice(index, 1);
		recreate(combo, traitClassesSuggestions_obj);
	} else if (combo == "ManageProperty") {
		index = findIndexOfOntology(propertySuggestions_obj, data);
		propertySuggestions_obj.splice(index, 1);
		recreate(combo, propertySuggestions_obj);
	} else if (combo == "ManageMethod") {
		index = findIndexOfOntology(methodSuggestions_obj, data);
		methodSuggestions_obj.splice(index, 1);
		recreate(combo, methodSuggestions_obj);
	} else {
		index = findIndexOfOntology(scaleSuggestions_obj, data);
		scaleSuggestions_obj.splice(index, 1);
		recreate(combo, scaleSuggestions_obj);
	}
	
	//set description field to empty
	if (description == null) {
		description = $("#"+lowerCaseFirstLetter(combo)+"Description"); 
	}
	description.val("");
}

function recreateComboAfterUpdate(combo, data) {
	var suggestions_obj = [];
	var description = null;
	
	if (combo.indexOf("TraitClass") > -1) {
		suggestions_obj = traitClassesSuggestions_obj;
	}
	else if (combo.indexOf("Property") > -1) {
		suggestions_obj = propertySuggestions_obj;
	}
	else if (combo.indexOf("Method") > -1) {
		suggestions_obj = methodSuggestions_obj;
	}
	else {
		suggestions_obj = scaleSuggestions_obj;
	}
	
	var index = findIndexOfOntology(suggestions_obj, data);
	if (index > -1) { //update
		suggestions_obj[index].description = data.definition;
		if (combo.indexOf("Property") > -1) {
			suggestions_obj[index].traitId = data.isAId;
		}
		
	}
	else { //add
		if (combo.indexOf("Property") > -1) {
			suggestions_obj.push({ 'id' : data.id,
				  'text' : data.name + getOntologySuffix(data.id),
				  'description' : data.definition,
				  'traitId' : data.isAId
			});
		}else{
			suggestions_obj.push({ 'id' : data.id,
				  'text' : data.name + getOntologySuffix(data.id),
				  'description' : data.definition
			});	
		}
		
		suggestions_obj = sortByKey(suggestions_obj, "text");
	}
	
	//set description field to empty
	description = $("#"+lowerCaseFirstLetter(combo)+"Description"); 
	description.val("");
	
	//recreate the dropdown
	$("#combo" + combo).select2({
			query: function (query) {
	              var data = {results: suggestions_obj}, i, j, s;
	              // return the array that matches
	              data.results = $.grep(data.results,function(item,index) {
	                return ($.fn.select2.defaults.matcher(query.term,item.text));
	              
	              });
	              
	              if (data.results.length === 0) data.results.unshift({id:query.term,text:query.term});
	              
	                query.callback(data);
	                
	            }		
	});
	var newData = { 'id' : data.id,
			  'text' : data.name + getOntologySuffix(data.id),
			  'description' : data.definition
		}
	description.val(data.definition);
	$("#combo"+combo).select2('data', newData).trigger('change');
}

function findIndexOfDeletedVariable(suggestions_obj, id) {
	for (var i = 0; i < suggestions_obj.length; i++) {
	    if (suggestions_obj[i].id == id) {
	        return i;
	    }
	}
}

function recreateVariableNameCombo(combo, id, name) {
	var suggestions_obj = [];
	
	//add the new data in the collection
	variableNameSuggestions_obj.push({ 'id' : id,
		  'text' : name + getOntologySuffix(id),
		  'description' : name
	});
	suggestions_obj = sortByKey(variableNameSuggestions_obj, "text");
	
	//recreate the dropdown
	$("#combo" + combo).select2({
			query: function (query) {
	              var data = {results: suggestions_obj}, i, j, s;
	              // return the array that matches
	              data.results = $.grep(data.results,function(item,index) {
	                return ($.fn.select2.defaults.matcher(query.term,item.text));
	              
	              });
	              
	              if (data.results.length === 0)
	            	  {
	            	  	//console.log('new');
	            	  	data.results.unshift({id:query.term,text:query.term});
	            	  
	            	  }
	              
	                query.callback(data);
	                
	            }		
	});
	
	var newData = { 'id' : id,
			  'text' : name + getOntologySuffix(id),
			  'description' : name
		};
	$("#combo"+combo).select2('data', newData).trigger('change');
}

function preSelectAfterUpdate(combo, id, name) {
	var newData = { 'id' : id,
			  'text' : name,
			  'description' : name
		};
	$("#combo" + combo).select2('data', newData).trigger('change');	
}

//function for deleting ontology
function deleteOntology(combo) {
	var formData = {id: $("#" + "combo" + (combo) ).select2('data').id, name: $("#" + lowerCaseFirstLetter(combo) + "Name").val()};

	Spinner.toggle();
	$.ajax({
		url: ontologyUrl + "deleteOntology/" + lowerCaseFirstLetter(combo.replace("Manage", "")),
		type: "post",
		dataType: "json",
		data: formData,
	    success: function(data){
		    if (data.status == "1") {
		    	if(combo == 'ManageTraitClass'){
		    		var chosendRecord = $("#comboTraitClass").select2("data");
		    		traitClassesSuggestions = data.traitClassesSuggestionList;
					traitClassesSuggestions_obj = [];
					treeClassData = data.traitClassTreeData;
					
					loadTraitOntologyCombos();
						
					//if there is a selected trait class in the Manage Variable screen
					if (chosendRecord != null) {
						if($("#" + "combo" + (combo) ).select2('data').id == chosendRecord.id){
							filterPropertyCombo(treeDivId, "comboTraitClass", "traitClassDescription", 0, true);
						}else{
							//we set it again
							$("#comboTraitClass").select2('data', chosendRecord);
							filterPropertyCombo(treeDivId, "comboTraitClass", "traitClassDescription", $("#comboTraitClass").select2("data").id, true);
						}
					}
					recreateComboAfterDelete(combo, formData);
		    	}else{
		    		recreateComboAfterDelete(combo, formData);	
		    	}	    	
	    		
	    		showSuccessMessageInModal(data.successMessage);
	    		//remove the list, other values and reset buttons
	    		$("#" + lowerCaseFirstLetter(combo) + "NameText").html("");
	    		$("#" + lowerCaseFirstLetter(combo) + "Name").val("");
	    		$("#manageLinkedVariableList").html("");
	    		$("#btnAdd" + combo).show();
	    		$("#btnUpdate" + combo).hide();
	    		$("#btnDelete" + combo).hide();
	    	} else {
	    		showErrorMessageInModal('page-message-modal', data.errorMessage);
	       	}
	   }, 
	   error: function(jqXHR, textStatus, errorThrown){
			console.log("The following error occured: " + textStatus, errorThrown);
	   }, 
	   complete: function(){ 
		   Spinner.toggle();
	   } 
	});
}

function clearForm(formName) {
	//$("#" + formName).reset();
	
	$("#" + formName).find("input").each(function() {
		
		this.value = "";
	});
	
}

function showValidValues() {
	var dataType = $("#dataType option:selected").text();
	if (dataType.indexOf("Categorical") > -1) {
		showSelectedValidValues(["AddCatVar","DelCatVar"]);
		hideValidValues(["Min", "Max", "None"]);
	} else if (dataType.indexOf("Numeric variable") > -1) {
		showSelectedValidValues(["Min", "Max"]);
		hideValidValues(["AddCatVar","DelCatVar", "None"]);
	} else {
		showSelectedValidValues(["None"]);
		hideValidValues(["AddCatVar","DelCatVar", "Min", "Max"]);
	}
}

function showSelectedValidValues(validValues) {
	for (var i = 0; i < validValues.length; i++) {
		$("#validValue" + validValues[i]).show();
	}
}

function hideValidValues(validValues) {
	for (var i = 0; i < validValues.length; i++) {
		$("#validValue" + validValues[i]).hide();
	}
}

function addCategoricalValidValue(id, label, description) {
	var deleteButton = "";
	var operation = "0";
	
	//add mode
	if (id == null) {
		operation = "1";
	}
	
	//if new valid value, add a delete button
	if (id < 0 || id == null) {
		deleteButton= "<button class='btn btn-primary' type='button' onClick='delCatVar($(this))'>" + 
						"<span class='glyphicon glyphicon-remove'></span>" +
					    "</button>";
		enumerations.push({ 'id' : id,
			  'name' : label, 
			  'description' : description,
			  'operation' : operation
		});
	} else {
		//read-only
		enumerations_central.push({ 'id' : id,
			  'name' : label, 
			  'description' : description,
			  'operation' : operation
		});
	}
	
	var newValidValue = "<tr><td class='validValueLabel'>" + label +  
						"</td><td class='validValueDesc'>" + description + 
						"</td><td class='validValueDel'>" + deleteButton + "</td></tr>";
	$("#catVarList").append(newValidValue);
	
	//clear fields
	$("#newValidValueLabel").val("");
	$("#newValidValueDesc").val("");
	
	//add scrollbar 
	if ($("#catVarList").height() > 200 && !$("#catVarList").parent().hasClass("scrollWrapper")) {
		$("#catVarList").parent().toggleClass("scrollWrapper");
	}
	styleDynamicTree('catVarList');
}

function validateNewValidValue(label, description, id) {
	if (id == null) {
		//validate if new valid value entered is unique and has a value
		if (findIndexOfEnumeration(enumerations, label, "name") > -1 || 
				findIndexOfEnumeration(enumerations_central, label, "name") > -1) {
			return "name";
		} else if (findIndexOfEnumeration(enumerations, description, "description") > -1 || 
				findIndexOfEnumeration(enumerations_central, description, "description") > -1) {
			return "description";
		} else if (label == "" || description == ""){
			return "required";
		}
		else {
			return "";
		}
	} else {
		return "";
	}
}

function delCatVar(button) {
	//get the label of the valid value to be deleted
	var name = button.closest("td").prev().prev().text();
	
	//get the index of the deleted label and remove it from the enumerations object
	var index = findIndexOfEnumeration(enumerations, name, "name");
	if (enumerations[index].id == null || enumerations[index].id == "") {
		enumerations.splice(index, 1);
	} else {
		enumerations[index].operation = "-1";
	}
	
	//remove the row
	button.closest("tr").remove();
	if ($("#catVarList").height() <= 200 && $("#catVarList").parent().hasClass("scrollWrapper")) {
		$("#catVarList").parent().toggleClass("scrollWrapper");
	}
	styleDynamicTree('catVarList');
}

function findIndexOfEnumeration(enumerations_obj, name, col) {
	//check if given value is already existing
	for (var i = 0; i < enumerations_obj.length; i++) {
		if (col == "name") {
		    if (enumerations_obj[i].name == name) {
		        return i;
		    }
		} else {
			if (enumerations_obj[i].description == name) {
		        return i;
		    }
		}
	}
	return -1;
}

function minMaxErrorMessage(bothMinMaxRequired, notANumber, invalidValue) {
	var minValue = $("#minValue").val();
	var maxValue = $("#maxValue").val();
	
	if (minValue != "" || maxValue != "") {
		if (minValue == "" || maxValue == "") {
			return bothMinMaxRequired;
		}
		if (isNaN(minValue) || isNaN(maxValue)) {
			return notANumber;
		}
		if (parseFloat(minValue) > parseFloat(maxValue)) {
			return invalidValue;
		}
	}
	return "";
}


function resetCategoricalValues() {
	enumerations = [];
	enumerations_central = [];
	$("#catVarList").empty();
	//show scrollbar if values exceed height allotted
	if ($("#catVarList").height() <= 200 && $("#catVarList").parent().hasClass("scrollWrapper")) {
		$("#catVarList").parent().toggleClass("scrollWrapper");
	}
	styleDynamicTree('catVarList');
}

function disableFieldsForCentralUpdate() {
	$("#newVariableName").attr("disabled","disabled");
	$("#variableDescription").attr("disabled","disabled");
	$("#role").attr("disabled","disabled");
	$("#comboTraitClass").select2('disable',true);
	$("#comboProperty").select2('disable',true);
	$("#comboMethod").select2('disable',true);
	$("#comboScale").select2('disable',true);
	$("#dataType").attr("disabled","disabled");
}

function enableFieldsForUpdate() {
	$("#newVariableName").removeAttr("disabled");
	$("#variableDescription").removeAttr("disabled");
	$("#role").removeAttr("disabled");
	$("#comboTraitClass").select2('enable',true);
	$("#comboProperty").select2('enable',true);
	$("#comboMethod").select2('enable',true);
	$("#comboScale").select2('enable',true);
	$("#dataType").removeAttr("disabled");
}

