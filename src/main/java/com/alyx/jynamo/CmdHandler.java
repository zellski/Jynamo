//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo;

import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import joptsimple.OptionSpec;

import java.util.List;

public abstract class CmdHandler {
    protected CmdHandler (AmazonDynamoDBClient client) {
        _client = client;
    }

    abstract public String handlesCommand ();

    abstract public String help ();

    public void configure(JynParser parser) {
        // no default configuration
    }

    abstract public void execute (JynParser.JynOptions opts, List<String> args);

    protected final AmazonDynamoDBClient _client;
}
