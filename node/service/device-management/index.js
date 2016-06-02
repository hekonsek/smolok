var mkdirp = require('mkdirp');
var container = require('rhea');
var fs = require('fs');

var host = process.env.EVENT_BUS_SERVICE_HOST || 'localhost';
var devicesDirectory = process.env.HOME + '/.smolok/devices';

mkdirp(devicesDirectory, function (err) {
    if(err) {
        return console.log(err)
    }
    console.log(`Using ${devicesDirectory} as device directory.`);
    container.on('message', function (context) {
        var from = context.receiver.remote.attach.source.address;
        if (from === 'device-management/register') {
            var device = context.message.body;
            fs.writeFile(devicesDirectory + '/' + device.deviceId, JSON.stringify(device), function (err) {
                if (err) {
                    return console.log(err);
                }
                console.log(`Device ${device.deviceId} registered.`);
            });
        } else if (from === 'device-management/get') {
            fs.readFile(devicesDirectory + '/' + device.deviceId, 'utf8', function (err, data) {
                if (err) {
                    return console.log(err);
                }
                var device = JSON.parse(data);
                container.once('sendable', function (context) {
                    context.sender.send({body: device});
                });
                context.connection.open_sender(context.message.replyTo);
            });
        }
    });
});

var connection = container.connect({'host': host, 'port': 5672});
connection.attach_receiver('device-management/register');
connection.attach_receiver('device-management/get');