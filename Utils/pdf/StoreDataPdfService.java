package com.thread_exec.thread_executor.Utils.pdf;

import com.thread_exec.thread_executor.model.StoreData;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface StoreDataPdfService extends PdfService{

    public ByteArrayInputStream createStoreDataPdf(List<StoreData> storeDataList);

}
