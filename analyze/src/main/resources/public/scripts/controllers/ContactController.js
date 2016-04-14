app.controller('ContactController', function($scope, $rootScope, $location, $http) {
    function initialize() {
        $scope.contacts = [];
        $scope.keyword = '';
        $scope.page = 0;
        $scope.selectedIds = [];
        $scope.newId = -1;
        $scope.searchContacts(0);
        $scope.switchToMode($location.path());
    }
    
    $scope.switchToMode = function(mode) {
        if (mode === '/edit') {
            $http.get('rest/security/user')
                .success(function(user) {
                    $scope.editMode = user;
                    $location.path($scope.editMode? '/edit' : '/view');
                });
        } else {
            $scope.editMode = false;
            $location.path('/view');
        }
    };
    
    // watch selected contacts
    $scope.$watch('contacts|filter:{selected:true}', function (results) {
            $scope.selectedIds = results.map(function(contact) {
                contact.selected = true;
                return contact.id;
            });
        }, true)
    
    $scope.searchContacts = function(page) {
        if ($scope.isLoading) {
            return;
        }
        
        if ($scope.selectedIds.length > 0) {
            if (!confirm("The selected contacts are not handled. Forget them and continue to search?")) {
                return;
            }
        }
        
        if (!page) {
            // reset contacts list if search again
            $scope.contacts = [];
            page = 0;
        }
        
        var PAGE_SIZE = 200;
        $scope.page = page;
        $scope.isLoading = true;
        $http.get('rest/contacts?keyword=' + $scope.keyword + '&page=' + $scope.page + '&pageSize=' + PAGE_SIZE)
             .success(function(items) {
                 $scope.hasMoreContacts = (items.length >= PAGE_SIZE);
                 for (var i=0; i<items.length; i++) {
                     $scope.contacts.push(items[i]);
                 }
             })
             .finally(function() {
                 $scope.isLoading = false;
             });
    }
    
    $scope.newContact = function(type) {
        $scope.keyword = '';
        $scope.contacts.unshift({
            id : $scope.newId--,
            type : type
        });
    };

    $scope.saveContact = function(contact) {
        if (!confirm('Your changes will be saved. Are you sure?')) {
            return;
        }
        
        delete contact.errors;
        
        var action = (contact.id < 0) ? $http.post : $http.put;
        var uri = (contact.id < 0) ? 'rest/contacts/' : 'rest/contacts/' + contact.id;
        action(uri, contact).then(function(response) {
            contact.id = response.data.id;
            contact.errors = {};
            document.getElementById('filter').focus();
        }, function(response) {
            if (response.status == 400) {
                contact.errors = response.data;
            }
        });
    };

    $scope.deleteContacts = function() {
        if (!confirm('Are you sure you want to delete selected contact(s)?')) {
            return;
        }
        
        var ids = $scope.selectedIds;
        $http.delete('rest/contacts?ids=' + ids).then(function(response) {
            for (var i = 0; i < ids.length; i++) {
                for (var j = $scope.contacts.length - 1; j >= 0; j--) {
                    if ($scope.contacts[j].id === ids[i]) {
                        $scope.contacts.splice(j, 1);
                        break;
                    }
                }
            }
        });
    };
    
    $scope.selectPhoto = function(contactId) {
        if (!$scope.editMode || contactId <= 0) {
            return;
        }
        
        document.getElementById('file' + contactId).click();
    };
    
    $scope.uploadPhoto = function(fileId) {
        if (!confirm('Are you sure you want to upload this photo?')) {
            return;
        }
        
        var file = document.getElementById(fileId).files[0];
        var formData = new FormData();
        formData.append('file', file);

        var contactId = fileId.substring(4);
        $http.post('rest/photos/' + contactId, formData, {
                headers: { 
                    'Content-Type': undefined,
                },
                transformRequest: angular.identity,
              })
              .success(function() {
                  var img = document.getElementById('img' + contactId);
                  img.src = 'rest/photos/' + contactId + '?t=' + new Date().getTime();
             });
    };
    
    initialize();
});