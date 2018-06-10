$(function() {
    $(".update-contacts").click(function() {
        $.ajax({
            url: '@routes.Home.updateContacts()',
            type: 'post',
            success: function () {
                window.location.reload(true);
            }
        });
    });

});