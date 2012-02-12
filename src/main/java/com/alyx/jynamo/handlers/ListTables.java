//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo.handlers;

import com.alyx.jynamo.CmdHandler;
import com.alyx.jynamo.JynParser;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.DescribeTableRequest;
import com.amazonaws.services.dynamodb.model.DescribeTableResult;
import com.amazonaws.services.dynamodb.model.ListTablesRequest;
import com.amazonaws.services.dynamodb.model.ListTablesResult;
import joptsimple.OptionSpec;

import static com.alyx.Log.log;

public class ListTables extends CmdHandler {
    public ListTables (AmazonDynamoDBClient client) {
        super(client);
    }

    @Override
    public String handlesCommand () {
        return "list";
    }

    @Override
    public String help () {
        return (" list\n" +
                "   Enumerates all existing tables and outputs their names as a JSON array.");
    }

    @Override
    public void execute (JynParser parser, String[] input, OptionSpec<String> table, boolean justTypes) {
        if (justTypes) {
            return;
        }

        ListTablesResult result = _client.listTables();

        System.out.println("LIST TABLES: " + result);
    }
}
