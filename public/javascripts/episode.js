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
    epRouter.controllers.Show.delete(title).ajax({
        success: function(data){
            elem.closest('li').slideUp();
        },
        error: function() {
            alert('An error occurred while saving!');
        }
    });
}

$(document).ready(function(){

    $('.descriptionTrigger').on('click', function(){
        var $this = $(this);

        var epid = $this.data('epid');
        var selectorForDescription = '#' + epid + ' .description';

        if(!$this.data('loaded')){
            var ep_number = +$this.data('epnumber') || 1;
            var season_number = +$this.data('seasonnumber') || 1;
            var series_title = $this.data('series');

            freebase.getEpisodeDescriptionInto(series_title, season_number, ep_number, selectorForDescription);
            $this.data('loaded', true);
        }
        $(selectorForDescription).toggle();

    });

    $('.descriptionTrigger .description').on('click', function(){
        $(this).slideUp('slow');
    });

    var showing = false;
    var loading = false;
    var titleElement = $('.showtitle');
    var title = titleElement.html();
    titleElement.hover(
            function(){
                if(!loading) {
                    loading = true;
                    freebase.getDescriptionInto(title, '.showinfo ');
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
            event.stopImmediatePropagation();
            var elem = $(this);
            var title = elem.closest('.descriptionTrigger').data('series');
            var title = title || elem.closest('li').data('series');
            var state = elem.hasClass('flag-red');
            var id = elem.attr('rel');
            epRouter.controllers.Show.consume(id,title,state ? 1 : 0).ajax({
                success: function(data){
                    if(state)
                    {
                        elem.removeClass('flag-red').addClass('flag-green');
                        elem.closest('li')
                            .prevAll('[title=\'' + title + '\']').find('span.flag').removeClass('flag-red').addClass('flag-green');
                        elem.closest('li').find('.torrent').hide();

                    }
                    else
                    {
                        elem.removeClass('flag-green').addClass('flag-red');
                        elem.closest('li').find('.torrent').show();
                    }
                },
                error: function() {
                    alert('An error occurred while saving!');
                }
            });
        }
    );

    $("span.showtitle, a.update, .torrent a").tooltip();

    if($('.titlecloud').size() > 0){
        var $titleCloud = $('.titlecloud');
        epRouter.controllers.Info.consumeInfo().ajax({
            success: function(data){
                for(title in data) {
                    $titleCloud.find("[data-title='" + title + "']").addClass(data[title]);
                }
            }
        });
    }


    // keycodes catched with shift,
    // u -> upcoming
    // r -> recent
    // d -> random
    // s -> search
    // h -> home
    // n -> next-pagination
    // p -> previous-pagination
    // l -> last-pagination
    $(document).on('keypress', function(event){

        var textAcceptingInputTypes = ["text", "password", "number", "email",
            "url", "range", "date", "month", "week", "time",
            "datetime", "datetime-local", "search", "color", "tel"];

        if($.inArray(event.target.type, textAcceptingInputTypes) > -1){
            return; // early escape if we are in a text input field
        }

        if (event.shiftKey) {

            if (event.keyCode == 85) { // 85 = u
                location.href = '/upcoming';
            } else if (event.keyCode == 82) { // = r
                location.href = '/recent';
            } else if (event.keyCode == 68) { // = d
                location.href = '/random';
            } else if (event.keyCode == 72) { // = h
                location.href = '/';
            } else if (event.keyCode == 78) { // = n
                var url = $('.pagination .next a').attr('href');
                location.href = url;
            } else if (event.keyCode == 80) { // = p
                var url = $('.pagination .prev a').attr('href');
                location.href = url;
            } else if (event.keyCode == 76) { // = l
                var url = $('.pagination .last a').attr('href');
                location.href = url;
            } else if (event.keyCode == 83) { // = s
                $('input[type=search]').focus();
                return false;
            }
        }
    });


});
