//
// Who - Copyright 2010-2011 Three Rings Design, Inc.

package com.alyx.jynamo.handlers;

import com.alyx.jynamo.CmdHandler;
import com.alyx.jynamo.JynParser;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.CreateTableRequest;
import com.amazonaws.services.dynamodb.model.CreateTableResult;
import com.amazonaws.services.dynamodb.model.KeySchema;
import com.amazonaws.services.dynamodb.model.KeySchemaElement;
import com.amazonaws.services.dynamodb.model.ProvisionedThroughput;
import joptsimple.OptionSpec;

import static com.alyx.Log.log;

public class CreateTable extends CmdHandler {
    public CreateTable (AmazonDynamoDBClient client) {
        super(client);
    }

    @Override
    public String handlesCommand () {
        return "create";
    }

    @Override
    public void execute (JynParser parser, String[] input, OptionSpec<String> table, boolean justTypes) {
        OptionSpec<String> hashKeyName = parser.stringOpt(
                "hash-key-name", "The name of the Table's main key; required for table creation.", null);
        OptionSpec<String> hashKeyType = parser.stringOpt(
                "hash-type", "The attribute type of the hash key: S or N.", null);
        OptionSpec<String> rangeKeyName = parser.stringOpt(
                "range-key-name", "The name of the Table's optional range key; for table creation.", null);
        OptionSpec<String> rangeKeyType = parser.stringOpt(
                "range-type", "The attribute type of the range key: S or N.", null);
        // TODO: default value parsing is all fucked up
        OptionSpec<Integer> readCapacity = parser.intOpt(
                "read-capacity", "The read throughput of the table. Defaults to 10.", 10);
        OptionSpec<Integer> writeCapacity = parser.intOpt(
                "write-capacity", "The write throughput of the table. Defaults to 10.", 10);

        if (justTypes) {
            return;
        }

        JynParser.JynOptions opts = parser.jynParse(input);

        KeySchemaElement hashElement= new KeySchemaElement()
                .withAttributeName(opts.require(hashKeyName))
                .withAttributeType(opts.require(hashKeyType));
        KeySchema schema = new KeySchema()
                .withHashKeyElement(hashElement);
        if (opts.has(rangeKeyName)) {
            KeySchemaElement rangeElement= new KeySchemaElement()
                    .withAttributeName(opts.require(rangeKeyName))
                    .withAttributeType(opts.require(rangeKeyType));
            schema.setRangeKeyElement(rangeElement);
        }
        String tableName = opts.valueOf(table);
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(tableName)
                .withKeySchema(schema)
                .withProvisionedThroughput(
                        new ProvisionedThroughput()
                                .withReadCapacityUnits(10L)
                                .withWriteCapacityUnits(10L));
        log.info("Creating table", "request", request);
        CreateTableResult result = _client.createTable(request);

        System.out.println("CREATE TABLE: " + result);
    }
}
