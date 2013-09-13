requirejs.config({
    "baseURL": 'javascripts',
    "shim": {
        'jquery-ui-1.8.19.custom.autocomplete.min': ['jquery-1.7.2.min'],
        'episode': ['jquery-1.7.2.min', 'moment.min', 'jquery.tools.min']
    }
});

require(['jquery-1.7.2.min',
    'jquery-ui-1.8.19.custom.autocomplete.min',
    'moment.min',
    'jquery.tools.min',
    'episode'], function($){

    });