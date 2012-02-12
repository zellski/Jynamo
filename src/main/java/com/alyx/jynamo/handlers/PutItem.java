//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo.handlers;

import com.alyx.jynamo.CmdHandler;
import com.alyx.jynamo.JynParser;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.DeleteTableRequest;
import com.amazonaws.services.dynamodb.model.DeleteTableResult;
import joptsimple.OptionSpec;

public class PutItem extends CmdHandler {
    public PutItem (AmazonDynamoDBClient client) {
        super(client);
    }

    @Override
    public String handlesCommand () {
        return "delete";
    }

    @Override
    public void execute (JynParser parser, String[] input, OptionSpec<String> table, boolean justTypes) {
        if (justTypes) {
            return;
        }

        JynParser.JynOptions opts = parser.jynParse(input);

        String tableName = opts.valueOf(table);
        DeleteTableRequest request = new DeleteTableRequest()
                .withTableName(tableName);
        DeleteTableResult result = _client.deleteTable(request);

        System.out.println("DELETE TABLE: " + result);
    }
}
