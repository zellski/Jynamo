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
    public void execute (JynParser parser, String[] input, OptionSpec<String> table, boolean justTypes) {
        if (justTypes) {
            return;
        }

        JynParser.JynOptions opts = parser.jynParse(input);

        String tableName = opts.valueOf(table);
        DescribeTableRequest request = new DescribeTableRequest()
                .withTableName(tableName);
        log.info("Describing table", "request", request);
        DescribeTableResult result = _client.describeTable(request);

        System.out.println("DESCRIBE TABLE: " + result);
    }
}
