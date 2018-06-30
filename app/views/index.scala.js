@(publicKey: String)

$(function() {

    // Add the action for updating contacts.
    $(".update-contacts").click(function() {
        $.ajax({
            url: '@routes.HomeController.updateContacts()',
            type: 'post',
            success: function () {
                window.location.reload(true);
            }
        });
    });

    Notification.requestPermission(function(status) {
        console.log('Notification permission status:', status);
    });

    if ('serviceWorker' in navigator && 'PushManager' in window) {
        console.log('Service Worker and Push is supported');

        navigator.serviceWorker.register('@routes.HomeController.serviceWorker()')
            .then(function(swReg) {
                console.log('Service Worker is registered', swReg);
                return subscribeToPushNotifications(swReg);
            })
            .catch(function(error) {
                console.error('Service Worker Error', error);
            });
    } else {
        console.warn('Push messaging is not supported');
        pushButton.textContent = 'Push Not Supported';
    }

    // Attempt to register a service worker.

    function subscribeToPushNotifications(reg) {
        return reg.pushManager.getSubscription().then(function(sub) {
            if (sub === null) {
                console.log('Not subscribed to push service!');
                return subscribe(reg);
            } else {
                // We have a subscription, update the database
                console.log('Subscription object: ', sub);
            }
        });
    }

    function subscribe(reg) {
        return reg.pushManager.subscribe({
            userVisibleOnly: true,
            applicationServerKey: urlBase64ToUint8Array('@publicKey')
        }).then(function(sub) {
            const subscriptionWithUserAgent = JSON.parse(JSON.stringify(sub));
            subscriptionWithUserAgent.user = {
                    userAgent: navigator.userAgent
            };
            const body = JSON.stringify(subscriptionWithUserAgent);
            console.log("Sending subscription " + body);
            $.ajax({
                url: '@routes.HomeController.subscribe()',
                type: 'post',
                data: body,
                success: function () {
                    console.log("Subscription sent successfully.")
                }
            });
            console.log('Endpoint URL: ', sub.endpoint);
        }).catch(function(e) {
            if (Notification.permission === 'denied') {
                console.warn('Permission for notifications was denied');
            } else {
                console.error('Unable to subscribe to push', e);
            }
        });
    }

    function urlBase64ToUint8Array(base64String) {
        const padding = '='.repeat((4 - base64String.length % 4) % 4);
        const base64 = (base64String + padding)
            .replace(/\-/g, '+')
            .replace(/_/g, '/');

        const rawData = window.atob(base64);
        const outputArray = new Uint8Array(rawData.length);

        for (let i = 0; i < rawData.length; ++i) {
            outputArray[i] = rawData.charCodeAt(i);
        }
        return outputArray;
    }
});
