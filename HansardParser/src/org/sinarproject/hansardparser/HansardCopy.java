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
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import java.io.FileOutputStream;
import static java.lang.System.out;

/**
 *
 * @author leow
 */
public class HansardCopy {

    public static void copyHalamanFrontPage() throws DocumentException, IOException {
        String topic_title = "0-intro";
        int start_page = 1;
        int end_page = 1;
        out.println("Copying to the file " + topic_title
                + ".pdf starting from page " + start_page
                + " till page " + end_page);
        copySelectedPages(start_page, end_page, topic_title);
    }

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
        // Only if end_page it is NOT
        //  the first page
        //  OR the last page
        if (!((end_page == 1)
                || (end_page >= HansardParser.my_reader.getNumberOfPages()))) {
            // Then copy out an additional page; just to cover possible extra content 
            // not the best; but an acceptable trade-off at this time
            PdfImportedPage my_half_page;
            my_half_page = copy.getImportedPage(HansardParser.my_reader, end_page + 1);
            // A layer that goes on top of the text and graphics
            //  You can get an instance of this upper layer with the method 
            //  PdfWriter.getDirectContent().
            /*
             PdfContentByte my_dc;
             my_dc = my_half_page.getPdfWriter().getDirectContent();
             */
            // Since covering with a white square will not save any further space; 
            //  just leave it be .. can look at the approach above at a future time 
            copy.addPage(my_half_page);
        }
        document.close();
    }
}
