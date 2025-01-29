package org.als.random.controller;

import org.als.random.domain.CompareSnapshotsRequest;
import org.als.random.domain.DatabaseComparatorResults;
import org.als.random.domain.DatabaseDifference;
import org.als.random.domain.DatabaseSnapshotRequest;
import org.als.random.service.DBSnapshotService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/snapshots")
public class DatabaseSnapshotsController {
    @Autowired
    private DBSnapshotService snapshotService;

    @GetMapping({"", "/"})
    public ModelAndView getDBSnapshotsHome() {
        ModelAndView mv = new ModelAndView("snapshots");
        mv.addObject("snapshotGroupList", snapshotService.getDatabaseSnapshotGroupList());

        return mv;
    }

    @GetMapping("/load-snapshot-details")
    public ResponseEntity<String> getSnapshotDetailsByFilePath(@RequestParam String filePath) throws IOException {
        return  ResponseEntity.ok(DBSnapshotService.getSnapshotDetailsByFilePath(filePath).toJson().toString());
    }

    @GetMapping("/snapshot-comparator")
    public ModelAndView getSnapshotComparatorView() {
        ModelAndView mv = new ModelAndView("snapshot-comparator");
        mv.addObject("snapshotGroupList", snapshotService.getDatabaseSnapshotGroupList());

        return mv;
    }

    @PostMapping("/compare")
    public ResponseEntity<String> getSnapshotDifference(@RequestBody CompareSnapshotsRequest compareSnapshotsRequest) throws IOException {
        DatabaseComparatorResults results = new DatabaseComparatorResults(compareSnapshotsRequest.getSnapshotList());
        return ResponseEntity.ok(results.getDatabaseDifferenceListToJson().toString());
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDatabaseSnapshot(@RequestBody DatabaseSnapshotRequest request) {
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
