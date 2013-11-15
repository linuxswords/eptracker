var freebase = {

    getMidFromTitle : function(title, callback){
        var service_url = 'https://www.googleapis.com/freebase/v1/mqlread';

        // get mid
        var query = [
            { 'id': null, 'name': title, 'type':'/tv/tv_program', 'mid':null}
        ];

        // start calls by getting the mid of the tv program first
        $.getJSON( service_url + '?callback=?', {query:JSON.stringify(query)}, function(response){
            if(response.result.length > 0) {
                $.each(response.result, function(i, element){
                    callback(element.mid);
                });
            } else {
               console.log('"'+title+'" was not found on freebase');
            }
        });

    },


    getDescriptionInto : function(title, selector){

        this.getMidFromTitle(title, setDescriptionFrom);

        function setDescriptionFrom(tv_program_mid){
            var service_url = 'https://www.googleapis.com/freebase/v1/search';
            var query = {
                query : tv_program_mid,
                output : '(description)'
            };
            $.getJSON( service_url + '?query=' + tv_program_mid +'&output=(description)&callback=?', {}, function(response){
                if(response.result.length > 0) {
                    var description = response.result[0].output.description['/common/topic/description'][0];
                    $(selector).html(description);
                    freebase.addPictureInto(title, selector);
                } else {
                    $(selector).html('no description was not found on freebase');

                }
            });
        }
    },


    getEpisodeDescriptionInto: function(title, season_number, episode_number, selector){

        this.getMidFromTitle(title, setEpisodeDescriptionFrom);

        function setEpisodeDescriptionFrom(tv_program_mid){
            var service_url = 'https://www.googleapis.com/freebase/v1/mqlread';

            var query = [{
                "type": "/tv/tv_series_episode",
                "/tv/tv_series_episode/season_number": season_number,
                "/tv/tv_series_episode/episode_number": episode_number,
                "/tv/tv_series_episode/series": [{
                    "mid": tv_program_mid
                }],
                "/common/topic/description": null
            }];

            $.getJSON( service_url + '?callback=?', { query:JSON.stringify(query)}, function(response){
                if(response.result.length > 0) {
                    var description = response.result[0]['/common/topic/description'];
                    console.log("description: " + description);
                    $(selector).text(description);
                } else {
                    $(selector).text('no description was not found on freebase');

                }
            });
        }
    },

    addPictureInto: function(title, selector){

        this.getMidFromTitle(title, prependPicture);

        function prependPicture(mid){
            var service_url = 'https://www.googleapis.com/freebase/v1/mqlread';
            var imageuri_prefix = 'https://usercontent.googleapis.com/freebase/v1/image';
            var query = [
                { 'mid': mid, '/common/topic/image':[{'mid':null}]}
            ];
            $.getJSON( service_url + '?callback=?', {query:JSON.stringify(query)}, function(response){
                if(response.result.length > 0) {
                    $.each(response.result, function(i, element){
                        $(selector).prepend('<img class="showthumb" src="' + imageuri_prefix + element.mid + '" />');
                    });
                } else {
                    console.log('did not found any pictures on freebase for mid=' + mid);
                }

            });
        }
    }
}