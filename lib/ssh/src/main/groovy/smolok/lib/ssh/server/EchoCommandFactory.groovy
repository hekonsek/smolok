package smolok.lib.ssh.server

import org.apache.sshd.server.Command
import org.apache.sshd.server.CommandFactory
import org.apache.sshd.server.Environment
import org.apache.sshd.server.ExitCallback

class EchoCommandFactory implements CommandFactory {

    @Override
    Command createCommand(String command) {
        return new EchoCommand(command)
    }

    static class EchoCommand implements Command {

        private final String command

        private OutputStream outputStream

        private ExitCallback exitCallback

        EchoCommand(String command) {
            this.command = command
        }

        @Override
        void setInputStream(InputStream inputStream) {
        }

        @Override
        void setOutputStream(OutputStream out) {
            this.outputStream = out
        }

        @Override
        void setErrorStream(OutputStream err) {
        }

        @Override
        void setExitCallback(ExitCallback callback) {
            this.exitCallback = callback
        }

        @Override
        void start(Environment env) throws IOException {
            outputStream.write(command.bytes)
            outputStream.flush()
            exitCallback.onExit(0)
        }

        @Override
        void destroy() throws Exception {

        }
    }

}
