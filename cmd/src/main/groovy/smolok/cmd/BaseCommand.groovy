package smolok.cmd

abstract class BaseCommand implements Command {

    private final String[] commandPrefix

    BaseCommand(String... commandPrefix) {
        this.commandPrefix = commandPrefix
    }

    @Override
    boolean supports(String... command) {
        if(commandPrefix.length > command.length) {
            return false
        }

        for(int i = 0; i < commandPrefix.length; i++) {
            if(commandPrefix[i] != command[i]) {
                return false
            }
        }

        true
    }

    protected String option(String[] command, String optionName, String defaultValue) {
        def optionFound = command.find{ it.startsWith("--${optionName}=") }
        optionFound != null ? optionFound.replaceFirst(/--${optionName}=/, '') : defaultValue
    }

    protected Optional<String> option(String[] command, String optionName) {
        Optional.ofNullable(option(command, optionName, null))
    }

}