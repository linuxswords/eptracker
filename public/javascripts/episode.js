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

    $("span.showtitle, a.update, .isohunt a").tooltip();


});