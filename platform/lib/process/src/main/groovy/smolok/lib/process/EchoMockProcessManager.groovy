package smolok.lib.process

import com.google.common.collect.ImmutableList;

/**
 * Mock process manager returning command input as an output.
 */
class EchoMockProcessManager extends ExecutorBasedProcessManager {

    @Override
    List<String> execute(String... command) {
        ImmutableList.copyOf(command)
    }

}
