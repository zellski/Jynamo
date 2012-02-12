//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo.handlers;

import com.alyx.jynamo.CmdHandler;
import com.alyx.jynamo.JynParser;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.GetItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemResult;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.PutItemResult;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import joptsimple.OptionSpec;

import java.util.List;
import java.util.Map;

public class GetItem extends CmdHandler {
    public GetItem (AmazonDynamoDBClient client) {
        super(client);
    }

    @Override
    public String handlesCommand () {
        return "get";
    }

    @Override
    public String help () {
        return (" get --table <table> <hashKeyValue> [<rangeKeyValue>]\n" +
                "   Retrieve an item with the given hash key value and optionally range key value, if the table\n" +
                "   is configure with a composite key.");
    }

    @Override
    public void configure (JynParser parser) {
        _tableName = parser.stringOpt(
                "table-name", "The name of the Table's main key; required for table creation.", null);
    }

    @Override
    public void execute (JynParser.JynOptions opts, List<String> args) {
        String tableName = opts.require(_tableName);

        // start building the item lookup key
        Key key = new Key();

        if (args.size() < 1) {
            System.err.println("Usage: " + help());
            return;
        }
        AttributeValue hashKey = new AttributeValue();
        String hashKeyStr = args.get(0);
        if (hashKeyStr.startsWith("#")) {
            hashKey.withN(hashKeyStr.substring(1));
        } else {
            hashKey.withS(hashKeyStr);
        }
        key.withHashKeyElement(hashKey);
        if (args.size() > 1) {
            AttributeValue rangeKey = new AttributeValue();
            String rangeKeyStr = args.get(1);
            if (rangeKeyStr.startsWith("#")) {
                rangeKey.withN(rangeKeyStr.substring(1));
            } else {
                rangeKey.withS(rangeKeyStr);
            }
            key.withRangeKeyElement(rangeKey);
        }

        GetItemRequest request = new GetItemRequest()
                .withKey(key)
                .withTableName(tableName);

        GetItemResult result = _client.getItem(request);

        System.out.println("GET ITEM: " + result);
    }

    protected OptionSpec<String> _tableName;
}
