(function() {
    'use strict';

    angular
        .module('minerMetricsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('logs', {
            url: '/logs',
            templateUrl: 'templates/logs.html',
            controller: 'LogsController'
        });
    }
})();
