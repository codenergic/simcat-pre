(function($, $q, EventBus) {
    'use strict';

    var input = $('textarea#input'),
        output = $('textarea#response'),
        inputToken = $('input#token'),
        submit = $('button#submit'),
        connecting = $('#connecting'),
        container = $('#container'),
        generate = $('button#generate'),
        connect = $('button#connect');

    submit.attr('enabled', false);

    container.hide();
    submit.hide();

    var eb = new EventBus('/eventbus');
    eb.onopen = function() {
        // send a message
        container.show();
        connecting.hide();
    }

    submit.click(onSubmit);
    connect.click(connectChannel);
    generate.click(generateToken);
    inputToken.keyup(function() {
        submit.hide();
    });

    function onSubmit() {
        eb.send('chat.message', input.val(), { token: inputToken.val() });
    }

    function generateToken() {
        eb.send('chat.token', {}, function(error, msg) {
            if (error) {
                alert('Failed getting token');
                return;
            }

            inputToken.val(msg.body);
            listenMsg();
        });
    }

    function connectChannel() {
        if (!inputToken.val() || inputToken.val() === '') {
            alert('Channel cannot be empty');
            return;
        }

        listenMsg();
    }

    function listenMsg() {
        var token = inputToken.val();
        var subscription = 'chat.message.' + token;

        if (subscription) {
            if (eb.handlers[subscription]) {
                return;
            }
        }

        console.log('Registering handler ' + subscription);
        eb.registerHandler(subscription, function(error, msg) {
            if (output.text() !== '') {
                output.append('\n');
            }
            output.append(msg.body);
        });
        submit.show();
    }
})(jQuery, Promise.noConflict(), EventBus);
