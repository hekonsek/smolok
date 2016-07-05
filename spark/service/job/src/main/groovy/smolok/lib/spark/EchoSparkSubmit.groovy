package smolok.lib.spark

class EchoSparkSubmit implements SparkSubmit {

    @Override
    SparkSubmitResult submit(SparkSubmitCommand command) {
        new SparkSubmitResult([command.path], [command.path])
    }

}
