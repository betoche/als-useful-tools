$(document).ready(() => {
   $("#modules").on('click', () => {

   });
});

function showHideDetailsBlock( elementId ) {
    let details = $(`#${elementId} > #details`);
     if( details.is(":visible") ) {
         details.hide();
     } else {
         details.show();
     }
}