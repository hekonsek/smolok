package smolok.cmd

class CommandDispatcher {

    private final OutputSink outputSink

    private final List<Command> commands

    CommandDispatcher(OutputSink outputSink, List<Command> commands) {
        this.outputSink = outputSink
        this.commands = commands
    }

    void handleCommand(String... command) {
        commands.find{ it.supports(command) }.handle(outputSink, command)
    }

}
