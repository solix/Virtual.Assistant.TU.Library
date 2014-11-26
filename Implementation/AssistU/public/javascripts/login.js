demo();  //call the demo function


//A demo function - shows one colour first then switches to another
function demo()
{
  $("body").addClass("blue");  
  setTimeout(function(){changeGradient("body", "blue", "green",  2000);},1000);  
}


//The important function, actually fades in another gradient
function changeGradient(object, oldClass, newClass, t)
{  
  
  $("#blackOut").removeClass(oldClass);      //Adds the new colour to temporary blackout div
  $("#blackOut").addClass(newClass);      //Adds the new colour to temporary blackout div
  $("#blackOut").animate({opacity:1},t);  //Animate the blackout div opacity to 1
 
  
  //To match time when blackout is complete
  setTimeout(function()
  {
    $(object).removeClass(oldClass);      //Remove old colour from object
    $(object).addClass(newClass);         //Add new colour to object
    $("#blackOut").css({opacity:0});      //Hide blackout ready for another change
  },t);
  
}

