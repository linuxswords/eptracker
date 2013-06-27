$(document).ready(function(){

    $('.momentdate').each(function(index,element){
       var date =$(this).attr('data-date');
       var dateDifference = moment(date, 'YYYYMMDD').fromNow();
        $(this).text(dateDifference);
    });


});