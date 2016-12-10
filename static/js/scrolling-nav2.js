//jQuery to collapse the navbar on scroll
$(window).scroll(function() {
    if ($(".navbar").offset().top > 50) {
        $(".navbar-fixed-top").addClass("top-nav-collapse");
    } else {
        $(".navbar-fixed-top").removeClass("top-nav-collapse");
    }
});

//jQuery for page scrolling feature - requires jQuery Easing plugin
$(function() {
  /*
    $('a.page-scroll').bind('click', function(event) {
        var $anchor = $(this);
        $('html, body').stop().animate({
            scrollTop: $($anchor.attr('href')).offset().top
        }, 1500, 'easeInOutExpo');
        event.preventDefault();
    });
   */
});


$( document ).ready(function() {
        var url      = window.location.href;     // Returns full URL
        
        var getUrlParameter = function getUrlParameter(sParam) {
                    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
                    sURLVariables = sPageURL.split('&'),
                    sParameterName,
                    i;
                    
                    for (i = 0; i < sURLVariables.length; i++) {
                        sParameterName = sURLVariables[i].split('=');
                    
                        if (sParameterName[0] === sParam) {
                            return sParameterName[1] === undefined ? "" : sParameterName[1];
                        }
                    }
                    return "";
        };
        
        function submitValidation(){
            if ($("#searchBar").val(query) == ""){
                    alert("Oh Ohh! Please enter a query to continue.");
                    return false;
            }
            return true;
        }
                    
        var query = decodeURI(getUrlParameter("query")).replace(/\+/g, ' ');
        var num = getUrlParameter("num");
        var fromIndex = getUrlParameter("fromIndex");
        var emo = getUrlParameter("emo");

        if (num == ""){
            $("#num").val("10");
        }else{
            $("#num").val(num);
        }
                    
        if (query == ""){
            $("#searchBar").val("");
        }else{
            $("#searchBar").val(query);
        }
                    
        if (fromIndex ===  ""){
            $("#fromIndex").val(0);
        }else{
            $("#fromIndex").val(fromIndex);
        }
                    
        if (emo == ""){
            $("#emo").val("funny");
        }else{
                if(emo == "happy"){
                    document.getElementById("btn-emo").src="static/images/happy.png";
                }else if(emo == "sad"){
                    document.getElementById("btn-emo").src="static/images/sad.png";
                }
            $("#emo").val(emo);
        }

        $( "#submitSearch" ).click(function() {
            $("#searchKey").val($("#searchBar").val());
            $( "#searchForm" ).submit();
        });
                    
        $( "#emo-funny" ).click(function() {
            $("#emo").val("funny");
            document.getElementById("btn-emo").src="static/images/funny.png";

        });
        $( "#emo-happy" ).click(function() {
            $("#emo").val("joy");
            document.getElementById("btn-emo").src="static/images/happy.png";
        });
        $( "#emo-sad" ).click(function() {
            $("#emo").val("sad");
            document.getElementById("btn-emo").src="static/images/sad.png";
        });
                    
        $("#searchBar").keypress(function (e) {
            if (e.which == 13) {
                    $("#searchKey").val($("#searchBar").val());
                    $('#searchForm').submit();
                    return false;
            }
        });
                    
                    
});
