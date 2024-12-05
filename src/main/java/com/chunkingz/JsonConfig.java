package com.chunkingz;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonConfigReader {
    private static class Config {
        public String baseURL;
        public List<String> keywords;
        public String location;
        public int numberOfJobsPerPage;
        public List<String> avoidJobTitles;
        public List<String> avoidCompanyNames;

        private static final Config config;

        static {
            try {
                var mapper = new ObjectMapper();
                config = mapper.readValue(new File("config.json"), Config.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load JSON config file.", e);
            }
        }

        public static String getBaseURL() {
            return config.baseURL;
        }

        public List<String> getKeywords() {
            return config.keywords;
        }

        public String getLocation() {
            return config.location;
        }

        public int getNumberOfJobsPerPage() {
            return config.numberOfJobsPerPage;
        }

        public List<String> getAvoidJobTitles() {
            return config.avoidJobTitles;
        }

        public List<String> getAvoidCompanyNames() {
            return config.avoidCompanyNames;
        }
    }
    
}
