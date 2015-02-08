'use strict';
/** app level module which depends on services and controllers */
angular.module('sseChat', ['sseChat.controllers']);

angular.module('sseChat', ['sseChat.controllers']).filter('hasSameSubject', function(){
    return function(subcomments, subject){
        var arrayToReturn = [];
        for (var i=0; i<subcomments.length; i++){
            if (subcomments[i].subject == subject) {
                arrayToReturn.push(subcomments[i]);
            }
        }
        return arrayToReturn;
    };
});