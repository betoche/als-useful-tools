$(document).ready(() => {
    console.log('snapshot-comparator.html is ready.');
    $('#comparator-btn').on('click', compareSnapshots);
});

function compareSnapshots() {
    let snapshot1 = $('#snapshot-database-1').find(':selected');
    let snapshot2 = $('#snapshot-database-2').find(':selected');

    if( snapshot1 && snapshot2 ){
        let spinner = showRandomSpinning();
        let formData = {
            'snapshot1': snapshot1.val(),
            'snapshot2': snapshot2.val()
        };
        $.ajax({
                url: "/snapshots/compare",
                type: 'POST',
                data: JSON.stringify(formData),
                contentType: "application/json",
                dataType: 'json',
                processData: false,
                headers: {
                    Accept: "application/json"
                },
                success: function(resultData) {
                    console.log( resultData );
                    let comparatorResults = $('#comparator-results');
                    let tableElem = $('<table class="table"></table>');
                    let tHeadElem = $('<thead></thead>');
                    let tBodyElem = $('<tbody></tbody>');
                    let tRowElem = $('<tr></tr>');
                    tRowElem.append($(`<th>Tables[${resultData.length}]</th>`));
                    tRowElem.append($('<th>Differences</th>'));
                    tHeadElem.append(tRowElem);

                    resultData.forEach( (difference) => {
                        let firstRow = true;
                        difference.reasonList.forEach((reason)=>{
                            if( firstRow ) {
                                let bodyRowElem = $('<tr></tr>');
                                bodyRowElem.append($(`<td style="vertical-align: top;" rowspan="${difference.reasonList.length}"><b>${difference.table}</b><br>${difference.sqlQuery}</td>`));
                                bodyRowElem.append($(`<td style="vertical-align: top;">${reason}</td>`));
                                tBodyElem.append(bodyRowElem);
                                firstRow = false;
                            } else {
                                let bodyRowElem = $('<tr></tr>');
                                bodyRowElem.append($(`<td style="vertical-align: top;">${reason}</td>`));
                                tBodyElem.append(bodyRowElem);
                            }
                        });
                    });
                    tableElem.append(tHeadElem);
                    tableElem.append(tBodyElem);
                    comparatorResults.html(tableElem);

                    spinner.remove();
                    alert("Snapshot comparator finished!");
                }
            });
    } else {
        alert('Please select both snapshots to compare.');
    }
}

function showSnapshotList( selectDB ) {
    let selectId = $(selectDB).attr('id');
    let database = $(selectDB).find(':selected');

    let snapshotSelect = $(`#snapshot-${selectId}`);
    snapshotSelect.html('<option></option>');

    let snapshotListJson = database.data('snapshots');

    if(snapshotListJson){
        snapshotListJson.forEach(function(snapshot){
            snapshotSelect.append($(`<option value='${snapshot.snapshot.snapshotRelativePath}'>${snapshot.snapshot.creationDate}</option>`));
        });
    }

}