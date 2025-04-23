$(document).ready(() => {
    console.log('snapshot-comparator.html is ready.');
    $('#comparator-btn').on('click', compareSnapshots);
});

function compareSnapshots() {
    let snapshot1 = $('#snapshot-database-1').find(':selected');
    let snapshot2 = $('#snapshot-database-2').find(':selected');

    console.log(`compareSnapshots: { snapshot1: ${snapshot1.val()}, snapshot2: ${snapshot2.val()} }`);

    if( snapshot1 && snapshot2 ){
        let spinner = showRandomSpinning();
        let comparatorResultsDiv = $('#comparator-results');
        comparatorResultsDiv.html('');
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
                    $.each(createRenderedComparisonResults(resultData), function(index, value){
                        comparatorResultsDiv.append(value);
                    });
                }
            }).always(function() {
                spinner.remove();
            });
    } else {
        alert('Please select both snapshots to compare.');
    }
}

function createRenderedComparisonResults( resultData ) {
    let tableDiffArray = [];
    resultData.forEach( (difference) => {
        if( difference.reasonList.length > 0 ) {
            let tableDifference = $(`
                <div class="accordion-item">
                    <h2 class="accordion-header">
                        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#${difference.table}-panelsStayOpen-collapseTwo" aria-expanded="false" aria-controls="panelsStayOpen-collapseTwo">
                            <b>${difference.table}</b>
                        </button>
                    </h2>
                    <div id="${difference.table}-panelsStayOpen-collapseTwo" class="accordion-collapse collapse">
                        <div class="accordion-body">`
                            + createRenderedDifferenceTable(difference) +
                        `</div>
                    </div>
                </div>
            `);

            tableDiffArray.push(tableDifference);
        }
    });

    return tableDiffArray;
}

function createRenderedDifferenceTable(difference) {
    let tableElement = `
        <table class="table">
            <thead>
                <tr>
                    <th>Tables</th>
                    <th>Differences</th>
                </tr>
            </thead>
            <tbody>` +
            createTableDifferenceContent(difference)
             +
            `</tbody>
        </table>
    `;

    return tableElement;
}

function createTableDifferenceContent(difference) {
    let rowsStr = '';
    let firstRow = true;
     difference.reasonList.forEach( (reason) => {
     rowsStr = rowsStr + `<tr>`;
         if( firstRow ) {
             rowsStr = rowsStr + `<td style="vertical-align: top;" rowspan="${difference.reasonList.length}"><b>${difference.table}</b><br>${difference.sqlQuery}</td>`;
             firstRow = false;
         }
         rowsStr = rowsStr + `<td style="vertical-align: top;">${reason}</td></tr>`;
     } );

     return rowsStr;
}

function showSnapshotList( selectDB ) {
    let selectId = $(selectDB).attr('id');
    let database = $(selectDB).find(':selected');

    let snapshotSelect = $(`#snapshot-${selectId}`);
    snapshotSelect.html('<option></option>');

    let snapshotListJson = database.data('snapshots');

    if(snapshotListJson){
        snapshotListJson.forEach(function(snapshot) {
            let snapshotTitle = 'N/A';
            if( snapshot.snapshot.snapshotTitle ) {
                snapshotTitle = snapshot.snapshot.snapshotTitle;
            }

            snapshotSelect.append($(`<option value='${snapshot.snapshot.snapshotRelativePath}'>${snapshotTitle} - ${snapshot.snapshot.creationDate}</option>`));
        });
    }
}