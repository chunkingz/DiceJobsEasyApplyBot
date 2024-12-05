package com.chunkingz;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonConfig {

    private static class Config {
        public String baseURL;
        public String[] keywords;
        public String location;
        public int numberOfJobsPerPage;
        public String[] avoidJobTitles;
        public String[] avoidCompanyNames;
    }
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

        public static String[] getKeywords() {
            return config.keywords;
        }

        public static String getLocation() {
            return config.location;
        }

        public static int getNumberOfJobsPerPage() {
            return config.numberOfJobsPerPage;
        }

        public static String[] getAvoidJobTitles() {
            return config.avoidJobTitles;
        }

        public static String[] getAvoidCompanyNames() {
            return config.avoidCompanyNames;
        }
}
