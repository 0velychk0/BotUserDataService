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
@RequestMapping("/viberData")
public class ViberUserController {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @GetMapping("/createTable")
    public String createTable() {
        CreateTableRequest req = dynamoDBMapper.generateCreateTableRequest(ViberUserData.class);
        req.setProvisionedThroughput(new ProvisionedThroughput(2L, 2L));
        amazonDynamoDB.createTable(req);
        log.info("Creation done");
        return "Table created";
    }

    @GetMapping("/deleteTable")
    public String deleteTable() {
        DeleteTableRequest req = dynamoDBMapper.generateDeleteTableRequest(ViberUserData.class);
        amazonDynamoDB.deleteTable(req);
        log.info("Deletion done");
        return "Table deleted";
    }

    @GetMapping("/addItem")
    public String addItem(@RequestParam String id) {
        log.info("Get addItem called: {}", id);
        ViberUserData ar = new ViberUserData(id, id, id, Integer.parseInt(id), id, id);
        dynamoDBMapper.save(ar);
        return "Item '" + id + "' added";
    }

    @PostMapping("/addItem")
    public String addItem(@RequestBody ViberUserData data) {
        log.info("Post addItem called: {}", data);
        if (data != null) {
            dynamoDBMapper.save(data);
        }
        return "Item '" + data.getId() + "' added";
    }

    @GetMapping("/getItem")
    public String getItem(@RequestParam String id) {
        ViberUserData itemRetrieved = dynamoDBMapper.load(ViberUserData.class, id);
        log.info("Query done for: {}", id);
        return (itemRetrieved != null) ? itemRetrieved.toString() : ("item '" + id + "' not found");
    }

    @GetMapping("/getAll")
    public String getAll() {
        Map<String, AttributeValue> eav = new HashMap<>();
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

        List<ViberUserData> scanResult = dynamoDBMapper.scan(ViberUserData.class, scanExpression);

        log.info("Query done: {}", Arrays.toString(scanResult.toArray()));
        return Arrays.toString(scanResult.toArray());
    }

    @GetMapping("")
    public String test() {
        return "ViberData Service Active";
    }
}
