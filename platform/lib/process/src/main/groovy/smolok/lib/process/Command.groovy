package smolok.lib.process

import smolok.lib.common.Properties

class Command {

    private final List<String> command

    private final File workingDirectory

    private final boolean sudo

    private final String sudoPassword

    Command(List<String> command, File workingDirectory, boolean sudo, String sudoPassword) {
        this.command = command
        this.workingDirectory = workingDirectory
        this.sudo = sudo
        this.sudoPassword = sudoPassword
    }

    static Command cmd(boolean sudo, String... command) {
        if(command.length == 1 && command[0] =~ /\s+/) {
            cmd(command[0].split(/\s+/))
        } else {
            new Command(command.toList(), null, sudo, null)
        }
    }

    static Command cmd(String... command) {
        cmd(false, command)
    }

    static Command sudo(String... command) {
        cmd(true, command)
    }

    // Members access

    List<String> command() {
        return command
    }

    File workingDirectory() {
        workingDirectory
    }

    boolean sudo() {
        sudo
    }

    // Member resolvers

    String sudoPassword() {
        if(sudoPassword != null) {
            return sudoPassword
        }
        Properties.stringProperty('SUDO_PASSWORD')
    }

}