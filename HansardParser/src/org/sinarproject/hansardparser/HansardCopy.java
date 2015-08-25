/*
 * To copy Hansard; all the boundary contexts here .. ?
 */
package org.sinarproject.hansardparser;

// Java Standard libs ...
import com.itextpdf.text.Document;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
// iTextPDF libs ..
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import java.io.FileOutputStream;

/**
 *
 * @author leow
 */
public class HansardCopy {

    private static void copyHalamanbyTopic(Map<Integer, Integer> myHalamanStartEnd, Map<Integer, List<String>> myHalamanHash)
            throws FileNotFoundException, DocumentException, IOException {
        Document document;
        PdfCopy copy;
        document = new Document();
        copy = new PdfCopy(document,
                new FileOutputStream("imokman.pdf"));
        document.open();
        int start_page = 0;
        int end_page = 0;
        for (int i = start_page; i <= end_page; i++) {
            copy.addPage(copy.getImportedPage(HansardParser.my_reader, i));
        }
        document.close();
    }

    // Determine the final filename of breaked up file??
    private static void getFinalPdfCopyFilename() {

    }

}
