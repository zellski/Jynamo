//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo;

import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import joptsimple.OptionSpec;

public abstract class CmdHandler {
    protected CmdHandler (AmazonDynamoDBClient client) {
        _client = client;
    }

    abstract public String handlesCommand ();

    abstract public void execute (JynParser parser, String[] input, OptionSpec<String> table, boolean justTypes);

    protected final AmazonDynamoDBClient _client;
}
