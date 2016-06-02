var spawn = require('child_process').spawn;
var container = require('rhea');

var host = process.env.EVENT_BUS_SERVICE_HOST || 'localhost';

container.on('message', function (context) {
    var service = context.message.body;
    var dockerCommand = service.uri;
    dockerCommand = dockerCommand.replace('docker:', '');
    var segments = dockerCommand.split(' ');
    var ls = spawn('docker', ['run', '--net=host', '--privileged', '-d'].concat(segments));

    ls.stdout.on('data', (data) => {
        console.log(`stdout: ${data}`);
        var fs = require('fs');

        fs.appendFile('/etc/rc.local', ['docker', 'run', '--net=host', '--privileged', '-d'].concat(segments).join(' '), function (err) {
            if (err) {
                return console.log(err);
            }

            console.log('Added to /etc/rc.local');
        });
    });


    ls.stderr.on('data', (data) => {
        console.log(`stderr: ${data}`);
    });
});

var connection = container.connect({'host': host, 'port': 5672});
connection.attach_receiver('loader/load');