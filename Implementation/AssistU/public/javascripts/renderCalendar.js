$(document).ready(function () {

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    var calendar = $('#calendar').fullCalendar({
        timeFormat:'H:mm{ - H:mm}',
        header:{
            left:'prev,next today',
            center:'title',
            right:'month,agendaWeek,agendaDay'
        },
        selectable:true,
        selectHelper:true,
        select:function (start, end, allDay) {
            var title = prompt('Event Title:');
            var description = prompt('Description:')
            if (title) {
                jQuery("#newTitle").val(title);
                jQuery("#newDescription").val(description);
                jQuery("#newStart").val(convertDate(start));
                jQuery("#newEnd").val(convertDate(end));
                jQuery("#newAllDay").val(allDay);

                jQuery.ajax({
                    type: 'POST',
                    url:  jQuery("#eventFormNew").attr("action"),
                    data: jQuery("#eventFormNew").serialize() ,
                    dataType: "json",
                    statusCode: {
                        200: function(data) {
                            calendar.fullCalendar('renderEvent',{id:data.id,title:title,description:description,start:start,end:end,allDay:allDay, url:data.url },true);
                        }
                    }

                });
            } else {
                alert("Title is required!");
            }
            calendar.fullCalendar('unselect');
        },
        eventDrop:function(event,dayDelta,minuteDelta,allDay,revertFunc){

            if (typeof event.id == "undefined"){
                alert("This event can not be changed!");
                revertFunc();
                return false;
            }

            jQuery("#moveId").val(event.id);
            jQuery("#moveDayDelta").val(dayDelta);
            jQuery("#moveMinuteDelta").val(minuteDelta);
            jQuery("#moveAllDay").val(allDay);


            jQuery.ajax({
                type:   'POST',
                url:    jQuery("#eventFormMove").attr("action"),
                data:   jQuery("#eventFormMove").serialize(),
                statusCode:{
                    400: function(data) {
                        revertFunc();
                        alert(data.responseText);
                    }
                }
            });

        },

        eventResize: function(event,dayDelta,minuteDelta,revertFunc) {
            if (typeof event.id == "undefined"){
                alert("This event can not be changed!");
                revertFunc();
                return false;
            }

            jQuery("#resizeId").val(event.id);
            jQuery("#resizeDayDelta").val(dayDelta);
            jQuery("#resizeMinuteDelta").val(minuteDelta);

            jQuery.ajax({
                type:   'POST',
                url:    jQuery("#eventFormResize").attr("action"),
                data:   jQuery("#eventFormResize").serialize(),
                statusCode:{
                    400: function(data) {
                        revertFunc();
                        alert(data.responseText);
                    }
                }
            });
        },
        windowResize: function(view) {
            setNewHeight();
        },
        editable:true,

        events:
            {
            url:"/events.json",
            cache: true
        },
        eventMouseover: function(event) {

            var tooltip = '<div class="tooltipevent well" style="width:250px;height:250px;background:#e5e8ff;position:absolute;z-index:10001;"><p><b>'+ event.title +'</b><br></p><p>' + event.description + '</p></div>';
            //var tooltip='<div class="timeline-panel"><div class="timeline-body"><p>'+ event.description+'</p></div></div>';
            $("body").append(tooltip);
            $(this).mouseover(function(e) {
                $(this).css('z-index', 10000);
                $('.tooltipevent').fadeIn('500');
                $('.tooltipevent').fadeTo('10', 1.9);
            }).mousemove(function(e) {
                $('.tooltipevent').css('top', e.pageY + 10);
                $('.tooltipevent').css('left', e.pageX + 20);
            });
        },

        eventMouseout: function(event) {
            $(this).css('z-index', 8);
            $('.tooltipevent').remove();
        }



    });
    setNewHeight();
});

function convertDate(date){
    return(date.getDate()+"."+(date.getUTCMonth()+1)+"."+date.getUTCFullYear()+" "+date.getHours()+":"+date.getMinutes());
}

function setNewHeight() {
    newHeight = jQuery(window).height() - 70; // 60 is padding of the body tag in main.css (required for Bootstrap's header)
    $('#calendar').fullCalendar('option', 'height', newHeight);
}


