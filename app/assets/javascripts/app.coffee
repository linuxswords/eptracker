app = angular.module('eptracker', ['ngRoute', 'filters-inArrayFilter', 'angularUtils.directives.dirPagination'])



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
      $log.error 'error getting torrentEngine', data
  )
  $http.get('/api/recent')
    .success( (data, status, headers, config) ->
      appdata.medias.recent = data
    )
    .error( (data, status, headers, config) ->
      $log.error(status)
      $log.error(data)
  )
  $http.get('/api/upcoming')
    .success( (data, status, headers, config) ->
       appdata.medias.upcoming = data
    )
    .error( (data, status, headers, config) ->
      $log.error "status", status
      $log.error "data", data
  )
  appdata

app.factory 'ShowUpdater', ($http, $log) ->
  update = (showid) ->
    # triggers the re-import of the show
    $http.put("/api/show/#{showid}/update")
    .success( (data, status, headers, config) ->
      $log.info("successful update")
      data
    )
    .error( (data, status, headers, config) ->
      $log.error("error update")
    )
  {
    update: update
  }

app.factory 'ShowServer', ($http, $log) ->
  service = this
  service.get = (showid) ->
    $http.get("/api/show/#{showid}")
    .success( (data, status, headers, config) ->
      data
    )
    .error( (data, status, headers, config) ->
      $log.error(status)
      $log.error(data)
    )
  service


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
      show: '='
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

app.directive 'singleShow', ->
  restrict: 'E'
  scope:
    show: '='
    activeState: '='
    loading: '='
    call: "&"
  templateUrl: '/assets/html/singleShow.html'


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
app.controller 'ShowController', ($scope, $log, $routeParams, ShowServer, ShowUpdater) ->
  service = this
  $scope.loading = false
  $scope.show = {}
  service.update = (name) ->
    $scope.loading = true
    ShowUpdater.update(name).then( (response) ->
      $scope.show.medias = response.data
      $scope.loading = false
    )
  ShowServer.get($routeParams.showid).then( (response) ->
    $scope.show.medias = response.data
  )

  service

app.controller 'ConsumeController', ($scope, $log, ShowConsumer) ->
  $scope.consume = (media) ->
    ShowConsumer(media).success( (response) ->
      if media.consumed
        angular.forEach($scope.show.medias, (m) ->
        # mark every media that was published before also as read
          if media.publishingDate > m.publishingDate && m.showId == media.showId
            m.consumed = true
        )
    )


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
