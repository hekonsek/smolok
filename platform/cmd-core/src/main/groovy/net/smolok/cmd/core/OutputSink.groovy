package net.smolok.cmd.core

interface OutputSink {

    void out(String commandId, String outputLine)

    List<String> output(String commandId, int offset)

    def markAsDone(String commandId)

    boolean isDone(String commandId)

    def reset()
}