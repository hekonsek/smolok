package net.smolok.cmd.core

import com.google.common.cache.Cache

import java.util.concurrent.Callable

import static com.google.common.cache.CacheBuilder.newBuilder
import static java.util.concurrent.TimeUnit.MINUTES

class GuavaCacheOutputSink implements OutputSink {

    // Internal collaborators

    private final Cache<String, List<String>> outputCache = newBuilder().
            expireAfterAccess(30, MINUTES).expireAfterWrite(30, MINUTES).build()

    private final Cache<String, Boolean> doneMarkers = newBuilder().
            expireAfterAccess(30, MINUTES).expireAfterWrite(30, MINUTES).build()

    // Sink operations

    @Override
    List<String> output(String commandId, int offset) {
        def output = outputCache.get(commandId) {[]}

        if(offset == output.size()) {
            if(doneMarkers.get(commandId) {false}) {
                return null
            } else {
                return []
            }
        }

        output[offset..output.size() - 1]
    }

    @Override
    def markAsDone(String commandId) {
        doneMarkers.put(commandId, true)
    }

    @Override
    boolean isDone(String commandId) {
        doneMarkers.get(commandId, new Callable<Boolean>() {
            @Override
            Boolean call() throws Exception {
                false
            }
        })
    }

    @Override
    def reset() {
        outputCache.cleanUp()
    }

    @Override
    void out(String commandId, String outputLine) {
        outputCache.get(commandId) {[]} << outputLine
    }

}