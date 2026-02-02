package com.labelmerge.service;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

@Service
public class PdfMergeService {

    public byte[] merge(MultipartFile[] files) throws Exception {

        PDRectangle A4 = PDRectangle.A4; // portrait
        int columns = 2;
        int rows = 2;

        float margin = 20;
        float gap = 10;

        float usableWidth = A4.getWidth() - (2 * margin) - gap;
        float usableHeight = A4.getHeight() - (2 * margin) - gap;

        float cellWidth = usableWidth / columns;
        float cellHeight = usableHeight / rows;

        try (PDDocument output = new PDDocument()) {

            LayerUtility layerUtility = new LayerUtility(output);

            PDPage page = null;
            PDPageContentStream cs = null;
            int slot = 0;

            for (MultipartFile file : files) {

                if (slot % 4 == 0) {
                    if (cs != null) cs.close();

                    page = new PDPage(A4);
                    output.addPage(page);
                    cs = new PDPageContentStream(output, page);
                    drawCutLines(cs, A4, margin);
                }

                try (PDDocument src = PDDocument.load(file.getInputStream())) {

                    PDRectangle srcSize = src.getPage(0).getMediaBox();
                    PDFormXObject form =
                            layerUtility.importPageAsForm(src, 0);

                    int index = slot % 4;
                    int row = index / columns;
                    int col = index % columns;

                    float x = margin + col * (cellWidth + gap);
                    float y = A4.getHeight()
                            - margin
                            - ((row + 1) * cellHeight)
                            - (row * gap);

                    float scale = Math.min(
                            cellWidth / srcSize.getWidth(),
                            cellHeight / srcSize.getHeight()
                    );

                    cs.saveGraphicsState();
                    cs.transform(Matrix.getTranslateInstance(x, y));
                    cs.transform(Matrix.getScaleInstance(scale, scale));
                    cs.drawForm(form);
                    cs.restoreGraphicsState();
                }

                slot++;
            }

            if (cs != null) cs.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            output.save(out);
            return out.toByteArray();
        }
    }



    private void drawCutLines(PDPageContentStream cs,
                              PDRectangle page,
                              float margin) throws Exception {

        cs.setLineWidth(0.5f);

        float midX = page.getWidth() / 2;
        float midY = page.getHeight() / 2;

        cs.moveTo(midX, margin);
        cs.lineTo(midX, page.getHeight() - margin);

        cs.moveTo(margin, midY);
        cs.lineTo(page.getWidth() - margin, midY);

        cs.stroke();
    }
}
