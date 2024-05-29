package com.thread_exec.thread_executor.Utils;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.thread_exec.thread_executor.Utils.pdf.StoreDataPdfService;
import com.thread_exec.thread_executor.model.StoreData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Parent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class StotePdfServiceImpl implements StoreDataPdfService {

    private static final String PDF_TYPE = "STORE DATA";
    @Override
    public ByteArrayInputStream createStoreDataPdf(List<StoreData> storeDataList) {
        log.info("creating Pdf for : "+ PDF_TYPE);
        String pdf_title = "This is the title of the page";
        String pdf_content = "This is the content of the page";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(),1,1,1,1);
        PdfWriter.getInstance(document,outputStream);
        document.open();
//
//        Font font = FontFactory.getFont(FontFactory.TIMES,25);
//        Paragraph content_title = new Paragraph(pdf_title, font);
//        content_title.setAlignment(Element.ALIGN_CENTER);
//        document.add(content_title);
//
//        Font font1 = FontFactory.getFont(FontFactory.COURIER_BOLD,20);
//        Paragraph content = new Paragraph(pdf_content, font1);
//        content.setAlignment(Element.ALIGN_CENTER);
//        document.add(new Chunk("Adding the chunk of data"));
//        document.add(content);
//        document.close();
        createPdfForStoreData(storeDataList, document);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public void createPdfForStoreData(List<StoreData> storeDataList, Document document){
        try{
            if (CollectionUtils.isNotEmpty(storeDataList) && document != null){

                Class<StoreData> storeDataClass = StoreData.class;
                Field[] fields = storeDataClass.getDeclaredFields();
                int numberOfFields = fields.length-1; // remove -1 for table index
//            addHeaderTable();
                Paragraph tableHeadingPara = new Paragraph("Store Data Information");
                tableHeadingPara.setAlignment(Element.ALIGN_CENTER);
                document.add(tableHeadingPara);
                document.add(new Paragraph("\n"));

                PdfPTable table = new PdfPTable(numberOfFields);
                addHeaderCell(table);
                addContent(table ,storeDataList);
                document.add(table);

            }else{
                log.error("data for generating pdf in invalid or empty ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            document.close();
        }
    }

    private void addContent( PdfPTable table,List<StoreData> storeDataList) {
        storeDataList.forEach(storeData -> {
            table.addCell(storeData.getInVoiceNumber());
            table.addCell(storeData.getInDate());
            table.addCell(storeData.getGender());
            table.addCell(storeData.getAge()!= null ? String.valueOf(storeData.getAge()) : "");
            table.addCell(storeData.getCategory());
            table.addCell(String.valueOf(storeData.getQuantity()));
            table.addCell(String.valueOf(storeData.getSellingPricePerUnit()));
            table.addCell(String.valueOf(storeData.getTotalProfit()));
            table.addCell(storeData.getPaymentMethod());
            table.addCell(storeData.getRegion());
            table.addCell(storeData.getState());
            table.addCell(storeData.getShoppingMall());
        });
    }

    private void addHeaderCell(PdfPTable table) {
        List<String> tableHeaderList = Arrays.asList(
                "InVoice Number",
                "INVoice Date",
                "Gender",
                "Age",
                "Category",
                "Quantity",
                "Selling Price Per Unit",
                "Total Profit",
                "Payment Method",
                "Region",
                "State",
                "Shopping Mall"
        );

        tableHeaderList.forEach(tableHeader -> {
            Font font = FontFactory.getFont(FontFactory.TIMES,15, Color.darkGray);

            PdfPCell pdfPCell = new PdfPCell(new Paragraph(tableHeader,font));

            table.addCell(pdfPCell);
        });
    }

    @Override
    public ByteArrayInputStream createPdf() {
        return null;
    }
}
