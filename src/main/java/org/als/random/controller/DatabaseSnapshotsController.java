package org.als.random.controller;

import org.als.random.domain.CompareSnapshotsRequest;
import org.als.random.domain.DatabaseComparatorResults;
import org.als.random.domain.DatabaseSnapshotRequest;
import org.als.random.enums.DatabaseTypeEnum;
import org.als.random.service.DBSnapshotService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RequestMapping("/snapshots")
public class DatabaseSnapshotsController {
    @Autowired
    private DBSnapshotService snapshotService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSnapshotsController.class);

    @GetMapping({"", "/"})
    public ModelAndView getDBSnapshotsHome() {
        long startTime = System.nanoTime();
        ModelAndView mv = new ModelAndView("snapshots");
        // TODO: implement parameter for sending the list without data
        mv.addObject("snapshotGroupList", snapshotService.getDatabaseSnapshotGroupList());
        mv.addObject("dbTypeList", DatabaseTypeEnum.values());
        double duration = ((double)(System.nanoTime() - startTime))/1_000_000.0;
        LOGGER.info("Retrieve snapshot group list process duration: %s seconds.".formatted(duration/1000));

        return mv;
    }

    @GetMapping("/load-snapshot-details")
    public ResponseEntity<String> getSnapshotDetailsByFilePath(@RequestParam String filePath) throws IOException {
        return  ResponseEntity.ok(DBSnapshotService.getSnapshotDetailsByFilePath(filePath, true).toJson().toString());
    }

    @GetMapping("/snapshot-comparator")
    public ModelAndView getSnapshotComparatorView() {
        // TODO: implement parameter for sending the list without data
        ModelAndView mv = new ModelAndView("snapshot-comparator");
        mv.addObject("snapshotGroupList", snapshotService.getDatabaseSnapshotGroupList());

        return mv;
    }

    @PostMapping("/compare")
    public ResponseEntity<String> getSnapshotDifference(@RequestBody CompareSnapshotsRequest compareSnapshotsRequest) throws IOException {
        long startTime = System.nanoTime();
        DatabaseComparatorResults results = new DatabaseComparatorResults(compareSnapshotsRequest.getSnapshotList());
        results.getNewRowsByTable(false);
        results.getUpdatedRowsByTable(false);

        String compareResponse = results.getDatabaseDifferenceListToJson().toString();
        double duration = ((double)(System.nanoTime() - startTime))/1_000_000.0;
        LOGGER.info("Snapshots comparison process duration: %s seconds.".formatted(duration/1000));
        return ResponseEntity.ok(compareResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDatabaseSnapshot(@RequestBody DatabaseSnapshotRequest request) throws URISyntaxException {
        return ResponseEntity.ok(snapshotService.createDatabaseSnapshot(request).toJson().toString());
    }

    @PostMapping("/delete-snapshot")
    public ResponseEntity<String> deleteSnapshotFile(@RequestParam String filePath){
        boolean result = false;
        String errorMessage = "NO";
        try {
            result = snapshotService.deleteSnapshotFile(filePath);
        } catch (IOException e) {
            errorMessage = e.getMessage();
        }
        JSONObject json = new JSONObject();
        json.put("operation", "delete-snapshot");
        json.put("snapshotFile", filePath);
        json.put("success", result);
        json.put("error", errorMessage);
        return ResponseEntity.ok(json.toString());
    }



}
