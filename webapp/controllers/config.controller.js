'use strict';

angular.module('minerMetricsApp')
    .controller('ConfigController', ['$scope', '$http', function ($scope, $http) {
        console.log('Config page opened');
        $scope.editInflux = false;
        $scope.claymores = null;
        $scope.addNewMiner = false;
        $scope.savedConfig = null;
        $scope.config = {
            minerEndpoints: null,
            tickTime: 3,
            influxConfig: {
                host: null,
                user: null,
                pass: null,
                db: null
            }
        };

        $scope.tickStatus = function () {
            //fixme refactor to service and $q.all
            var deferred = $q.defer();
            $http.get('status.json').then(function (response) {
                $scope.initialized = response.data.initialized;
                $scope.started = response.data.started;
                deferred.resolve();
            }, function (error) {
                $scope.initialized = false;
                $scope.started = false;
                $http.get('/daemon/status').then(function (response) {
                    $scope.initialized = response.data.initialized;
                    $scope.started = response.data.started;
                    deferred.resolve();
                },function (error) {
                    deferred.reject();
                });
            });
            return deferred.promise;
        };

        $scope.tickStatus();


        $scope.claymore = {
            name: null,
            url: null,
            test: null,
            lastUrl: null
        };

        $scope.response = {
            "minerEndpoints": null,
            "tickTime": 3,
            "influxConfigDTO": {"host": "http://127.0.0.1:8086", "user": "root", "pass": "root", "db": "minermetrics"}
        };

        $http({url: '/daemon/configuration', method: 'GET'})
            .then(function (response) {
                $scope.config = response.data;
            }, function (error) {
                $scope.error = true;
            });

        $scope.testClaymore = function (url) {
                $http.post('/daemon/claymore/test', url).then(function (response) {
                $scope.claymore.test = true;
                console.log(response);
            }, function (error) {
                $scope.claymore.lastUrl = url;
                $scope.claymore.test = false;
                console.log(error);
            })
        };

        $scope.addClaymore = function (claymore) {
            var claymore = {url: claymore.url, name: claymore.name};
            if($scope.config.minerEndpoints == null) {
                $scope.config.minerEndpoints = [];
            }
            $scope.config.minerEndpoints.push(claymore);
            $scope.configEdited = true;
            $scope.claymore = {
                name: null,
                url: null,
                test: null,
                lastUrl: null
            };
            $scope.addNewMiner = false;
        };

        $scope.saveConfig = function () {
            console.log('Saving config');
            $http.post('/daemon/configuration', $scope.config).then(function (response) {
                console.log(response);
                $scope.savedConfig = true;
            }, function (error) {
                console.log(error);
                $scope.savedConfig = false;
            });
        }
    }]);
