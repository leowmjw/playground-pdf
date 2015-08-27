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
import static java.lang.System.out;

/**
 *
 * @author leow
 */
public class HansardCopy {

    public static void copyHalamanbyTopic(Map<Integer, Integer> myHalamanStartEnd, Map<Integer, List<String>> myHalamanHash)
            throws FileNotFoundException, DocumentException, IOException {
        // Get Topic Title; to be used as filename ..
        for (Integer current_page : myHalamanStartEnd.keySet()) {
            // Get the Topic Title that is normalized
            String topic_title = Utils.getTopicbyPageNumber(current_page, myHalamanHash);
            // Apply the offset ..
            int start_page = current_page + 1;
            int end_page = myHalamanStartEnd.get(current_page) + 1;
            out.println("Copying to the file " + topic_title
                    + ".pdf starting from page " + start_page
                    + " till page " + end_page);
            copySelectedPages(start_page, end_page, topic_title);
        }

    }

    private static void copySelectedPages(int start_page, int end_page, String topic_title)
            throws FileNotFoundException, DocumentException, IOException {
        Document document;
        PdfCopy copy;
        document = new Document();
        copy = new PdfCopy(document,
                new FileOutputStream("results/" + topic_title + ".pdf"));
        document.open();
        for (int i = start_page; i <= end_page; i++) {
            copy.addPage(
                    copy.getImportedPage(HansardParser.my_reader, i)
            );
        }
        document.close();
    }

    // Determine the final filename of breaked up file??
    private static void getFinalPdfCopyFilename() {

    }

}
