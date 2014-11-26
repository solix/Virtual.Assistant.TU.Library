/**
 * Created by Kupletsky Sergey
 *
 * Hierarchical timing
 * Add specific delay for CSS3-transition to elements.
 */

(function($) {
  var speed = 900;
  var container =  $('.display-animation');  
  container.each(function() {   
    var elements = $(this).children();
    elements.each(function() {      
      var elementOffset = $(this).offset(); 
      var offset = elementOffset.left*0.8 + elementOffset.top;
      var delay = parseFloat(offset/speed).toFixed(2);
      $(this)
        .css("-webkit-transition-delay", delay+'s')
        .css("-o-transition-delay", delay+'s')
        .css("transition-delay", delay+'s')
        .addClass('animated');
    });
  });
})(jQuery);