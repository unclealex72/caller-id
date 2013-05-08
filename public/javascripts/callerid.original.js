	    var map;
	    geocoder = new google.maps.Geocoder();
        var marker = new google.maps.Marker();
	    
		function createPage(pageNumber, calls) {
			var pageId = "page" + pageNumber;
            var navId = "nav" + pageNumber;
			dust.render("page", {
				id : pageId
			}, function(err, results) {
				$("#calls").append(results);
			});
            dust.render("nav", {
                id : navId,
                idx : pageNumber
            }, function(err, results) {
                $("#nav").append(results);
            });
			var page = $("#" + pageId)
            var nav = $("#" + navId)
			if (!pageNumber) {
				page.add(nav).addClass("active")
			}
			$.each(calls, function(idx, call) {
				dust.render("call", call, function(err, results) {
					page.append(results);
				})
			});
		}
		function loadCalls() {
			return $.ajax({
				url : "calls.json",
				dataType : "json",
				success : function(calls) {
					var page = 0;
					while (calls.length) {
						createPage(page++, calls.splice(0, 10));
					}
                    // Make sure that all carousel pages have the same height.
					var height = null
					$("#calls>div").each(function(page) {
						if (height) {
							$(this).height(height);
						}
						else {
							height = $(this).height();
						}
					});
					// Link the paging controls to the carousel.
		            $("#carousel").bind('slid', function() { 
		                var index = $(this).find(".active").index(); 
		                $("#nav li").removeClass("active").eq(index).addClass("active"); 
		            });
		            $("#nav a").click(function(e) {
		                var page = parseInt($(this).attr("data-page")); 
                        $("#carousel").carousel("pause");
		                $("#carousel").carousel(page);
		            });
		            
		            // Add functionality to all the buttons.
		            $("#calls button.contact").click(function(e) {
                        $("#contact-text-area").text($(this).attr("data-number"));
		            	$('#contact-modal').on('shown', function () {
	                        $("#contact-text-area")[0].select();
		            	});
		            	$("#contact-modal").modal("show");
		            });
                    $("#calls button.search").click(function(e) {
                        var searchTerm = $(this).attr("data-search-term");
                        window.open("https://www.google.co.uk/search?q=" + searchTerm, "_blank");
                    });
                    $("#calls button.map-marker").click(function(e) {
                        var countryCode = $(this).attr("data-country-code");
                        var location = $(this).attr("data-location");
                        geocoder.geocode( { address: location, region: countryCode }, function(results, status) {
                            if (status == google.maps.GeocoderStatus.OK) {
                              map.panTo(results[0].geometry.location);
                              marker.setMap(map);
                              marker.setPosition(results[0].geometry.location);
                            } else {
                              alert("Geocode was not successful for the following reason: " + status);
                            }
                          });

                    });
				}
			});
		}
		function initialiseMap() {
			$("#map_canvas").height($("#all-calls").height());
			var mapOptions = {
				center : new google.maps.LatLng(50, 0),
				zoom : 5,
				mapTypeId : google.maps.MapTypeId.ROADMAP
			};
			map = new google.maps.Map(document.getElementById("map_canvas"),
					mapOptions);
		}
		$(document).ready(function() {
			var queue = $.Deferred();
			queue.resolve();
			queue = queue.then(loadCalls);
			queue = queue.then(initialiseMap);
		});
	</script>
