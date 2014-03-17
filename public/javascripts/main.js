requirejs.config({
    waitSeconds: 5,
    "baseURL": 'javascripts',
    paths: {
        "storyjs":"js/storyjs-embed",
        "timelinejs":"js/timeline-min"
    },
    "shim": {
        'jquery': { deps: ['jquery-1.7.2.min'], exports: '$' },
        'jquery-ui-1.8.19.custom.autocomplete.min': ['jquery-1.7.2.min'],
        'freebase' :['jquery-1.7.2.min'],
        'episode': ['jquery-1.7.2.min', 'moment.min', 'jquery.tools.min', 'freebase'],
        'storyjs': {deps: ['jquery-1.7.2.min'], exports: 'VMM' },
        'timelinejs': {deps: ['jquery-1.7.2.min', 'storyjs']},
        'episode-timeline': { deps: ['jquery-1.7.2.min', 'timelinejs', 'storyjs'], exports: 'episodeTimeline' }
    }

});

require(['jquery-1.7.2.min',
    'jquery-ui-1.8.19.custom.autocomplete.min',
    'moment.min',
    'jquery.tools.min',
    'freebase',
    'episode-timeline',
    'episode'], function($){
    });