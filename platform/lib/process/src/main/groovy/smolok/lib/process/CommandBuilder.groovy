package smolok.lib.process

class CommandBuilder {

    private final List<String> command

    File workingDirectory

    boolean sudo = false

    String sudoPassword

    CommandBuilder(List<String> command) {
        this.command = command
    }

    CommandBuilder(String... command) {
        this.command = command.toList()
    }

    Command build() {
        new Command(command, workingDirectory, sudo, sudoPassword)
    }

    CommandBuilder workingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory
        this
    }

    CommandBuilder sudo(boolean sudo) {
        this.sudo = sudo
        this
    }

    CommandBuilder sudoPassword(String sudoPassword) {
        this.sudoPassword = sudoPassword
        this
    }

}
