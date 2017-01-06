'use strict';

angular.module('minerMetricsApp')
    .controller('MainController', ['$scope', '$http', function ($scope, $http) {
        console.log('Main page opened');
        $scope.initialized = null;
        $scope.game = false;
        $scope.claymores = [];

        $http.get('status.json').then(function (response) {
            $scope.initialized = response.data.initialized;
            $scope.started = response.data.started;
            if (response.data.claymores != null) {
                $scope.claymores = response.data.claymores;
            }
            console.log($scope.claymores);
        }, function (error) {
            $scope.initialized = false;
            $scope.started = false;
        });


        $scope.generateDashboard = function (id) {
            if($scope.claymores[id] == null){
                alert('No claymores defined');
                return;
            }
            var url = $scope.claymores[id].url;
            var cards = $scope.claymores[id].cardCount;
            $http({
                url: 'templates/dashboard/grafana_base.json',
                method: 'GET',
                transformResponse: undefined
            }).then(function (response) {
                console.log(response);
                var baseTemplateJson = response.data;
                var baseTemplate = JSON.parse(baseTemplateJson);
                $http({
                    url: 'templates/dashboard/grafana_card_row.json',
                    method: 'GET',
                    transformResponse: undefined
                }).then(function (response) {
                    var cardTemplateJson = response.data;
                    console.log(cardTemplateJson);
                    for(var i=0;i<$scope.claymores[id].cardCount;i++) {
                        var cardTemplate = JSON.parse(cardTemplateJson.replace("*", id));
                        cardTemplate.forEach(function (template) {
                            baseTemplate.rows.push(template);
                        });
                        $scope.template = JSON.stringify(baseTemplate);
                    }

                });
            });

        }

    }]);
