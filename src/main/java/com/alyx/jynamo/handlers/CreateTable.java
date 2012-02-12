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

import java.util.List;

import static com.alyx.Log.log;

public class CreateTable extends CmdHandler {

    protected OptionSpec<String> _hashKeyName;
    protected OptionSpec<String> _hashKeyType;
    protected OptionSpec<String> _rangeKeyName;
    protected OptionSpec<String> _rangeKeyType;
    protected OptionSpec<Integer> _readCapacity;
    protected OptionSpec<Integer> _writeCapacity;

    public CreateTable (AmazonDynamoDBClient client) {
        super(client);
    }

    @Override
    public String handlesCommand () {
        return "create";
    }

    @Override
    public String help () {
        return (" create --hash-key-name <name> --hash-key-type <N|S> <tableName>\n" +
                "   Creates a new table in the DB with the given primary key. Optionally, --range-key-name and\n" +
                "   --range-key-type may be supplied, to create a composite key.");
    }

    @Override
    public void configure (JynParser parser) {
        _hashKeyName = parser.stringOpt(
                "hash-key-name", "The name of the Table's main key; required for table creation.", null);
        _hashKeyType = parser.stringOpt(
                "hash-type", "The attribute type of the hash key: S or N.", null);
        _rangeKeyName = parser.stringOpt(
                "range-key-name", "The name of the Table's optional range key; for table creation.", null);
        _rangeKeyType = parser.stringOpt(
                "range-type", "The attribute type of the range key: S or N.", null);
        // TODO: default value parsing is all fucked up
        _readCapacity = parser.intOpt(
                "read-capacity", "The read throughput of the table. Defaults to 10.", 10);
        _writeCapacity = parser.intOpt(
                "write-capacity", "The write throughput of the table. Defaults to 10.", 10);
    }

    @Override
    public void execute (JynParser.JynOptions opts, List<String> args) {
        KeySchemaElement hashElement= new KeySchemaElement()
                .withAttributeName(opts.require(_hashKeyName))
                .withAttributeType(opts.require(_hashKeyType));
        KeySchema schema = new KeySchema()
                .withHashKeyElement(hashElement);
        if (opts.has(_rangeKeyName)) {
            KeySchemaElement rangeElement= new KeySchemaElement()
                    .withAttributeName(opts.require(_rangeKeyName))
                    .withAttributeType(opts.require(_rangeKeyType));
            schema.setRangeKeyElement(rangeElement);
        }

        if (args.size() != 1) {
            System.err.println("Usage: " + help());
            return;
        }
        String tableName = args.get(0);
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
