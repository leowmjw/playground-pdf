/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sinarproject;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.GlyphTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.IOException;
import static java.lang.System.out;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.boon.Maps.map;
import org.boon.primitive.Int;

/**
 *
 * @author leow
 */
public class ECRedelineation {

    public static String SOURCE_2ND = "./example/Sarawak_2ndSchedule.pdf";
    public static String SOURCE = "./example/Sarawak_Proposal.pdf";
    static PdfReader my_reader;
    static int currentScheduleBlock = 0;
    static String currentPARLabel;
    static String currentDUNLabel;
    static Map<String, String> final_mapped_data;
    static Map<String, String> error_while_parsing;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        out.println("Sinar Project's EC Parser ..");
        PdfReader reader = null;
        try {
            reader = new PdfReader(SOURCE);
            int n;
            n = reader.getNumberOfPages();
            int i;
            // Maps init .. Key is <PAR_CODE>/<DUN_CODE>/<DM_CODE>
            // <Key> -> Name:Population
            // Errors -> Map<ErrKey, Original String>; ErrKey is Nxx
            final_mapped_data = new TreeMap<>();
            error_while_parsing = new TreeMap<>();
            // Loop through each page ..
            for (i = 1; i < n; i++) {
                try {
                    String content;
                    content = PdfTextExtractor.getTextFromPage(reader, i, new LocationTextExtractionStrategy());
                    describePage(content, i);
                } catch (IOException ex) {
                    Logger.getLogger(ECRedelineation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // Dump out error hash .. if any
            if (error_while_parsing.size() > 0) {
                out.println("========================");
                out.println("    PARSING ERRORS     ");
                out.println("========================");
                for (Map.Entry<String, String> single_report_entry : error_while_parsing.entrySet()) {
                    out.println("CODE: " + single_report_entry.getKey());
                    out.println("UNMATCHED: " + single_report_entry.getValue());
                }
            } else {
                out.println("========================");
                out.println("      ALL OK!!!         ");
                out.println("========================");
                
            }
            out.println("xxxxxxxXXXXXXXXXXxxxxxxxxx");
        } catch (IOException ex) {
            Logger.getLogger(ECRedelineation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void describePage(String raw_extracted_content, int current_page) {
        // DEBUG: 
        // out.print(raw_extracted_content);
        if (Utils.isStartOfSchedule(raw_extracted_content)) {
            out.println("Mark page " + current_page + " as a NEW Block");
        };
        switch (currentScheduleBlock) {
            case 1:
                // DO nothing .. for now ..
                break;
            case 2:
                /*
                 Map<String, Map<String, Map<String, Int>>> m;
                 m = null;
                 Map<Character, Map<Character, Map<Character, Integer>>> n;
                 n = map('d',
                 map('c',
                 map('s', 1)
                 )
                 );
                 */
                // Esier code 123/12/33
                //  returns a snippet instead ..
                // Arrays of DMs, DMs[] = ""
                // Init PAR structure??
                // [PAR][DUN][DM]
                // Map<String, Map<String,Map<String,Int>>>
                // Do something ..
                // Look for PAR Header; extract out the value ..
                // Break apart the content by line ..
                // match ..
                String[] lines_of_content = raw_extracted_content.split("\\r?\\n");
                for (String single_line_of_content : lines_of_content) {
                    // DEBUG: For a look at the raw line to be process ..
                    // out.println("LINE:" + single_line_of_content);
                    if (Utils.isStartOfPAR(single_line_of_content)) {
                        // Anything to do with PAR??
                        out.println(single_line_of_content);
                        out.println("========^^^ MATCHED PAR ^^^^=======");
                    } else if (Utils.isStartOfDUN(single_line_of_content)) {
                        // ANything to do DUN
                        out.println(single_line_of_content);
                        out.println("========^^^ MATCHED DUN ^^^^=======");
                    } else if (Utils.containsDMData(single_line_of_content)) {
                        // Extract DM
                    } else {
                        // Nothing to do ..
                    };
                }
                break;
            case 3:
                // Do nothing ... for now ..
                break;
            default:
                break;
        }
    }

}
