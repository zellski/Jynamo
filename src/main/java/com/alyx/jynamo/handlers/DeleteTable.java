//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo.handlers;

import com.alyx.jynamo.CmdHandler;
import com.alyx.jynamo.JynParser;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.CreateTableRequest;
import com.amazonaws.services.dynamodb.model.CreateTableResult;
import com.amazonaws.services.dynamodb.model.DeleteTableRequest;
import com.amazonaws.services.dynamodb.model.DeleteTableResult;
import com.amazonaws.services.dynamodb.model.DescribeTableRequest;
import com.amazonaws.services.dynamodb.model.DescribeTableResult;
import com.amazonaws.services.dynamodb.model.KeySchema;
import com.amazonaws.services.dynamodb.model.KeySchemaElement;
import com.amazonaws.services.dynamodb.model.ProvisionedThroughput;
import joptsimple.OptionSpec;

import java.util.List;

import static com.alyx.Log.log;

public class DeleteTable extends CmdHandler {
    public DeleteTable (AmazonDynamoDBClient client) {
        super(client);
    }

    @Override
    public String handlesCommand () {
        return "delete";
    }

    @Override
    public String help () {
        return (" delete <tableName>\n" +
                "   Deletes an existing table from the DB. Be careful! There is no confirmation prompt!");
    }

    @Override
    public void execute (JynParser.JynOptions opts, List<String> args) {
        if (args.size() != 1) {
            System.err.println("Usage: " + help());
            return;
        }
        String tableName = args.get(0);
        DeleteTableRequest request = new DeleteTableRequest()
                .withTableName(tableName);
        DeleteTableResult result = _client.deleteTable(request);

        System.out.println("DELETE TABLE: " + result);
    }
}
