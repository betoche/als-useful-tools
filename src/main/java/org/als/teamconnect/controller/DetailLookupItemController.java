package org.als.teamconnect.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.als.teamconnect.ExcelUtils;
import org.als.teamconnect.entity.DesignChange;
import org.als.teamconnect.entity.DetailLookupItem;
import org.als.teamconnect.repo.DesignChangeRepository;
import org.als.teamconnect.repo.DetailLookupItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/lookup-table")
public class DetailLookupItemController {
    @Autowired
    private DetailLookupItemRepository detailLookupItemRepository;
    @Autowired
    private DesignChangeRepository designChangeRepository;
    private final static String Y_DESIGN_CHANGE = "Y_DESIGN_CHANGE";
    private final static String Y_DETAIL_LOOKUP_ITEM = "Y_DETAIL_LOOKUP_ITEM";

    @GetMapping("/detail-lookup-item-list")
    public ResponseEntity<Iterable<DetailLookupItem>> getDetailLookupItemList() {
        return ResponseEntity.ok(detailLookupItemRepository.findAll());
    }

    @GetMapping("/design-change-list")
    public ResponseEntity<Iterable<DesignChange>> getDesignChangeList() {
        return ResponseEntity.ok(designChangeRepository.findAll());
    }

    @GetMapping("/import")
    public ModelAndView showImportExcelFileForm() {
        cleanSessionObjects();
        ModelAndView mv = new ModelAndView("import-excel-file");
        mv.addObject("hasDataToImport", false);
        return mv;
    }

    @PostMapping("/import-data")
    public ModelAndView importData() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes)requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        HttpSession httpSession = request.getSession(true);
        Object detailLookupItemListObj = httpSession.getAttribute("detailLookupItemList");
        Object designChangeListObj = httpSession.getAttribute("designChangeList");

        if( Objects.nonNull(detailLookupItemListObj) && detailLookupItemListObj instanceof List ) {
            saveDetailLookupItemListTree((List<DetailLookupItem>)detailLookupItemListObj);

        } else if( Objects.nonNull(designChangeListObj) ) {
            saveDesignChangeList((List<DesignChange>)designChangeListObj);
        }

        ModelAndView mv = new ModelAndView("import-excel-file");
        mv.addObject("hasDataToImport", false);

        return mv;
    }

    private void saveDesignChangeList(List<DesignChange> designChangeList) {
        for( DesignChange designChange : designChangeList ){
            if(designChangeRepository.findById(designChange.getPrimaryKey()).isEmpty()){
                designChangeRepository.save(designChange);
            }
        }
    }

    private void saveDetailLookupItemListTree( List<DetailLookupItem> detailLookupItemList ) {
        for( DetailLookupItem item : detailLookupItemList ) {
            if(detailLookupItemRepository.findById(item.getPrimaryKey()).isEmpty()) {
                if( !alreadyExists(item) ) {
                    detailLookupItemRepository.save(item);
                }
            }
            if( Objects.nonNull(item.getChildren()) && !item.getChildren().isEmpty() ) {
                saveDetailLookupItemListTree(item.getChildren());
            }

        }
    }
    private boolean alreadyExists( DetailLookupItem item ) {
        boolean alreadyExists = false;
        Optional<DetailLookupItem> optional = detailLookupItemRepository.findByParentIdAndNameAndTableId(item.getParentId(), item.getName(), item.getTableId());
        if( optional.isPresent() ) {
            alreadyExists = true;
            DetailLookupItem tmpItem = optional.get();
            if( listHasObjects(item.getChildren()) ) {
                for( DetailLookupItem detail : item.getChildren() ) {
                    detail.setParentId(tmpItem.getPrimaryKey());
                }
            }
        }

        return alreadyExists;
    }


    @PostMapping("/analyze")
    public ModelAndView analizeData(@RequestParam("file-name") MultipartFile reapExcelDataFile,
                                                  @RequestParam("table-name") String tableName) throws IOException {
        List<Object[]> fileData = ExcelUtils.getDataInSheet(reapExcelDataFile.getInputStream(), 0, true);

        List<DetailLookupItem> detailLookupItemList = null;
        List<DesignChange> designChangeList = null;
        boolean hasData = false;

        switch (tableName) {
            case Y_DESIGN_CHANGE -> designChangeList = DesignChange.parseFromObjectList(fileData);
            case Y_DETAIL_LOOKUP_ITEM -> detailLookupItemList = DetailLookupItem.parseFromObjectList(fileData);
        }
        addSessionObject(detailLookupItemList, designChangeList);

        validateLookupDetailItemAgainstDB(detailLookupItemList);

        ModelAndView mv = new ModelAndView("import-excel-file");
        mv.addObject("detailLookupItemList", detailLookupItemList );
        mv.addObject("detailLookupItemSize", DetailLookupItem.getTotalItemSize(detailLookupItemList));
        mv.addObject("designChangeList", designChangeList );

        mv.addObject("hasDataToImport", (listHasObjects(designChangeList) || listHasObjects(detailLookupItemList)));

        return mv;
    }

    private void validateLookupDetailItemAgainstDB( List<DetailLookupItem> detailLookupItemList ){
        if( listHasObjects(detailLookupItemList) ) {
            for( DetailLookupItem item : detailLookupItemList ){
                Optional<DetailLookupItem> tmpParent = Optional.empty();
                Optional<DetailLookupItem> tmpItem = detailLookupItemRepository.findById(item.getPrimaryKey());
                item.setExistsInDB(tmpItem.isPresent());

                if( Objects.nonNull(item.getParentId()) ) {
                    tmpParent = detailLookupItemRepository.findById(item.getParentId());
                } else {
                    if(alreadyExists(item)){
                        item.setExistsInDB(true);
                    }
                }

                tmpItem.ifPresent(detailLookupItem -> item.setDbName(detailLookupItem.getName()));

                item.setParentExistsInDB(tmpParent.isPresent());
                tmpParent.ifPresent(detailLookupItem -> item.setDbParentName(detailLookupItem.getName()));

                if( listHasObjects(item.getChildren()) ) {
                    validateLookupDetailItemAgainstDB(item.getChildren());
                }
            }
        }
    }

    private boolean listHasObjects( List list ) {
        return Objects.nonNull(list) && list.size()>0;
    }

    private void cleanSessionObjects() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes)requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("detailLookupItemList", null);
        httpSession.setAttribute("designChangeList", null);
    }

    private void addSessionObject( List<DetailLookupItem> detailLookupItemList, List<DesignChange> designChangeList) {
        cleanSessionObjects();

        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes)requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("detailLookupItemList", detailLookupItemList);
        httpSession.setAttribute("designChangeList", designChangeList);
    }
}
