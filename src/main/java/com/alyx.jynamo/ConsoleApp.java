//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

import static com.alyx.Log.log;

public class ConsoleApp
{
    public static final String CREDS_PROP = "creds.properties";

    public static void main (String[] args) {
        try {
            InputStream credStream = ConsoleApp.class.getResourceAsStream(CREDS_PROP);
            if (credStream == null) {
                credStream = ConsoleApp.class.getResourceAsStream((CREDS_PROP + ".dist");
                if (credStream == null) {
                    log.error("Cannot find " + CREDS_PROP + " to read.");
                    System.exit(1);
                }
            }

            AWSCredentials credentials = new PropertiesCredentials(credStream);
            ConsoleApp app = new ConsoleApp(credentials);
            app.run();
            
        } catch (IOException ioe) {
            log.error("Failed to construct AWSCredentials", ioe);
            System.exit(1);
        }
        System.exit(0);
    }

    public ConsoleApp (AWSCredentials credentials) {
        _credentials = credentials;
    }

    public void run () {
        log.info("Hello!");
    }

    final protected AWSCredentials _credentials;
}
