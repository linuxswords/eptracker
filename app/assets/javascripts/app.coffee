app = angular.module('eptracker', ['ngRoute', 'filters-inArrayFilter'])



#
#  Routing
#
app.config(['$routeProvider', ($routeProvider) ->
  $routeProvider
  .when('/mediaCloud', {
    templateUrl: '/assets/html/mediaCloud.html'
    controller: 'CloudController'
  })
  .when('/show/:showid', {
    templateUrl: '/assets/html/show.html'
    controller: 'ShowController'
    })
  .otherwise({redirectTo: '/' })
])





#
#  Factories
#
app.factory 'Media', ($http, $log) ->
  appdata = {}
  appdata.medias = {}
  $http.get('/api/torrentengine')
    .success( (data, status, header) ->
      appdata.torrentEngine = data
    )
    .error( (data, status, header) ->
      $log.error('error getting torrentEngine #{data}')
  )
  $http.get('/api/recent')
    .success( (data, status, headers, config) ->
#      console.log("recent call returns #{status}")
      appdata.medias.recent = data
    )
    .error( (data, status, headers, config) ->
      $log.error(status)
      $log.error(data)
  )
  $http.get('/api/upcoming')
    .success( (data, status, headers, config) ->
#      console.log("upcoming call returns #{status}")
       appdata.medias.upcoming = data
    )
    .error( (data, status, headers, config) ->
      $log.error(status)
      $log.error(data)
  )
  appdata

app.factory 'ShowUpdater', ($http, $log) ->
  service = this
  update = (showid) ->
    $log.info("got update call")
    $http.put("/api/show/#{showid}/update")
    .success( (data, status, headers, config) ->
      $log.info("successful update")
    )
    .error( (data, status, headers, config) ->
      $log.error("error update")
    )
  {
    update: update
  }


app.factory 'Cloud', ($http, $log) ->
  clouddata = {}
  clouddata.data = {}
  $http.get('/api/showstat/consumed')
  .success( (data, status, header) ->
    clouddata.data = data
  )
  .error( (data, status, header) ->
    $log.error('error getting clouddata data')
  )
  clouddata





#
#   Services
#
app.service 'ShowServer', ($http, $log) ->
  (showid, holder) ->
    $http.get("/api/show/#{showid}")
    .success( (data, status, headers, config) ->
      holder.medias = data
    )
    .error( (data, status, headers, config) ->
      $log.error(status)
      $log.error(data)
    )

app.service 'ShowConsumer', ($http) ->
  (media) ->
    $http.post("/api/show/#{media.identifier}/#{media.title}/#{if media.consumed then 0 else 1}")
    .success( (data, status, headers, config) ->
      media.consumed = !media.consumed
    )
    .error( (data, status, headers, config) ->
      $log.error(status)
      $log.error(data)
    )



#
#  Directives
#
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




#
#  Filters
#
app.filter 'relDate', -> (time) -> moment(time).fromNow()

app.filter 'classTag', -> (size) ->
  if size < 50 # the coffeescript switch did not work?
    'tag1'
  else if size < 100
    'tag2'
  else
    'tag3'



#
#  Controllers
#
app.controller 'ShowController', ($scope, $routeParams, ShowServer, ShowUpdater) ->
  service = this
  $scope.activeState = ''
  $scope.show = {}
  service.update = (name) ->
    $scope.activeState = 'active'
    ShowUpdater.update(name).then( ->
      $scope.activeState = ''
    )
  ShowServer($routeParams.showid, $scope.show)
  service

app.controller 'ConsumeController', ($scope, ShowConsumer) ->
  $scope.consume = ShowConsumer


app.controller 'MediaController', (Media) ->
  this.data = Media

app.controller 'CloudController', ($scope, Cloud) ->
  this.cloud = Cloud
  $this = this
  startfilter = ['all','partial','none']
  this.statusFilters = startfilter
  $scope.statusFilters = startfilter
  $scope.reset = ->
    $scope.statusFilters = startfilter
    $this.statusFilters = $scope.statusFilters

  $scope.updateStatusFilter = (val) ->
    $scope.statusFilters = [val]
    $this.statusFilters = $scope.statusFilters
  this
