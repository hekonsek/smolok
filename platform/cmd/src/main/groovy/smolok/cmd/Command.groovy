package smolok.cmd

import groovy.transform.CompileStatic

@CompileStatic
interface Command {

    boolean supports(String... command)

    void handle(OutputSink outputSink, String... command)

}