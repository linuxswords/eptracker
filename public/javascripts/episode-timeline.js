

/**
 * episode custom timeline
 *
 * @type {{createTimeLineFor: createTimeLineFor, createTimeLineWith: createTimeLineWith}}
 */
var episodeTimeline = {
    createTimeLineFor: function (showId) {

        epRouter.controllers.Show.rawshow(showId).ajax({
            success: function (data) {
                var timeEvents = [];
                $.each(data, function (index, show) {
                    var tick = {
                        "startDate": show.publishingDate,
                        "endDate": show.publishingDate,
                        "headline": show.identifier + " - " + show.subtitle,
                        "text": ''
                    };
                    timeEvents.push(tick);
                });
                episodeTimeline.createTimeLineWith(showId, timeEvents);
            },
            error: function () {
                console.log('An error occurred while retrieving data!');
            }
        });

    },

    createTimeLineWith: function (title, data) {


        var timeline = {
            "timeline": {
                "headline": title,
                "type": "default",
                "text": "",
                "font": "SansitaOne-Kameron",
                "date": data
            }
        };

        createStoryJS({
            type: 'timeline',
            width: '750',
            height: '325',
            source: timeline,
            embed_id: 'timeline'
        });
    }
}

function tl(title){
    $(document).ready(function(){
        episodeTimeline.createTimeLineFor(title);
    });
}
