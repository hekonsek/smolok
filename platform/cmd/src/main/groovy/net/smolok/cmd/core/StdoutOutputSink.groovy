package net.smolok.cmd.core

import net.smolok.cmd.core.OutputSink

class StdoutOutputSink implements OutputSink {

    @Override
    void out(String outputLine) {
        println outputLine
    }

}
