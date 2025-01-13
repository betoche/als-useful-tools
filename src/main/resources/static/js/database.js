const DatabaseTable = {
    dataTable: null,
    setDataTable( table ) {
        this.dataTable = table;
    },
    getDataTable() {
        return this.dataTable;
    },
    destroyJQueryTable() {
        if( this.getDataTable() != null ) {
            this.dataTable.destroy();
            //this.dataTable = null;
        }
    },
    initJQueryTable( tableElem ) {
        this.destroyJQueryTable();
        this.setDataTable( $(tableElem).DataTable({responsive: true}) );
    }
};

$(document).ready(function() {
    addSnapshotGroupListener();
    addDbSnapshotListener()
});

function addSnapshotGroupListener() {
    $('.snapshot-groups').on('click', "[data-toggle=collapse]", function() {
        showHideDBSnapshots(this);
    });
}
function showHideDBSnapshots( elem ){
    $(elem).toggleClass('in');
    if( $(elem).parent().find(".list-group-flush.collapse").is(':visible') ) {
        $(elem).parent().find(".list-group-flush.collapse").hide();
    } else {
        $(elem).parent().find(".list-group-flush.collapse").show();
    }

    return false;
}
function addDbSnapshotListener() {
    $('.db-snapshot').on('click', function() {
        loadDbSnapshotDetails(this);
    });
}

function loadDbSnapshotDetails(snapshotElement) {
    $('.db-snapshot').each(function(idx, item) {
        $(item).parent().removeClass('bg-primary');
        $(item).parent().addClass('bg-secondary');
        $(item).parent().removeClass('active');
    });
    $(snapshotElement).parent().addClass('bg-primary');
    $(snapshotElement).parent().removeClass('bg-secondary');
    $(snapshotElement).parent().addClass('active');

    let path = $(snapshotElement).data('path');
    $.ajax({
        url: '/snapshots/load-snapshot-details',
        type: 'GET',
        data: 'filePath=' + encodeURIComponent(path),
        dataType: "html",
        success: function(resultData) {
            let jsonData = JSON.parse(resultData);
            let snapshotJson = jsonData.snapshot;
            let databaseJson = snapshotJson.database;

            let username = databaseJson.username;
            let password = databaseJson.password;
            let host = databaseJson.host;
            let port = databaseJson.port;

            let databaseName = databaseJson.name;
            let fileName = snapshotJson.snapshotFileName;
            let creationDate = snapshotJson.creationDate;

            let tablesCount = databaseJson.tables.length;
            let recordsCount = databaseJson.recordsCount;
            let tables = databaseJson.tables;

            $('#name').val(databaseName);
            $('#host').val(host);
            $('#port').val(port);
            $('#username').val(username);
            $('#password').val(password);

            $('#snap-db-name').text(databaseName);
            $('#snap-db-file-name').text(fileName);
            $('#snap-db-creation-date').text(creationDate);
            $('#snap-db-tables-count').text(tablesCount);
            $('#snap-db-records-count').text(recordsCount);

            loadDbSnapshotTablesDetails(tables);
        }
    });
}

function loadDbSnapshotTablesDetails(tableArray) {
    let tableListElem = $('#database-table-list');
    let dbTable = Object.create(DatabaseTable);
    tableArray.forEach(function(tableJson){
        let table = tableJson.table;

        let name = table.name;
        let lastKey = table.lastPrimaryKey;
        let records = table.numberOfRecords;

        let nameElem = $(`<div>${name}</div>`);
        let badgeElem = $(`<div>[ Last Key: ${lastKey}, Records: ${records} ]</div>`);
        let listElem = $(`<div class="list-group-item database-table"></div>`);

        listElem.append(nameElem);
        listElem.append(badgeElem);

        listElem.on('click', function(){showTableDetails(dbTable, table, listElem);});

        tableListElem.append(listElem);
    });
}

