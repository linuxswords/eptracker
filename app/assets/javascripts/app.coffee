app = angular.module('eptracker', ['ngRoute'])

app.config(['$routeProvider', ($routeProvider) ->
  $routeProvider.when('/mediaCloud', {
    templateUrl: '/assets/html/mediaCloud.html'
    controller: 'CloudCtrl'
  })
  .otherwise({redirectTo: '/' })
])


app.factory 'Media', ($http) ->
  appdata = {}
  appdata.medias = {}
  $http.get('/api/torrentengine')
    .success( (data, status, header) ->
      appdata.torrentEngine = data
    )
    .error( (data, status, header) ->
      console.log('error getting torrentEngine #{data}')
  )
  $http.get('/api/recent')
    .success( (data, status, headers, config) ->
#      console.log("recent call returns #{status}")
      appdata.medias.recent = data
    )
    .error( (data, status, headers, config) ->
      console.log(status)
      console.log(data)
  )
  $http.get('/api/upcoming')
    .success( (data, status, headers, config) ->
#      console.log("upcoming call returns #{status}")
       appdata.medias.upcoming = data
    )
    .error( (data, status, headers, config) ->
      console.log(status)
      console.log(data)
  )
  appdata

app.factory 'Cloud', ($http) ->
  clouddata = {}
  clouddata.data = {}
  $http.get('/api/showstat/consumed')
  .success( (data, status, header) ->
    clouddata.data = data
  )
  .error( (data, status, header) ->
    console.log('error getting clouddata data')
  )
  clouddata

app.directive 'asideMedia', ->
    restrict: 'E'
    scope:
      medias: '='
      torrentEngine: '='
      title: '@'
    templateUrl: '/assets/html/mediaListItem.html'

app.directive 'mediaFlag', ->
    restrict: 'E'
    scope:
      media: '='
    templateUrl: '/assets/html/mediaFlag.html'

app.directive 'mediaTorrent', ->
    restrict: 'E'
    scope:
      torrentEngine: '='
      med: '='
    templateUrl: '/assets/html/mediaTorrent.html'
    link: ($scope, elem, attr) ->
      $scope.showTorrent = (media) ->
        !media.consumed && moment().isAfter(media.publishingDate)


app.filter 'relDate', -> (time) -> moment(time).fromNow()

app.filter 'classTag', -> (size) ->
  if size < 50 # the coffeescript switch did not work?
    'tag1'
  else if size < 100
    'tag2'
  else
    'tag3'

app.controller 'MediaCtrl', (Media) ->
  this.data = Media

app.controller 'CloudCtrl', ($scope, Cloud) ->
  this.cloud = Cloud
  this
