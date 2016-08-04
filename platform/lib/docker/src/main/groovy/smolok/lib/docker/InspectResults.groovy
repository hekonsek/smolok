package smolok.lib.docker

import com.google.common.collect.ImmutableMap

class InspectResults {

    private final Map<String, Object> results

    InspectResults(Map<String, Object> results) {
        this.results = new HashMap<>(results)
    }

    Map<String, Object> results() {
        new HashMap<>(results)
    }

    Map<String, String> environment() {
        results.Config.Env.inject([:]){ envs, env ->
            def envParts = env.split('=')
            envs[envParts[0]] = envParts[1]
            envs
        }
    }

}
