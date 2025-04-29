function showInfoCard( buttonElem, optionType) {
    let jsonData = $(buttonElem).data('option');
    let backgroundClass = $(buttonElem).data('background-class');
    let borderClass = $(buttonElem).data('border-class');
    let textClass = $(buttonElem).data('text-class');

    let cardElement = $('#card-info');
    let cardOptionType = $('#card-info-option-type');
    let cardTitle = $('#card-info-title');
    let cardDescription = $('#card-info-description');
    let cardLink = $('#card-info-link');
    let cardElem = $('#card');

    addCardRandomBootstrapClasses(backgroundClass, borderClass, textClass);

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

function addCardRandomBootstrapClasses(backgroundClass, borderClass, textClass) {
    let cardBody = $('#card-body');
    cardBody.removeClass();
    cardBody.addClass('card-body');
    cardBody.addClass(textClass);

    let card = $('#card-info');
    card.removeClass();
    card.addClass('card');
    card.addClass(backgroundClass);
    card.addClass(borderClass);
}

function showRandomSpinning() {
    let spinner;
    $.ajax({
        url: '/random-spinner',
        type: 'GET',
        dataType: "html",
        async: false,
        success: function(resultData) {
            let waitTime = 20000;
            spinner = $(resultData);
            $('body').append(spinner);
            setTimeout( () => {console.log(`removing spinner after ${waitTime} milli seconds!`);spinner.remove();}, 20000 );
        }
    });

    return spinner;
}