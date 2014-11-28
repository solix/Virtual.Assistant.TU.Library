// var todomvc = angular.module('todomvc', []);

// /**
//  * Services that persists and retrieves TODOs from localStorage
// */
// todomvc.factory('todoStorage', function () {
// 	var STORAGE_ID = 'todos-angularjs-perf';

// 	return {
// 		get: function () {
// 			return JSON.parse(localStorage.getItem(STORAGE_ID) || '[]');
// 		},

// 		put: function (todos) {
//           //alert("saving:"+JSON.stringify(todos));
// 			localStorage.setItem(STORAGE_ID, JSON.stringify(todos));
// 		}
// 	};
// });

// todomvc.directive('todoEscape', function () {
// 	var ESCAPE_KEY = 27;
// 	return function (scope, elem, attrs) {
// 		elem.bind('keydown', function (event) {
// 			if (event.keyCode === ESCAPE_KEY) {
// 				scope.$apply(attrs.todoEscape);
// 			}
// 		});
// 	};
// });

// /**
//  * Directive that places focus on the element it is applied to when the expression it binds to evaluates to true
//  */
// todomvc.directive('todoFocus', function ($timeout) {
// 	return function (scope, elem, attrs) {
// 		scope.$watch(attrs.todoFocus, function (newVal) {
// 			if (newVal) {
// 				$timeout(function () {
// 					elem[0].focus();
// 				}, 0, false);
// 			}
// 		});
// 	};
// });

// todomvc.controller('TodoCtrl', function TodoCtrl($scope, $location, $filter, todoStorage) {
// 	var todos = $scope.todos = todoStorage.get();

// 	$scope.newTodo = '';
// 	$scope.remainingCount = $filter('filter')(todos, {completed: false}).length;
// 	$scope.editedTodo = null;

// 	if ($location.path() === '') {
// 		$location.path('/');
// 	}

// 	$scope.location = $location;

// 	$scope.$watch('location.path()', function (path) {
// 		$scope.statusFilter = { '/active': {completed: false}, '/completed': {completed: true} }[path];
// 	});

// 	$scope.$watch('remainingCount == 0', function (val) {
// 		$scope.allChecked = val;
// 	});

// 	$scope.addTodo = function () {
// 		var newTodo = $scope.newTodo.trim();
// 		if (newTodo.length === 0) {
// 			return;
// 		}

// 		todos.push({
// 			title: newTodo,
// 			completed: false
// 		});
// 		todoStorage.put(todos);

// 		$scope.newTodo = '';
// 		$scope.remainingCount++;
// 	};

// 	$scope.editTodo = function (todo) {
// 		$scope.editedTodo = todo;
// 		// Clone the original todo to restore it on demand.
// 		$scope.originalTodo = angular.extend({}, todo);
// 	};

// 	$scope.doneEditing = function (todo) {
// 		$scope.editedTodo = null;
// 		todo.title = todo.title.trim();

// 		if (!todo.title) {
// 			$scope.removeTodo(todo);
// 		}

// 		todoStorage.put(todos);
// 	};

// 	$scope.revertEditing = function (todo) {
// 		todos[todos.indexOf(todo)] = $scope.originalTodo;
// 		$scope.doneEditing($scope.originalTodo);
// 	};

// 	$scope.removeTodo = function (todo) {
// 		$scope.remainingCount -= todo.completed ? 0 : 1;
// 		todos.splice(todos.indexOf(todo), 1);
// 		todoStorage.put(todos);
// 	};

// 	$scope.todoCompleted = function (todo) {
// 		$scope.remainingCount += todo.completed ? -1 : 1;
// 		todoStorage.put(todos);
// 	};

// 	$scope.clearCompletedTodos = function () {
// 		$scope.todos = todos = todos.filter(function (val) {
// 			return !val.completed;
// 		});
// 		todoStorage.put(todos);
// 	};

// 	$scope.markAll = function (completed) {
// 		todos.forEach(function (todo) {
// 			todo.completed = !completed;
// 		});
// 		$scope.remainingCount = completed ? todos.length : 0;
// 		todoStorage.put(todos);
// 	};
// });

// $(document).ready(function() {});
