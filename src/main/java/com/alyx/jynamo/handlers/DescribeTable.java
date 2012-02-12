//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo.handlers;

import com.alyx.jynamo.CmdHandler;
import com.alyx.jynamo.JynParser;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.DeleteTableRequest;
import com.amazonaws.services.dynamodb.model.DeleteTableResult;
import com.amazonaws.services.dynamodb.model.DescribeTableRequest;
import com.amazonaws.services.dynamodb.model.DescribeTableResult;
import joptsimple.OptionSpec;

import java.util.List;

import static com.alyx.Log.log;

public class DescribeTable extends CmdHandler {
    public DescribeTable (AmazonDynamoDBClient client) {
        super(client);
    }

    @Override
    public String handlesCommand () {
        return "describe";
    }

    @Override
    public String help () {
        return (" describe <table>\n" +
                "   Prints a JSON summary of a table; its key, configured throughput, current size, etc.");
    }

    @Override
    public void execute (JynParser.JynOptions opts, List<String> args) {
        if (args.size() != 1) {
            System.err.println("Usage: " + help());
            return;
        }
        String tableName = args.get(0);
        DescribeTableRequest request = new DescribeTableRequest()
                .withTableName(tableName);
        DescribeTableResult result = _client.describeTable(request);

        System.out.println("DESCRIBE TABLE: " + result);
    }
}
