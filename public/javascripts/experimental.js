var clientId = '720703205602-qbl6kpbobbvapentgos5b6k1e8sk0nm2.apps.googleusercontent.com',
    apiKey = 'AIzaSyBRa8QpgAc21kzW5hsbYfqSRIubkQB-xt0',
    scopes = 'https://www.google.com/m8/feeds';
// Use a button to handle authentication the first time.
function handleClientLoad () {
    console.log("handleClientLoad");
    gapi.client.setApiKey ( apiKey );
    $("#update").on("click", checkAuth);
    //window.setTimeout ( checkAuth, 1 );
}
function checkAuth() {
    console.log("checkAuth");
    gapi.auth.authorize({client_id: clientId, scope: scopes, immediate: true}, handleAuthResult);
}
function handleAuthResult ( authResult ) {
    console.log("handleAuthResult");
    var authorizeButton = document.getElementById ( 'update' );
    if ( authResult && !authResult.error ) {
        authorizeButton.style.visibility = 'hidden';
        var cif = {
            method: 'GET',
            url:  'https://www.google.com/m8/feeds/contacts/default/full/',
            data: {
                "access_token": authResult.access_token,
                "alt":          "json",
                "max-results":  "10000"
            },
            headers: {
                "Gdata-Version":    "3.0"
            },
            xhrFields: {
                withCredentials: true
            },
            dataType: "jsonp"
        };
        $.ajax ( cif ).done ( function ( result ) {
            $ ( '#gcontacts' ).html ( JSON.stringify ( result, null, 3 ) );
        } );
    } else {
        authorizeButton.style.visibility = '';
        authorizeButton.onclick = handleAuthClick;
    }
}
function handleAuthClick ( event ) {
    console.log("handleAuthClick");
    gapi.auth.authorize ( { client_id: clientId, scope: scopes, immediate: false }, handleAuthResult );
    return false;
}