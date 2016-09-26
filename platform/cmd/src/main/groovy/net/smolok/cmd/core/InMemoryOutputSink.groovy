package net.smolok.cmd.core

import net.smolok.cmd.core.OutputSink

class InMemoryOutputSink implements OutputSink {

    private final List<String> output = []

    @Override
    void out(String outputLine) {
        output << outputLine
    }

    void reset() {
        output.clear()
    }

    List<String> output() {
        output
    }

}
