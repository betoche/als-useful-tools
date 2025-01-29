function showInfoCard( buttonElem, optionType ) {
    let jsonData = $(buttonElem).data('option');
    let cardElement = $('#card-info');
    let cardOptionType = $('#card-info-option-type');
    let cardTitle = $('#card-info-title');
    let cardDescription = $('#card-info-description');
    let cardLink = $('#card-info-link');
    let cardElem = $('#card');
    cardElem.removeClass('d-none');
    cardElem.addClass('d-block');


    cardOptionType.text(optionType);
    cardTitle.text(jsonData.title);
    cardDescription.text(jsonData.description);
    cardLink.attr('href', jsonData.path+(jsonData.hasSubOptions?'?optionKey='+jsonData.key:''));

    $('.random-options').each(function(idx, elem) {
        $(elem).removeClass('active');
    });
    $(buttonElem).addClass('active');
}

function showRandomSpinning() {
    let spinner;
    $.ajax({
        url: '/random-spinner',
        type: 'GET',
        dataType: "html",
        async: false,
        success: function(resultData) {
            spinner = $(resultData);
            $('body').append(spinner);
            setTimeout(() => {console.log('removing spinner after 10 seconds!');spinner.remove();}, 10000);
        }
    });

    return spinner;
}