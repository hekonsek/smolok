package smolok.cmd

import org.apache.commons.lang3.ArrayUtils

abstract class BaseCommand implements Command {

    private final String[] commandPrefix

    BaseCommand(String[] commandPrefix) {
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

    @Override
    boolean helpRequested(String... command) {
        hasOption(command, 'help')
    }

    @Override
    String help() {
        'No help available for the command.'
    }

    protected String option(String[] command, String optionName, String defaultValue) {
        def optionFound = command.find{ it.startsWith("--${optionName}=") }
        optionFound != null ? optionFound.replaceFirst(/--${optionName}=/, '') : defaultValue
    }

    protected Optional<String> option(String[] command, String optionName) {
        Optional.ofNullable(option(command, optionName, null))
    }

    protected Boolean hasOption(String[] command, String optionName) {
        command.find{ it.startsWith("--${optionName}") } ? true : false
    }

    protected String[] removeOption(String[] commands, String optionName) {
        commands.toList().findAll { !it.startsWith("--${optionName}") } as String[]
    }

    protected String[] putOptionAt(String[] commands, int index, String option) {
        ArrayUtils.add(commands, index, option)
    }

}