package net.smolok.cmd.core

import com.google.common.cache.Cache

import java.util.concurrent.Callable

import static com.google.common.cache.CacheBuilder.newBuilder

class GuavaCacheOutputSink implements OutputSink {

    private final Cache<String, List<String>> outputCache = newBuilder().build()

    private final Cache<String, Boolean> doneMarkers = newBuilder().build()

    @Override
    List<String> output(String commandId, int offset) {
        def output = outputCache.get(commandId, new Callable<List<String>>() {
            @Override
            List<String> call() throws Exception {
                []
            }
        })

        if(offset == output.size()) {
            if(doneMarkers.get(commandId, new Callable<Boolean>() {
                @Override
                Boolean call() throws Exception {
                    false
                }
            })) {
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
        outputCache.get(commandId, new Callable<List<String>>() {
            @Override
            List<String> call() throws Exception {
                []
            }
        }) << outputLine
    }

}