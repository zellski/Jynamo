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
import com.alyx.jynamo.handlers.GetItem;
import com.alyx.jynamo.handlers.ListTables;
import com.alyx.jynamo.handlers.PutItem;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import javax.annotation.Nullable;

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

        for (CmdHandler handler : _handlers) {
            handler.configure(parser);
        }

        JynParser.JynOptions options;
        try {
            options = parser.jynParse(input);

        } catch (OptionException oe) {
            System.err.println("Syntax error: " + oe.getMessage());
            return printHelpAndExit(parser);
        }

        if (options.has("help")) {
            return printHelpAndExit(parser);
        }
        List<String> args = options.nonOptionArguments();
        if (args.size() == 0) {
            System.err.println("You must provide a single-word command:");
            return printHelpAndExit(parser);
        }

        String cmd = args.get(0);
        args = args.subList(1, args.size());

        boolean handled = false;
        for (CmdHandler handler : _handlers) {
            if (handler.handlesCommand().equals(cmd)) {
                // send in options and the "rest" of the arguments
                handler.execute(options, args);
                handled = true;
                break;
            }
        }

        if (!handled) {
            System.err.println("Unknown command: " + cmd + "\n");
            return printHelpAndExit(parser);
        }
        return 0;
    }

    protected int printHelpAndExit (OptionParser parser) {
        try {
            System.err.println("Available commands: ");
            for (CmdHandler handler : _handlers) {
                System.err.println(handler.help());
            }
            System.err.println("==================================================================================");
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
        _cmdClasses.add(PutItem.class);
        _cmdClasses.add(GetItem.class);
    }
}
