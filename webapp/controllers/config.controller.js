'use strict';

angular.module('minerMetricsApp')
    .controller('ConfigController', ['$scope', '$http', function ($scope, $http) {
        console.log('Config page opened');
        $scope.editInflux = false;
        $scope.claymores = null;


        $http({url: '/daemon/configuration', method: 'GET'})
            .then(function (response) {

        }, function (error) {

        })


    }]);
