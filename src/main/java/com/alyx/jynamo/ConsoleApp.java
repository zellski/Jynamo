//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.List;

import com.alyx.jynamo.handlers.CreateTable;
import com.alyx.jynamo.handlers.DeleteTable;
import com.alyx.jynamo.handlers.DescribeTable;
import com.alyx.jynamo.handlers.ListTables;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.google.common.collect.Lists;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import static com.alyx.Log.log;

public class ConsoleApp
{
    public static final String CREDS_PROP = "creds.properties";

    public static void main (String[] args) {
        try {
            InputStream credStream = ConsoleApp.class.getResourceAsStream(CREDS_PROP);
            if (credStream == null) {
                credStream = ConsoleApp.class.getResourceAsStream(CREDS_PROP + ".dist");
                if (credStream == null) {
                    log.error("Cannot find " + CREDS_PROP + " to read.");
                    System.exit(1);
                }
            }

            AWSCredentials credentials = new PropertiesCredentials(credStream);
            ConsoleApp app = new ConsoleApp(credentials);
            System.exit(app.parseAndExecute(args));

        } catch (IOException ioe) {
            log.error("Failed to construct AWSCredentials", ioe);
            System.exit(1);
        }
    }

    public ConsoleApp (AWSCredentials credentials) {
        _client = new AmazonDynamoDBClient(credentials);

        for (Class<? extends CmdHandler> baseClass : _cmdClasses) {
            try {
                Constructor<? extends CmdHandler> ctor = baseClass.getConstructor(AmazonDynamoDBClient.class);
                _handlers.add(ctor.newInstance(_client));
            } catch (Exception e) {
                log.warning("Failed to instantiate command handler: " + baseClass);
            }
        }
    }

    public int parseAndExecute (String[] input)
            throws IOException {
        JynParser parser = new JynParser();
        parser.noArgOpt("help", "Prints this help.");
        parser.noArgOpt("dry-run", "Do not actually modify the DB in any way.");

        // the one option which is always required, no matter what
        OptionSpec<String> table = parser.stringOpt("table", "The DynamoDB table on which to operate.", null);

        for (CmdHandler handler : _handlers) {
            handler.execute(parser, null, null, true);
        }

        OptionSet options;
        try {
            options = parser.parse(input);

        } catch (OptionException oe) {
            System.err.println("Syntax error: " + oe.getMessage());
            return printHelpAndExit(parser);
        }

        if (options.has("help")) {
            return printHelpAndExit(parser);
        }
        List<String> args = options.nonOptionArguments();
        if (args.size() != 1) {
            System.err.println("All commands are single words only. Any other arguments must be provided as options:");
            return printHelpAndExit(parser);
        }

        String cmd = args.get(0);
        boolean handled = false;
        for (CmdHandler handler : _handlers) {
            if (handler.handlesCommand().equals(cmd)) {
                handler.execute(parser, input, table, false);
                handled = true;
                break;
            }
        }

        if (!handled) {
            System.err.println("Unknown command: " + args.get(0) + "\n");
            return printHelpAndExit(parser);
        }
        return 0;
    }

    protected static int printHelpAndExit (OptionParser parser) {
        try {
            parser.printHelpOn(System.err);
        } catch (IOException e) {
            log.error("Things are very bad.");
        }
        return 1;
    }

    protected final AmazonDynamoDBClient _client;

    protected List<CmdHandler> _handlers = Lists.newArrayList();

    protected static List<Class<? extends CmdHandler>> _cmdClasses = Lists.newArrayList();

    static {
        _cmdClasses.add(CreateTable.class);
        _cmdClasses.add(DeleteTable.class);
        _cmdClasses.add(DescribeTable.class);
        _cmdClasses.add(ListTables.class);
    }
}
