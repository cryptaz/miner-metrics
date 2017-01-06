(function() {
    'use strict';

    angular
        .module('minerMetricsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('main', {
            url: '/main',
            templateUrl: 'templates/main.html',
            controller: 'MainController'
        });
    }
})();
