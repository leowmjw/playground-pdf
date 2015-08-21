/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sinarproject.hansardparser;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import static java.lang.System.out;
import java.util.Set;
import java.util.TreeSet;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author leow
 */
public class HansardParser {

    /**
     * The resulting PDF file.
     */
    public static String SOURCE
            = "example/DR-PARLIMEN/DR-18062015.PDF";
    // PdfReader for multiple uses?
    private static PdfReader my_reader;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        out.println("Sinar Project's Hansard Parser ..");
        PdfReader reader = new PdfReader(HansardParser.SOURCE);
        // Assign it for later reuse ..
        HansardParser.my_reader = reader;
        Map<Integer, List<String>> myHalamanHash;
        myHalamanHash = HansardParser.getHalaman(reader);
        // Itertae thoug it .. use example here and here ..
        HansardParser.splitHalamanbyTopic(myHalamanHash);

        int n;
        // n = reader.getNumberOfPages();
        // For test of extraction and regexp; use first 5 pages ..
        n = 2;
        for (int i = 2; i < n; i++) {
            // PdfDictionary pageDict = reader.getPageN(i);
            // use location based strategy
            out.println("Page " + i);
            out.println("===========");
            String content = PdfTextExtractor.getTextFromPage(reader, i);
            out.println(content);
        }
    }

    private void copyHalamanbyTopic(Integer start_page, Integer end_page, String topicName)
            throws FileNotFoundException, DocumentException, IOException {
        // process topicName by replacing space with _?? or leave it as is?

        // Apply the offset ..
    }

    private static void splitHalamanbyTopic(Map<Integer, List<String>> myHalamanHash) {
        // Iterate through the hash ..
        // Form final filename
        // Copy over the pages ..
        int topicIndex = 0;
        int currentIndex = 0;
        ArrayList<Integer> Halamans = new ArrayList<>();
        // Halamans[0] = topic, start_page, end_page (1,1)
        // Halamans[1] = topic, start_page, end_page (1,26)
        // Halamans[2] = topic, start_page, end_page (27,60)
        // Halamans[3] = topic, start_page, end_page (61

        // Actual Start page (Start page + 1)
        // Actual End page (Page Number - 1)
        out.println("Sorted Pages and Title");
        out.println("=======================");
        // Current index - 1, fill in the end_page based on start_page - 1; if start_page != 1, else fill 1
        for (Integer myStart_Page : myHalamanHash.keySet()) {
            // Current start will inform the previous end if != 1
            List<String> myTopicList = myHalamanHash.get(myStart_Page);
            for (String myTopic : myTopicList) {
                out.println("Page: " + myStart_Page + " Topic: " + myTopic);
            }
            
        }
    }

    private static Map<Integer, List<String>> getHalaman(PdfReader myreader) {
        try {
            // OPTIONE #1:
            // RegExp for Halaman is .. /(.*)\(Halaman.*(\d+)\)/ig
            // Pattern pattern_halaman = Pattern.compile("(.*)\\(Halaman.*(\\d+)\\)", Pattern.CASE_INSENSITIVE);
            // OPTION #2:
            // Better pattern is --> http://www.regexr.com/3bkfc
            // /(.*?\n?.*?)\(Halaman.*?(\d+)\).*?\n/igm
            Pattern pattern_halaman = Pattern.compile("(.*?\\n?.*?)\\(Halaman.*?(\\d+)\\).*?\\n", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
            // Title name is $1
            // Halaman # is $2
            // Extract out and log every matched items ??
            String content = PdfTextExtractor.getTextFromPage(myreader, 1);

            // Use TreeMap to have native sort in keys
            Map<Integer, List<String>> myHalaman = null;
            myHalaman = new TreeMap<>();

            Matcher halaman_matcher = pattern_halaman.matcher(content);
            while (halaman_matcher.find()) {
                // Replace all until : pattern ..??
                Pattern pattern_header = Pattern.compile(".*\\:", Pattern.CASE_INSENSITIVE);
                // Replace anything NOT valid Title -->  [^\(\)\-\.\w\d]           
                Pattern pattern_valid_title = Pattern.compile("[^\\(\\)\\-\\. \\w\\d]", Pattern.CASE_INSENSITIVE);
                // Replace multiple spaces into one space
                Pattern pattern_single_spacing = Pattern.compile("[ ]+");
                String halaman_title
                        = pattern_single_spacing.matcher(
                                pattern_valid_title.matcher(
                                        pattern_header.matcher(
                                                halaman_matcher.group(1)
                                        ).replaceAll("")
                                ).replaceAll("")
                        ).replaceAll(" ").trim();
                out.println("Title is: " + halaman_title);
                String halaman_page_number_str = halaman_matcher.group(2).trim();
                out.println("Start Page is: " + halaman_page_number_str);
                // This is the page number ..
                Integer halaman_page_number = new Integer(halaman_page_number_str);
                // Get the list from the current key; which is page number ...
                List<String> l;
                l = myHalaman.get(halaman_page_number);
                if (l == null) {
                    myHalaman.put(halaman_page_number, l = new ArrayList<String>());
                }
                l.add(halaman_title);
                // myHalaman.put(halaman_title, halaman_page_number_str);
                // Actually do the copy here??
                // Sort by page to rearrange the split ..
                // [1:a]   ====>  [1:a]
                // [1:b]   ====>  [1:b]
                // [27:c]  ====>  [23:e]
                // [61:d]  ====>  [27:c]
                // [23:e]  ====>  [23:e]
            }

            return myHalaman;
        } catch (IOException ex) {
            Logger.getLogger(HansardParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void IFail(String[] args) throws IOException {
        // TODO code application logic here
        out.println("Sinar Project's Hansard Parser ..");
        PdfReader reader = new PdfReader(HansardParser.SOURCE);
        int n;
        // n = reader.getNumberOfPages();
        // For test of extraction and regexp; use first 5 pages ..
        n = 3;
        for (int i = 1; i < n; i++) {
            PdfDictionary pageDict = reader.getPageN(i);
            // use location based strategy
            out.println("Page " + i);
            out.println("===========");

            out.println(pageDict.getKeys().toString());
            // get page
            PdfDictionary pPage = pageDict.getAsDict(PdfName.PAGE);
            // PdfDictionary asDict = pPage.getAsDict(PdfName.CONTENT);
            if (pPage == null) {
                out.println("Nothing to do here!!");
            } else {
                Set<PdfName> keys = pPage.getKeys();
                for (PdfName key : keys) {
                    out.println(key);
                }

            }
            Set<PdfName> pKeys = pageDict.getKeys();
            for (PdfName pKey : pKeys) {
                out.println("KEY is: " + pKey);
                out.println("=============");
                PdfDictionary myContentDict = pageDict.getAsDict(pKey);
                if (myContentDict != null) {
                    out.println(myContentDict.getKeys());

                } else {
                    out.println("nothing in this key");
                }
                PdfString myContentStr = pageDict.getAsString(pKey);
                if (myContentStr != null) {
                    out.println(myContentStr.toString());
                } else {
                    out.println("Found no PdfString");
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void mainFont(String[] args) {
        // TODO code application logic here
        out.println("Detecting the fonts in the file ..");
        // Open source file in example and read out the Format available
        // looking for bold ..
        // Example: example/DR-18062015.PDF
        Set<String> set = null;
        try {
            set = new HansardParser().listFonts(HansardParser.SOURCE);
        } catch (IOException ex) {
            Logger.getLogger(HansardParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String fontname : set) {
            out.println(fontname);
        }
    }

    /**
     * Creates a Set containing information about the fonts in the src PDF file.
     *
     * @param src the path to a PDF file
     * @return
     * @throws IOException
     */
    public Set<String> listFonts(String src) throws IOException {
        Set<String> set;
        set = new TreeSet<>();
        PdfReader reader = new PdfReader(src);
        PdfDictionary resources;
        for (int k = 1; k <= reader.getNumberOfPages(); ++k) {
            resources = reader.getPageN(k).getAsDict(PdfName.RESOURCES);
            processResource(set, resources);
        }
        reader.close();
        return set;
    }

    /**
     * Extracts the font names from page or XObject resources.
     *
     * @param set the set with the font names
     * @param resource
     */
    public static void processResource(Set<String> set, PdfDictionary resource) {
        if (resource == null) {
            return;
        }
        PdfDictionary xobjects = resource.getAsDict(PdfName.XOBJECT);
        if (xobjects != null) {
            for (PdfName key : xobjects.getKeys()) {
                processResource(set, xobjects.getAsDict(key));
            }
        }
        PdfDictionary fonts = resource.getAsDict(PdfName.FONT);
        if (fonts == null) {
            return;
        }
        PdfDictionary font;
        for (PdfName key : fonts.getKeys()) {
            font = fonts.getAsDict(key);
            String name = font.getAsName(PdfName.BASEFONT).toString();
            if (name.length() > 8 && name.charAt(7) == '+') {
                name = String.format("%s subset (%s)", name.substring(8), name.substring(1, 7));
            } else {
                name = name.substring(1);
                PdfDictionary desc = font.getAsDict(PdfName.FONTDESCRIPTOR);
                if (desc == null) {
                    name += " nofontdescriptor";
                } else if (desc.get(PdfName.FONTFILE) != null) {
                    name += " (Type 1) embedded";
                } else if (desc.get(PdfName.FONTFILE2) != null) {
                    name += " (TrueType) embedded";
                } else if (desc.get(PdfName.FONTFILE3) != null) {
                    name += " (" + font.getAsName(PdfName.SUBTYPE).toString().substring(1) + ") embedded";
                }
            }
            set.add(name);
        }
    }

}
