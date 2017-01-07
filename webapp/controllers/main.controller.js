'use strict';

angular.module('minerMetricsApp')
    .controller('MainController', ['$scope', '$http', '$timeout', function ($scope, $http, $timeout) {
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
        }, function (error) {
            $scope.initialized = false;
            $scope.started = false;
        });
        $scope.generateDashboard = function (id) {
            $scope.template = null;
            if ($scope.claymores[id] == null) {
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
                var baseTemplateJson = response.data;
                baseTemplateJson = baseTemplateJson.replace(/#/g, $scope.claymores[id].url);
                var baseTemplate = JSON.parse(baseTemplateJson);
                $http({
                    url: 'templates/dashboard/grafana_card_row.json',
                    method: 'GET',
                    transformResponse: undefined
                }).then(function (response) {
                    var cardTemplateJson = response.data;
                    for (var i = 0; i < $scope.claymores[id].cardCount; i++) {
                        var replaced = cardTemplateJson.replace(/\*/g, i);
                        replaced = replaced.replace(/#/g, $scope.claymores[id].url);
                        var cardTemplate = JSON.parse(replaced);
                        cardTemplate.rows.forEach(function (template) {
                            baseTemplate.rows.push(template);
                        });
                        $scope.template = JSON.stringify(baseTemplate);
                    }
                }, function (error) {
                    //todo
                    console.log(error)
                });
            }, function (error) {
                //todo
                console.log(error)
            });
        }
    }]);