function showTableDetails(dbTable, table, listElem) {
    dbTable.destroyJQueryTable();
    let divElem = $('#database-table-details');
    let tableElement = $(`<table class="table caption-top"></table>`);
    let tableCaptionElem = $(`<caption id="database-table-caption"></caption>`);
    let tableHeaderlem = $(`<thead id="database-table-header" class="table-dark" style="font-size: small;">`);
    let tableBodyElem = $(`<tbody id="database-table-body" style="font-size: x-small;">`);

    tableElement.html('');
    tableCaptionElem.html('');
    tableHeaderlem.html('');
    tableBodyElem.html('');

    tableCaptionElem.text(`${table.name}: [total records: ${table.numberOfRecords}, last primary key: ${table.lastPrimaryKey}]`);
    $('.database-table').each(function(idx, item) {
        $(item).removeClass('active');
    });
    $(listElem).addClass('active');

    let columns = table.columns;
    let trHead = $(`<tr></tr>`);

    columns.forEach(function(columnJson) {
        let column = columnJson.column;
        let name = column.name;
        let type = column.columnType;

        let columnHeader = $(`<th><span>${name}</span><br><span>${type}</span></th>`);

        trHead.append(columnHeader);
    });

    let rowArray = table.data.rows;
    tableBodyElem.html('');
    rowArray.forEach(function(row){
        let rowElem = $(`<tr></tr>`);
        let columnArray = row.columns;

        columns.forEach(function(columnJson) {
            let columnDataJson = findColumnJsonByName(columnArray, columnJson.column.name);
            let columnValue = columnDataJson.value;

            let columnElem = $(`<td>${columnValue}</td>`);

            rowElem.append(columnElem);
        });
        tableBodyElem.append(rowElem);
    });


    tableHeaderlem.html(trHead);

    tableElement.append(tableCaptionElem);
    tableElement.append(tableHeaderlem);
    tableElement.append(tableBodyElem);
    divElem.html(tableElement);
    dbTable.initJQueryTable(tableElement);
}

function findColumnJsonByName(columnArray, name) {
    let results = null;
    try {
        columnArray.forEach( function(columnJson){
            if(columnJson.name == name){
                results = columnJson;
                throw new Error('break!');
            }
        });
    } catch (e) {
    }

    return results;
}

function retrieveOptionContent(buttonElem) {
    let jsonData = $(buttonElem).data('option');
    let optionContent = $('#db-util-option-content');

    $.ajax({
        url: jsonData.path,
        type: 'GET',
        dataType: "html",
        success: function(resultData) {
            optionContent.html(resultData);
        }
    });

    let buttonElements = $('.random-options');
    buttonElements.each(function(idx, elem) {
        $(elem).removeClass('active');
    });
    $(buttonElem).addClass('active');
}

function takeDBSnapshot() {
    let dbName = $('#name').val();
    let dbUsername = $('#username').val();
    let dbPassword = $('#password').val();
    let dbHost = $('#host').val();
    let dbPort = $('#port').val();

    let formData = {
        'name': dbName,
        'username': dbUsername,
        'password': dbPassword,
        'host': dbHost,
        'port': dbPort
    };

    $.ajax({
        url: "/snapshots/create",
        type: 'POST',
        data: JSON.stringify(formData),
        contentType: "application/json",
        dataType: 'json',
        processData: false,
        headers: {
            Accept: "application/json"
        },
        success: function(resultData) {
            alert("Download completed");
        }
    });
}

function deleteSnapshot(elem) {
    let path = $(elem).data('path');
    if(confirm('Are you sure to delete the snapshot with path: ' + path)){
    $.ajax({
            url: "/snapshots/delete-snapshot?filePath=" + encodeURIComponent(path),
            type: 'POST',
            contentType: "application/json",
            dataType: 'json',
            processData: false,
            headers: {
                Accept: "application/json"
            }
        }).always(function(responseJson) {
            if(responseJson.success) {
                alert("Snapshot deleted successfully.");
            } else {
                alert("ERROR deleting the snapshot: \n\t - " + responseJson.error);
            }
        });
    }
}