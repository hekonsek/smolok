var RaspiCam = require("raspicam");
var mkdirp = require('mkdirp');

var imagesDirectory = '/var/smolok/camera'

mkdirp(imagesDirectory, function (err) {

    if (err) console.error(err);

    var camera = new RaspiCam({
        mode: "timelapse",
        output: imagesDirectory + "/current.jpg",
        encoding: "jpg",
        timelapse: 1000,
        timeout: 120000
    });

    camera.on("start", function( err, timestamp ){
        console.log("timelapse started at " + timestamp);
    });

    camera.on("read", function( err, timestamp, filename ){
        console.log("timelapse image captured with filename: " + filename);
    });

    camera.on("exit", function( timestamp ){
        console.log("timelapse child process has exited");
    });

    camera.on("stop", function( err, timestamp ){
        console.log("timelapse child process has been stopped at " + timestamp);
    });

    camera.start();

});