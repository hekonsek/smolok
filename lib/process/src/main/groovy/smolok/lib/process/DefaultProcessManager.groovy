package smolok.lib.process

import static org.apache.commons.io.IOUtils.readLines

class DefaultProcessManager extends ExecutorBasedProcessManager {

    @Override
    List<String> execute(String... command) {
        if(log.isDebugEnabled()) {
            log.debug('About to execute command:', command.toList())
        }

        try {
            def process = new ProcessBuilder().redirectErrorStream(true).command(command).start()
            readLines(process.getInputStream())
        } catch (IOException e) {
            throw new ProcessExecutionException(e)
        }
    }

}