'use strict';
/** Controllers */
angular.module('sseChat.controllers', []).controller('ChatCtrl', function ($scope, $http) {

    $scope.projectids =[ ];
    $.ajax ( { url : "/projectids", async : false, dataType : 'json', success : function ( response ) { $scope.projectids = response } } ) ;
    $scope.lastpid = "";
    $.ajax ( { url : "/projectid", async : false, dataType : 'json', success : function ( response ) { $scope.lastpid = response } } ) ;
    $scope.comments =[ ] ;
    $.ajax ( { url : "/comments", async : false, dataType : 'json', success : function ( response ) { $scope.comments = response } } ) ;
    $scope.subcomments =[ ] ;
    $.ajax ( { url : "/subcomments", async : false, dataType : 'json', success : function ( response ) { $scope.subcomments = response } } ) ;

    $scope.message = { } ;
    $scope.message.subject = "" ;
    $scope.message.content = "" ;
    for(var i = 0; i < $scope.comments.length; i++){
        $scope.message["subcomment" + $scope.comments[i].cid] = "";
    }

    $scope.isChild = false ;
    $scope.currentProject = $.grep($scope.projectids, function(p){return p.projectID === $scope.lastpid.projectID;})[0 ];

    /** change current room, restart EventSource connection */
    $scope.setCurrentProject = function (pid) {
        $scope.currentProject = $.grep($scope.projectids, function(p){return p.projectID === pid;})[0 ];
        $scope.chatFeed.close();
        $scope.comments =[ ] ;
        $.ajax ( { url : "/comments", async : false, dataType : 'json', success : function ( response ) { $scope.comments = response } } ) ;
        $scope.subcomments =[ ] ;
        $.ajax ( { url : "/subcomments", async : false, dataType : 'json', success : function ( response ) { $scope.subcomments = response } } ) ;
        $scope.listen();
    };

    /** change current subject, restart EventSource connection */
    $scope.setCurrentSubjectAndisChild = function (subject, isChild) {
        $scope.message.subject = subject;
        $scope.isChild = isChild;
    };

    $scope.reset = function () {
        $scope.message = {};
        $scope.message.subject = "";
        $scope.message.content = "";
        for(var i = 0; i < $scope.comments.length; i++){
            $scope.message["subcomment" + $scope.comments[i].cid] = "";
        }
    };

    $scope.setSubmessageAsContent = function (cid) {
        $scope.message.content = $scope.message["subcomment" + cid];
    };

    /** posting chat text to server */
    $scope.submitMsg = function () {
        $http.post("/chat", { subject: $scope.message.subject, content: $scope.message.content,
            date: (new Date()).toUTCString(), projectID: $scope.currentProject.projectID, isChild: $scope.isChild});
        $scope.reset();
    };

    /** handle incoming messages: add to messages array */
    $scope.addMsg = function (msg) {
        var newmessage = JSON.parse(msg.data);
        $scope.$apply(function () {
            if(newmessage["isChild"] === true){
                $scope.subcomments.push(newmessage);
            } else {
                $scope.comments.push(newmessage);
            }
        });
    };

    /** start listening on messages from selected room */
    $scope.listen = function () {
        if($scope.projectids.length > 0) {
            $scope.chatFeed = new EventSource ( "/chatFeed/" + $scope.currentProject.projectID ) ;
            $scope.chatFeed.addEventListener ( "message", $scope.addMsg, false ) ;
        }
    };

    $scope.listen();
});