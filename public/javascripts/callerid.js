var clientId = '720703205602-qbl6kpbobbvapentgos5b6k1e8sk0nm2.apps.googleusercontent.com',
    apiKey = 'AIzaSyBRa8QpgAc21kzW5hsbYfqSRIubkQB-xt0',
    scopes = 'https://www.google.com/m8/feeds';

// Use a button to handle authentication the first time.
function handleClientLoad () {
    console.log("handleClientLoad");
    gapi.client.setApiKey ( apiKey );
    //$("#update").on("click", checkAuth);
}

function updateContacts(updateProgressCallback, successCallback, failureCallback) {
    console.log("updateContacts");
    gapi.auth.authorize({
            client_id: clientId,
            scope: scopes,
            immediate: true
        },
        _.partial(handleAuthResult, updateProgressCallback, successCallback, failureCallback));
}

function handleAuthResult (updateProgressCallback, successCallback, failureCallback, authResult) {
    console.log("handleAuthResult");
    if ( authResult && !authResult.error ) {
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
        $.ajax(cif).done(_.partial(onContactsDownloaded, updateProgressCallback, successCallback, failureCallback));
    } else {
        console.log(authResult);
        //authorizeButton.style.visibility = '';
        //authorizeButton.onclick = handleAuthClick;
    }
}

function onContactsDownloaded(updateProgressCallback, successCallback, failureCallback, googleContactsFeed) {
    var googleContacts = googleContactsFeed["feed"];
    console.log(googleContacts);
    var user = googleContacts["author"][0]["email"]["$t"];
    var contacts = _.map(googleContacts["entry"], function(contact) {
        var name = contact["title"]["$t"];
        var phoneNumbers = _.map(contact["gd$phoneNumber"], function(phoneNumber) {
            return {
                number: phoneNumber["$t"],
                type: phoneNumber["rel"].replace("http://schemas.google.com/g/2005#", "")
            }
        });
        return {
            name: name,
            phoneNumbers: phoneNumbers
        };
    });
    var contactsWithPhoneNumbers = _.filter(contacts, function(contact) { return contact.phoneNumbers.length != 0; });
    update(updateProgressCallback, successCallback, failureCallback, user, contactsWithPhoneNumbers);
}

function update(updateProgressCallback, successCallback, failureCallback, user, contacts) {
    updateProgressCallback(0, contacts.length);
    $.ajax({
        url: "/user/" + user + "/contacts",
        method: "DELETE"
    }).done(function() {
        add(updateProgressCallback, successCallback, failureCallback, user, contacts, 1, contacts.length);
    }).error(function(jqXHR, textStatus, errorThrown) {
        failureCallback();
    });
}

function add(updateProgressCallback, successCallback, failureCallback, user, contacts, progress, total) {
    if (contacts.length != 0) {
        var contact = contacts[0];
        console.log(contact);
        $.ajax({
            url: "/user/" + user + "/contacts",
            method: "POST",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(contact)
        }).done(function(response) {
            updateProgressCallback(progress, total);
            $("#results").append("<tr><td><pre>" + JSON.stringify(response, null, 2) + "</pre></td></tr>");
            contacts.shift();
            add(updateProgressCallback, successCallback, failureCallback, user, contacts, progress + 1, total);
        }).error(function(jqXHR, textStatus, errorThrown) {
            failureCallback();
        });
    }
    else {
        successCallback();
    }
}

/*
function handleAuthClick() {
    console.log("handleAuthClick");
    gapi.auth.authorize ( { client_id: clientId, scope: scopes, immediate: false }, handleAuthResult );
    return false;
}
    */