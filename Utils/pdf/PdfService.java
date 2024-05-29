package com.thread_exec.thread_executor.Utils.pdf;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;


public interface PdfService {
    public ByteArrayInputStream createPdf();
}
