$(document).ready(function() {
        collapseTree();
        let disabledImportButton = false;
    });

function collapseTree() {
    // delegated handler
    $(".list-group-tree").on('click', "[data-toggle=collapse]", function(){
      $(this).toggleClass('in');
      $(this).find(".list-group.collapse").collapse('toggle');

      // next up, when you click, dynamically load contents with ajax - THEN toggle
      return false;
    });
}

function importData() {
  console.log('Importing data...');
  let form = document.getElementById('import-form');
  form.action='/lookup-table/import-data';
  form.submit();
}