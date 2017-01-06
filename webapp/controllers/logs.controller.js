'use strict';

angular.module('minerMetricsApp')
    .controller('LogsController', ['$scope', '$http', function ($scope, $http) {
        console.log('Logs page opened');
        $scope.log = false;

        $scope.showLog = function (name) {
            if(name == 'startup') {
                $http({
                    url: 'startup_log.html',
                    method: 'GET',
                    transformResponse: undefined
                }).then(function (response) {
                        $scope.log = response.data
                }, function (error) {
                    $scope.log = false;
                    alert('Error during log opening, try watching logs by yourself (jumping into Docker)');
                })
            }
            if(name == 'daemon') {

                $http({
                    url: 'daemon_log.html',
                    method: 'GET',
                    transformResponse: undefined
                }).then(function (response) {
                    $scope.log = response.data
                }, function (error) {
                    $scope.log = false;
                    alert('Error during log opening. Possibly miner-metrics not started. If so, check logs by yourself (jumping into Docker)');
                })
            }
            return;
        }

    }]);
