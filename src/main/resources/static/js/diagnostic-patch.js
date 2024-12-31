$(document).ready(() => {
    setAddButtonListener();
    setSubmitDiagnosticPatch();
    loadClassNameList();
});

function setAddButtonListener(){
    let plusButton = $('#add-class');
    plusButton.on('click', () => { addClassToList(); } )
}
function addClassToList() {
    let classNameTextInput = $('#add-file');
    let className = classNameTextInput.val();
    if(!className) return;

    let classNameList = $(".class-name").map(function() {
                            return this.innerHTML;
                        }).get();
    if( classNameList.includes(className) ) {
        alert(`Class ${className} already added to list`);
    } else {
        createClassElement( className );
        classNameTextInput.val('');
    }
}
function loadClassNameList() {
    let classNameListElement = $('#add-file');
    let classNameList = classNameListElement.val().slice(1).slice(0,-1).split(',');
     for( let i = 0; i < classNameList.length; i++ ) {
        createClassElement(classNameList[i]);
     }
     classNameListElement.val('');
}
function createClassElement( className ) {
    let classNameListElement = $('#class-name-list');
    let newClassName = $(`<li class="list-group-item"><span class="class-name">${className}</span> </li>`);
    let removeIconElement = $('<a href="javascript:noAction();" class="icon-link"><i class="bi bi-trash"></i></a>');
    removeIconElement.on('click', () => {newClassName.remove();});
    newClassName.append(removeIconElement);

    classNameListElement.append(newClassName);
}

function setSubmitDiagnosticPatch() {
    $( "#diagnostic-patch-form" ).on( "submit", function( event ) {
        let validatedFormData = validateFormData();
        if(validatedFormData.isValid) {
            let classNameList = $(".class-name").map(function() {
                                        return this.innerHTML;
                                    }).get();
            let addFileInput = $('#add-file');
            addFileInput.val( classNameList.join(',') );
            console.log('submitting');
        } else {
            alert("ERRORS:\r\n- "+validatedFormData.errors.join("\r\n- "));
            event.preventDefault();
        }
    });
}

function validateFormData() {
    let returnObj = {isValid: true, errors: []};
    if(!$('#ticket-number').val()){
        returnObj.isValid = false;
        returnObj.errors.push('Missing Ticket Number, please input a ticket number value.')
    }
    if(!$('#tce-version').val()){
        returnObj.isValid = false;
        returnObj.errors.push('Missing TeamConnectEnterprise version, please input a TeamConnectEnterprise version value.')
    }
    if(!$('#branch-name').val()){
        returnObj.isValid = false;
        returnObj.errors.push('Missing branch name, please set a valid branch name.')
    }
    if(!$('#project-dir').val()){
        returnObj.isValid = false;
        returnObj.errors.push('Missing project directory, please input the directory of the TCE project.')
    }
    let classNameList = $(".class-name").map(function() {
                                return this.innerHTML;
                            }).get();
    if(!classNameList.length){
        returnObj.isValid = false;
        returnObj.errors.push('There are no class names listed, please add at least a class name using the Add Class name input.')
    }

    return returnObj;
}

function downloadZipFile() {
    let fileListPathElements = $(".file-list-path");
    let zipFileNameStr = $('#zip-file-name').text();
    let instructions = $('#patch-instructions').text();
    let fileListPath = [];
    for( let i = 0 ; i < fileListPathElements.length ; i++ ){
        fileListPath.push($(fileListPathElements[i]).text());
    }

    let formData = {};
    formData['zipFileName'] = zipFileNameStr;
    formData['patchInstructions'] = instructions;
    formData['classFileList'] = fileListPath;


    $.ajax({
        url: "/diagnostic-patch/download-zip-file",
        type: 'POST',
        data: JSON.stringify(formData),
        contentType: "application/json",
        dataType: 'json',
        processData: false,
        headers: {
            Accept: "application/json"
        },
        success: function(resultData) { alert("Download completed"); }
    });

}

function noAction(){}