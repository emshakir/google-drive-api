$(function() {
    var userName = "shakir";
    $('#btn-drive-in').click(function () {
        $.ajax({
            url: 'google/signin/',
            method: 'POST',
            data: {
                userName: userName
            }
        }).done(function (response) {
            window.open(response,'_blank');
        });
    })
})