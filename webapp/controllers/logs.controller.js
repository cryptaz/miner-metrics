'use strict';

angular.module('minerMetricsApp')
    .controller('LogsController', ['$scope', '$http', '$q', function ($scope, $http, $q) {
        console.log('Logs page opened');
        $scope.log = false;
        $scope.daemonClicked = false;
        $scope.error = {
            noStatusFile: null,
            notStarted: null,
            template: null,
            noLogFileFound: null
        };

        $scope.tickStatus = function () {
            var deferred = $q.defer();
            $http.get('status.json').then(function (response) {
                $scope.initialized = response.data.initialized;
                $scope.started = response.data.started;
                if (response.data.claymores != null) {
                    $scope.claymores = response.data.claymores;
                }
                deferred.resolve();
            }, function (error) {
                $scope.initialized = false;
                $scope.started = false;
                deferred.reject();
            });
            return deferred.promise;
        };

        $scope.tickStatus();

        $scope.showLog = function (name) {
            $scope.tickStatus().then(function (result) {
                if(name == 'startup') {
                    $scope.daemonClicked = false;
                    $http({
                        url: 'startup_log.html',
                        method: 'GET',
                        transformResponse: undefined
                    }).then(function (response) {
                        $scope.error.noLogFileFound = false;
                        $scope.log = response.data
                    }, function (error) {
                        $scope.error.noLogFileFound = true;
                        $scope.log = false;
                    })
                }
                if(name == 'daemon')  {
                    $scope.daemonClicked = true;
                    if(!$scope.started) {
                        $scope.error.notStarted = true;
                        $scope.log = false;
                        return;
                    }
                    $scope.error.notStarted = false;
                    $http({
                        url: 'daemon_log.html',
                        method: 'GET',
                        transformResponse: undefined
                    }).then(function (response) {
                        $scope.error.noLogFileFound = false;
                        $scope.log = response.data
                    }, function (error) {
                        $scope.error.noLogFileFound = true;
                        $scope.log = false;
                    })
                }
            }, function (error) {
                $scope.log = false;
                $scope.error.noStatusFile = true;
                console.log('Status could not obtained');
            });
        }

    }]);
