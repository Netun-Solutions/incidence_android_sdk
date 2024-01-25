package es.incidence.library.config;

public class IncidenceLibraryConfig {
    final private String apikey;
    final private IncidenceEnvironment environment;

    private IncidenceLibraryConfig(String apikeyLoc, IncidenceEnvironment environment) {
        this.apikey = apikeyLoc;
        this.environment = environment;
    }

    public String getApikey() {
        return apikey;
    }

    public IncidenceEnvironment getEnvironment() {
        return environment;
    }

    public static class Builder {
        private String apikey;
        private IncidenceEnvironment environment = IncidenceEnvironment.TEST;

        public Builder setApikey(String apikey) {
            this.apikey = apikey;
            return this;
        }

        public Builder setEnvironment(IncidenceEnvironment environment) {
            this.environment = environment;
            return this;
        }

        public IncidenceLibraryConfig createIncidenceLibraryConfig() {
            return new IncidenceLibraryConfig(apikey, environment);
        }
    }
}
