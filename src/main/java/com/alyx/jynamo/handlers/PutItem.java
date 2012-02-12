//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo.handlers;

import com.alyx.jynamo.CmdHandler;
import com.alyx.jynamo.JynParser;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.DeleteTableRequest;
import com.amazonaws.services.dynamodb.model.DeleteTableResult;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.PutItemResult;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import joptsimple.OptionSpec;

import java.util.List;
import java.util.Map;

public class PutItem extends CmdHandler {
    public PutItem (AmazonDynamoDBClient client) {
        super(client);
    }

    @Override
    public String handlesCommand () {
        return "put";
    }

    @Override
    public String help () {
        return (" put <key1>=<value1> [<key2>=<value2> ...]\n" +
                "   Creates a new item in the database with the given attributes, which must always include the\n" +
                "   table's primary hash key, and potentially a range key if the table was configured with a\n" +
                "   composite primary key.\n");
    }

    @Override
    public void configure (JynParser parser) {
        _tableName = parser.stringOpt(
                "table-name", "The name of the Table's main key; required for table creation.", null);
    }

    @Override
    public void execute (JynParser.JynOptions opts, List<String> args) {
        String tableName = opts.require(_tableName);

        Map<String, AttributeValue> item = Maps.newHashMap();
        for (String arg : args) {
            List<String> bits = Lists.newArrayList(Splitter.on("=").split(arg));
            if (bits.size() != 2) {
                System.err.println("Malformed key=value expression: " + arg);
            }
            AttributeValue value = new AttributeValue();
            String valueStr = bits.get(1);
            if (valueStr.startsWith("#")) {
                value.withN(valueStr.substring(1));
            } else {
                value.withS(valueStr);
            }
            item.put(bits.get(0), value);
        }

        PutItemRequest request = new PutItemRequest()
                .withTableName(tableName)
                .withItem(item);
        PutItemResult result = _client.putItem(request);

        System.out.println("PUT ITEM: " + result);
    }

    protected OptionSpec<String> _tableName;
}
