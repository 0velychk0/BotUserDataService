package com.ovelychko;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/telegramData")
public class TelegramUserController {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @GetMapping("/createTable")
    public String createTable() {
        CreateTableRequest req = dynamoDBMapper.generateCreateTableRequest(TelegramUserData.class);
        req.setProvisionedThroughput(new ProvisionedThroughput(2L, 2L));
        amazonDynamoDB.createTable(req);
        log.info("Creation done");
        return "Table created";
    }

    @GetMapping("/deleteTable")
    public String deleteTable() {
        DeleteTableRequest req = dynamoDBMapper.generateDeleteTableRequest(TelegramUserData.class);
        amazonDynamoDB.deleteTable(req);
        log.info("Deletion done");
        return "Table deleted";
    }

    @GetMapping("/addItem")
    public String addItem(@RequestParam String id) {
        TelegramUserData ar = new TelegramUserData(Long.parseLong(id), id, false, id, id, id, false, false, false);
        dynamoDBMapper.save(ar);
        log.info("Adding done: {}", id);
        return "Item '" + id + "' added";
    }

    @PostMapping("/addItem")
    public String addItem(@RequestBody TelegramUserData data) {
        log.info("Post addItem called: {}", data);
        if (data != null) {
            dynamoDBMapper.save(data);
        }
        return "Item '" + data.getId() + "' added";
    }

    @GetMapping("/getItem")
    public String getItem(@RequestParam String id) {
        TelegramUserData itemRetrieved = dynamoDBMapper.load(TelegramUserData.class, Long.parseLong(id));
        log.info("Query done for: {}", id);
        return (itemRetrieved != null) ? itemRetrieved.toString() : ("item '" + id + "' not found");
    }

    @GetMapping("/getAll")
    public String getAll() {
        Map<String, AttributeValue> eav = new HashMap<>();
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

        List<TelegramUserData> scanResult = dynamoDBMapper.scan(TelegramUserData.class, scanExpression);

        log.info("Query done: {}", Arrays.toString(scanResult.toArray()));
        return Arrays.toString(scanResult.toArray());
    }

    @GetMapping("")
    public String test() {
        return "TelegramData Service Active";
    }
}
