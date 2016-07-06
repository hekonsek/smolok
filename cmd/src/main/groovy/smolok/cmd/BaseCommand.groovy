package smolok.cmd

abstract class BaseCommand implements Command {

    protected String option(String[] command, String optionName, String defaultValue) {
        def optionFound = command.find{ it.startsWith("--${optionName}=") }
        optionFound != null ? optionFound.replaceFirst(/--${optionName}=/, '') : defaultValue
    }

    protected Optional<String> option(String[] command, String optionName) {
        Optional.ofNullable(option(command, optionName, null))
    }

}