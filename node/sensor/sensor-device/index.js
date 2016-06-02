var host = process.env.AMQP_HOST || 'localhost';
var deviceId = process.env.DEVICE_ID || 'device-' + Math.random();

var os = require('os');
var ifaces = os.networkInterfaces();
Object.keys(ifaces).forEach(function (ifname) {
    ifaces[ifname].forEach(function (iface) {
        if ('IPv4' !== iface.family || iface.internal !== false) {
           // skip over internal (i.e. 127.0.0.1) and non-ipv4 addresses
            return;
        }
        if(ifname.startsWith('wlan') || ifname.startsWith('eth')) {
            var container = require('rhea');
            container.on('connection_open', function (context) {
                context.connection.open_sender('device-management/register');
            });
            container.once('sendable', function (context) {
                var device = {deviceId: deviceId, properties: {ip: iface.address}};
                console.log('Sending device registration update: ' + JSON.stringify(device))
                context.sender.send({body: device});
            });
            container.connect({'host': host, 'port': 5672});
        }
    });
});