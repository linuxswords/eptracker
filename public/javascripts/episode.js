/*
  episode js
 */

$('.momentdate').each(function(index,element){
   var date =$(this).attr('data-date');
   var dateDifference = moment(date, 'YYYYMMDD').fromNow();
    $(this).text(dateDifference);
});

function deleteShow(source) {
    var elem = $(source);
    var title = elem.attr('rel');
    deleteEp.controllers.Show.delete(title).ajax({
        success: function(data){
            elem.closest('li').slideUp();
        },
        error: function() {
            alert('An error occurred while saving!');
        }
    });
}

$(document).ready(function(){

    var showing = false;
    var loading = false;
    var titleElement = $('.showtitle');
    var title = titleElement.html();
    titleElement.hover(
            function(){
                if(!loading) {
                    loading = true;
                    getDescriptionInto(title, '.showinfo ');
                }
            },
            function(){
                showing = false;
            }
    )
            .click(
            function(){
                $('.showinfo').toggle('slow');
            }
    )
            .css('cursor', 'pointer');



    $('.characterbox').hover(
        function(){
            $('.characterepbox').hide();
            $(this).find('.characterepbox').show()
        },
        function(){}
    );

    $('.characterbox span.close').click(function(){
        $('.characterepbox').hide();
    })  ;

    $('.characters').bind('mouseout', function(){
    });

    jsRoutes.controllers.Show.epGuideData().ajax({
        success: function(data){
            $("input.importField").autocomplete(
                {
                    source:data,
                    minLength:2,
                    autoFocus:true,
                    position : {
                        my: 'left bottom',
                        at: 'left top',
                        collision:'none'
                    }
                });
        }
    });

    $("span.flag").click(
        function(event) {
            var elem = $(this);
            var title = elem.closest('li').attr('title')
            var state = elem.hasClass('flag-red');
            var id = elem.attr('rel');
            consumeEp.controllers.Show.consume(id,title,state ? 1 : 0).ajax({
                success: function(data){
                    if(state)
                    {
                        elem.removeClass('flag-red').addClass('flag-green');
                        elem.closest('li').prevAll('[title=\'' + title + '\']').find('span.flag').removeClass('flag-red').addClass('flag-green');
                    }
                    else
                    {
                        elem.removeClass('flag-green').addClass('flag-red');
                    }
                },
                error: function() {
                    alert('An error occurred while saving!');
                }
            });
        }
    );

    $("span.showtitle, a.update, .torrent a").tooltip();


});

function getDescriptionInto(title, selector){
    var service_url = 'https://www.googleapis.com/freebase/v1/mqlread';

    // get mid
    var query = [
        { 'id': null, 'name': title, 'type':'/tv/tv_program', 'mid':null}
    ];

    // start calls by getting the mid of the tv program first
    $.getJSON( service_url + '?callback=?', {query:JSON.stringify(query)}, function(response){
        if(response.result.length > 0) {
            $.each(response.result, function(i, element){
                console.log('found series');
                setDescriptionFrom(element.mid);
            });
        } else {
            $(selector).html('"'+title+'" was not found on freebase');
        }
    });

    function setDescriptionFrom(tv_program_mid){
        var service_url = 'https://www.googleapis.com/freebase/v1/search';
        var query = {
            query : tv_program_mid,
            output : '(description)'
        };

//    $.getJSON( service_url + '?callback=?', { query:JSON.stringify(query)}, function(response){
        $.getJSON( service_url + '?query=' + tv_program_mid +'&output=(description)&callback=?', {}, function(response){
            if(response.result.length > 0) {
                var description = response.result[0].output.description['/common/topic/description'][0];
                $(selector).html(description);
            } else {
                $(selector).html('no description was not found on freebase');

            }
        });
    }


}


var freebasecall = function(text){
    var text = text || 'breaking bad';
    var service_url = 'https://www.googleapis.com/freebase/v1/mqlread';
    var query = [
        { 'id': null, 'name': text, 'type':'/tv/tv_program', 'mid':null}
    ];
    $.getJSON( service_url + '?callback=?', {query:JSON.stringify(query)}, function(response){
        if(response.result.length > 0) {
            $.each(response.result, function(i, element){
                console.log('found series');
                getPicture(element.mid);
                getDescription(element.mid);
            });
        } else {
            console.log('did not found a tv programm in freebase for ' + text);
        }


    });

}

function getDescription(mid){
    var service_url = 'https://www.googleapis.com/freebase/v1/search';
    var query = {
        query : mid,
        output : '(description)'
    };

//    $.getJSON( service_url + '?callback=?', { query:JSON.stringify(query)}, function(response){
    $.getJSON( service_url + '?query=' +mid +'&output=(description)&callback=?', {}, function(response){
        if(response.result.length > 0) {
            console.log(response.result[0].output.description['/common/topic/description']);
        } else {
            console.log('did not found a description for mid=' + mid);
        }
    });
}

var getPicture = function(mid){
    var service_url = 'https://www.googleapis.com/freebase/v1/mqlread';
    var imageuri_prefix = 'https://usercontent.googleapis.com/freebase/v1/image';
    var query = [
        { 'mid': mid, '/common/topic/image':[{'mid':null}]}
    ];
    $.getJSON( service_url + '?callback=?', {query:JSON.stringify(query)}, function(response){
        if(response.result.length > 0) {
            $.each(response.result, function(i, element){
                $('<img src="' + imageuri_prefix + element.mid + '" />').appendTo(document.body);
            });
        } else {
            console.log('did not found any pictures on freebase for mid=' + mid);
        }

    });
}
