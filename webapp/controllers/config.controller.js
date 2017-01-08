'use strict';

angular.module('minerMetricsApp')
    .controller('ConfigController', ['$scope', '$http', function ($scope, $http) {
        console.log('Config page opened');
        $scope.editInflux = false;
        $scope.claymores = null;

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

        $scope.testClaymore = function () {
            $http.post('/daemon/claymore/test').then(function (response) {
                console.log(response);
            }, function (error) {
                console.log(error);
            })
        }
    }]);
