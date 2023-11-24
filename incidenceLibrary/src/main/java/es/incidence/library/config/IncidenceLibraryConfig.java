package es.incidence.library.config;

public class IncidenceLibraryConfig {
    final private String apikey;
    final private Environment environment;

    private IncidenceLibraryConfig(String apikeyLoc, Environment environment) {
        this.apikey = apikeyLoc;
        this.environment = environment;
    }

    public String getApikey() {
        return apikey;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public static class Builder {
        private String apikey;
        private Environment environment = Environment.TEST;

        public Builder setApikey(String apikey) {
            this.apikey = apikey;
            return this;
        }

        public Builder setEnvironment(Environment environment) {
            this.environment = environment;
            return this;
        }

        public IncidenceLibraryConfig createIncidenceLibraryConfig() {
            return new IncidenceLibraryConfig(apikey, environment);
        }
    }
}
