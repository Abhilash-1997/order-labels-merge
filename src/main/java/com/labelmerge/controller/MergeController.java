package com.labelmerge.controller;

import com.labelmerge.service.PdfMergeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class MergeController {

    private static final Logger log =
            LoggerFactory.getLogger(MergeController.class);

    private final PdfMergeService pdfMergeService;

    public MergeController(PdfMergeService pdfMergeService) {
        this.pdfMergeService = pdfMergeService;
    }

    @PostMapping("/merge")
    public ResponseEntity<byte[]> mergeLabels(
            @RequestParam("files") MultipartFile[] files) throws Exception {

        log.info("Merge API called");
        log.info("Number of files received: {}", files.length);

        long start = System.currentTimeMillis();

        byte[] pdf = pdfMergeService.merge(files);

        long end = System.currentTimeMillis();
        log.info("PDF generated successfully, size={} bytes, time={} ms",
                pdf.length, (end - start));

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=labels_A4.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

}

