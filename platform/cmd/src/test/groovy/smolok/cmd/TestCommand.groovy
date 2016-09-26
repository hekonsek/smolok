package smolok.cmd

class TestCommand extends BaseCommand {

    TestCommand() {
        super('this', 'is', 'my', 'command')
    }

    @Override
    void handle(OutputSink outputSink, String... command) {

    }

    @Override
    String help() {
        '''Use this command like that:

foo bar baz'''
    }

}
