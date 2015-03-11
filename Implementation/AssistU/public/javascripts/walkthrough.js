// Instance the tour
var tour = new Tour({
    debug: true
});

tour.addSteps([{
    element: "#one",
    placement: "top",
    title: "Welcome Buddy ",
    content: "This pop-up walks you through the app in less than 5 minute. You are now in the dashboard page. below is your overview."
}, {
    element: ".two",
    title: "Side panel",
    placement: "right",
    content: "This is a side panel , you can have a quick access of all availible features, you can also search for an article quickly on google scholar, science direct, tudelft repository.click on project"
} ,{
    element: "#three",
    title: "notification panel",
    placement: "bottom",
    content: "You can view latest activities on notification panel. click on next to go to Project page"
},{
    element: "#four",
    title: "Project page",
    placement: "right",
    content: "you can Create a new Project or view if you have any invitations "
},{
    element: "#five",
    orphan: true,
    title: "Here is your calendar",
    placement: "right",
    content: "Click on calendar and Plan your future ! That was it :)"
},
    {
    element: "#six",
    title: "Discuss with others",
    placement: "right",
    content: "When you have an active project you can start discussing a topic with other members!"
},{
    element: "#seven",
    title: "Add a Personal Task",
    placement: "right",
    content: "Get yourself organized by making a TODO(s)"
},{
    element: "#eight",
    title: "Tips and Suggestions",
    placement: "right",
    content: "Get the best Tips ever"
},

]);

tour.init().start(true);




