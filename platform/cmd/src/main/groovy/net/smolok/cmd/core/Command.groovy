package net.smolok.cmd.core

import groovy.transform.CompileStatic

/**
 * Provides set of operations required to by implemented by CMD commands.
 */
@CompileStatic
interface Command {

    /**
     * Indicates whether given command implementation can handle string entered in the command line.
     *
     * @param command list of strings representing command typed into CMD.
     * @return whether given command implementation can handle entered command string.
     */
    boolean supports(String... command)

    void handle(OutputSink outputSink, String... command)

    boolean helpRequested(String... command)

    String help()

}