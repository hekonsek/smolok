package smolok.cmd

class StdoutOutputSink implements OutputSink {

    @Override
    void out(String outputLine) {
        println outputLine
    }

}
