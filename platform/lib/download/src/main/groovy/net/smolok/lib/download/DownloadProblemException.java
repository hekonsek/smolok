package net.smolok.lib.download;

public class DownloadProblemException extends RuntimeException {

    public DownloadProblemException(String filename, Throwable cause) {
        super("Cannot download file: " + filename, cause);
    }

}
