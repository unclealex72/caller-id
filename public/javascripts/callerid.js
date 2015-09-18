function auth() {
    $("#update").prop('disabled', true);
    $("#success").hide();
    $("#failure").hide();
    var config = {
        'client_id': '720703205602-qbl6kpbobbvapentgos5b6k1e8sk0nm2.apps.googleusercontent.com',
        'scope': 'https://www.google.com/m8/feeds'
    };
    gapi.auth.authorize(config, function() {
        fetch(gapi.auth.getToken());
    });
}
function fetch(token) {
    $.ajax({
        url: '//www.google.com/m8/feeds/contacts/default/full?alt=json&max-results=1000',
        dataType: 'jsonp',
        data: token
    }).done(function(data) {
        var contacts = parseContacts(data);
        updateContacts(contacts);
    });
}
function parseContacts(data) {
    var username = data.feed.author[0].email["$t"];
    var contacts = _.chain(data.feed.entry)
        .map(function(entry) {
            return {
                title: entry["title"],
                phoneNumbers: entry["gd$phoneNumber"]
            }
        })
        .filter(function(entry) { return entry.phoneNumbers; })
        .map(function(entry) {
            var name = entry["title"]["$t"];
            var phoneNumbers = _.map(entry["phoneNumbers"], function(phoneNumber) {
                return {
                    number : phoneNumber["$t"],
                    type: phoneNumber["rel"].replace("http://schemas.google.com/g/2005#", "")
                };
            });
            return { name: name, phoneNumbers: phoneNumbers };
        })
        .value();
    return {
        email: username,
        contacts: contacts
    };
}
function updateContacts(contacts) {
    $.ajax({
        url: 'contacts',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(contacts)
    }).done(function(data) {
        $("#update").prop('disabled', false);
        $("#success").show();
    }).fail(function(jqXHR, textStatus, errorThrown) {
        $("#update").prop('disabled', false);
        $("#failure").show();
    });
}

$(function() {

    $("#update").on("click", auth);
});