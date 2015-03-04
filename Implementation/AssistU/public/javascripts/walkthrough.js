// Instance the tour
var tour = new Tour({
    storage: false,

    debug: true
});

tour.addSteps([{
    element: "#one",
    placement: "top",
    backdrop: true,
    title: "Welcome Buddy ",
    content: "This pop-up walks you through the app in less than 5 minute. You are now in the dashboard page. below is your overview."
}, {
    element: ".two",
    title: "Side panel",
    placement: "right",
    content: "This is a side panel , you can have a quick access of all availible features, you can also search for an article quickly on google scholar, science direct, tudelft repository.click on project"
}]);

tour.init().start(true);



