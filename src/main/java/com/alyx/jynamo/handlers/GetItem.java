//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo.handlers;

import com.alyx.jynamo.CmdHandler;
import com.alyx.jynamo.JynParser;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.PutItemResult;
import com.google.common.collect.ImmutableMap;
import joptsimple.OptionSpec;

public class GetItem extends CmdHandler {
    public GetItem (AmazonDynamoDBClient client) {
        super(client);
    }

    @Override
    public String handlesCommand () {
        return "put";
    }

    @Override
    public void execute (JynParser parser, String[] input, OptionSpec<String> table, boolean justTypes) {
        // TODO: handle either type
        OptionSpec<String> hashKeyName = parser.stringOpt(
                "hash-key-name", "The name of the Table's main key; required for table creation.", null);
        OptionSpec<Integer> hashKeyValue = parser.intOpt("hash-key-value", "The hash value to look up by.", null);

        if (justTypes) {
            return;
        }

        JynParser.JynOptions opts = parser.jynParse(input);

        String tableName = opts.valueOf(table);

        PutItemRequest request = new PutItemRequest()
                .withTableName(tableName)
                .withItem(ImmutableMap.of(
                        opts.require(hashKeyName), new AttributeValue().withN(opts.require(hashKeyValue).toString()),
                        "petName", new AttributeValue("Bubba")
                ));
        PutItemResult result = _client.putItem(request);

        System.out.println("PUT ITEM: " + result);
    }
}
