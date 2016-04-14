var app = angular.module('contactApp', []);
app.config(function($httpProvider) {
    $httpProvider.responseInterceptors.push(function ($rootScope, $q) {
        return function (promise) {
            return promise.then(
                function (response) { // success
                    return response;
                },
                function (response) { // error
                    if (response.status === 500) {
                        $rootScope.errorMessage = response.data;
                    }

                    return $q.reject(response);
                }
            );
        };
    });
});