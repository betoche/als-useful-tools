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
    cardLink.attr('href', jsonData.path+'?optionKey='+jsonData.key);

    $('.random-options').each(function(idx, elem) {
        $(elem).removeClass('active');
    });
    $(buttonElem).addClass('active');
}