package com.labelmerge.controller;

import com.labelmerge.service.PdfMergeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class MergeController {

    private final PdfMergeService pdfMergeService;

    public MergeController(PdfMergeService pdfMergeService) {
        this.pdfMergeService = pdfMergeService;
    }

    @PostMapping("/merge")
    public ResponseEntity<byte[]> mergeLabels(@RequestParam("files") MultipartFile[] files) throws Exception {
        byte[] pdf = pdfMergeService.merge(files);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=labels_A4.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
