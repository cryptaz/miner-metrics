'use strict';

angular.module('minerMetricsApp')
    .controller('MainController', ['$scope', '$http', function ($scope, $http) {
        console.log('Main page opened');
        $scope.initialized = null;
        $scope.claymores = [];
        
        $http.get('status.json').then(function (response) {
            $scope.initialized = response.data.initialized;
            $scope.started = response.data.started;
        }, function (error) {
            $scope.initialized = false;
            $scope.started = false;
        })

    }]);
