self.addEventListener('install', function(event) {
    // Perform install steps
    console.log("I am installing myself.")
});

self.addEventListener('push', function(e) {
    var call = e.data.json();
    var contact = call.contact;
    var phoneNumber = call.phoneNumber;
    var messageParts = [];

    if (contact) {
        messageParts.push(contact.name + " (" + contact.phoneType + ")");
    }
    if (phoneNumber) {
        messageParts.push(phoneNumber.formattedNumber);
        var address = phoneNumber.city ? phoneNumber.city + ", " : "";
        messageParts.push(address + phoneNumber.countries[0]);
    }
    var body = messageParts.join("\n");

    var useAvatar = contact && contact.avatarUrl;
    var iconUrl = useAvatar ? contact.avatarUrl : "/caller-id/assets/images/whocalled.png";

    var options = {
        body: body,
        icon: iconUrl,
        vibrate: [100, 50, 100],
        data: {
            dateOfArrival: Date.now(),
            primaryKey: '2'
        },
        actions: [
            {action: 'explore', title: 'Explore this new world',
                icon: 'images/checkmark.png'},
            {action: 'close', title: 'Close',
                icon: 'images/xmark.png'}
        ]
    };
    e.waitUntil(
        self.registration.showNotification('Landline call received', options)
    );
});