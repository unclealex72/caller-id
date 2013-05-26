jQuery ->
  # Initialise variables for the Google map.
  map = null
  geocoder = new google.maps.Geocoder()
  marker = null

  # Prepare the carousel.
  $("#page0, #nav0").addClass "active"

  # Make sure that all carousel pages have the same height.
  height = null
  $("#calls>div").each (page) ->
    if height
      $(this).height height
    else
      height = $(this).height()

  #Link the paging controls to the carousel.
  $("#carousel").bind 'slid', () ->
    index = $(this).find(".active").index()
    $("#nav li").removeClass("active").eq(index).addClass("active")

  $("#nav a").click (e) ->
    page = parseInt($(this).attr("data-page"))
    $("#carousel").carousel "pause"
    $("#carousel").carousel page

  # Add functionality to the buttons
  $("#calls button.contact").click (e) ->
    $("#contact-text-area").text($(this).attr("data-number"))
    $('#contact-modal').on 'shown', ->
      $("#contact-text-area")[0].select()
    $("#contact-modal").modal "show"

  $("#calls button.search").click (e) ->
      searchTerm = $(this).attr("data-search-term")
      window.open "https://www.google.co.uk/search?q=" + searchTerm, "_blank"

  $("#calls button.map-marker").click (e) ->
      countryCode = $(this).attr("data-country-code")
      location = $(this).attr("data-location")
      geocoder.geocode { address: location, region: countryCode }, (results, status) ->
        if (status == google.maps.GeocoderStatus.OK)
          loc = results[0].geometry.location 
          map.panTo loc
          if marker == null
            marker = new google.maps.Marker
              map: map
              position: loc
          else
            marker.setPosition location
        else
          alert "Geocode was not successful for the following reason: " + status

  # Initialise the map.
  $("#map_canvas").height $("#all-calls").height()
  mapOptions =
    center: new google.maps.LatLng(50, 0)
    zoom: 5
    mapTypeId: google.maps.MapTypeId.ROADMAP
    
  map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions)
