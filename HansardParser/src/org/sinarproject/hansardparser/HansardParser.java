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
        // Below gets the Topics associated with each page ..
        Map<Integer, List<String>> myHalamanHash;
        myHalamanHash = HansardParser.getHalaman(reader);
        // Below gets the start page and end page mappings
        Map<Integer, Integer> myHalamanStartEnd;
        myHalamanStartEnd = HansardParser.splitHalamanbyTopic(myHalamanHash);
        // Identify the playas
        HansardParser.identifySpeakersinTopic(myHalamanStartEnd, myHalamanHash);

        // Below copies out the files and split them ..
        /*
         try {
         HansardParser.copyHalamanbyTopic(myHalamanStartEnd, myHalamanHash);
         } catch (FileNotFoundException ex) {
         Logger.getLogger(HansardParser.class.getName()).log(Level.SEVERE, null, ex);
         } catch (DocumentException ex) {
         Logger.getLogger(HansardParser.class.getName()).log(Level.SEVERE, null, ex);
         }
         */
    }

    private static void copyHalamanbyTopic(Map<Integer, Integer> myHalamanStartEnd, Map<Integer, List<String>> myHalamanHash)
            throws FileNotFoundException, DocumentException, IOException {
        // process topicName by replacing space with _?? or leave it as is?
        // Iterate through the hash ..
        // Form final filename
        // Copy over the pages ..
        int topicIndex = 0;
        int currentIndex = 0;

        // Apply the offset ..
    }

    private static void prepareSpeechBlock(String final_marked_content) {

        // replace all newline with space /\n+/g
        Pattern pattern_newlines;
        pattern_newlines = Pattern.compile("\\n+");
        final_marked_content = pattern_newlines.matcher(final_marked_content).replaceAll(" ");
        // Look for the speaker pattern --> /(\s+.+?|IMOKMAN\*\*)(.+?)(>>|$)/g;
        //  and have each section recognized .. $2 is the SpeechBlock
        Pattern pattern_marked_speakers;
        pattern_marked_speakers = Pattern.compile("(\\s+.+?|IMOKMAN\\*\\*)(.+?)(>>|$)");

        Matcher matched_marked_speakers = pattern_marked_speakers.matcher(final_marked_content);
        while (matched_marked_speakers.find()) {
            String matched_speech_block = matched_marked_speakers.group(2);
            // Check both patterns to extract out speaker ..
            Pattern pattern_mark_speakers;
            pattern_mark_speakers = Pattern.compile("(.+?)\\:");
            Matcher found_speakers = pattern_mark_speakers.matcher(matched_speech_block);
            Pattern pattern_mark_alt_speakers;
            // Pattern is [ ]+?(.+?\[.+?\])\s+?
            pattern_mark_alt_speakers = Pattern.compile("([ ]+.+\\[.+\\]\\s+?)minta");
            Matcher matched_alt_speakers = pattern_mark_alt_speakers.matcher(matched_speech_block);

            out.println("SPEECH_BLOCK");
            String final_message = "";
            String final_speaker = "";
            if (found_speakers.find()) {
                final_speaker = found_speakers.group(1);
                final_message = found_speakers.replaceAll("");
            } else if (matched_alt_speakers.find()) {
                final_speaker = matched_alt_speakers.group(1);
                final_message = matched_alt_speakers.replaceAll("");
            } else {
                final_speaker = "ERR";
                final_message = matched_speech_block;
            }
            out.println("Speaker " + final_speaker + " says ---> " + final_message);
            out.println("=============================");
            // Split out speaker from what was said; look for the : pattern
            // Maybe even detect time marker??
            // Special case; from previous page; append the previous guy ..
            // Mark the last guy to move on to the next page ..

        }
    }

    private static void preparePage(String content) {
        // Prepare page for analysis
        // Remove the DR line
        // Remove timeline marker??
        // Extract out the sections; if exist at least one : in it
        Pattern pattern_speaker_exist;
        pattern_speaker_exist = Pattern.compile("\\:");
        Pattern pattern_speaker_alt_exist;
        pattern_speaker_alt_exist = Pattern.compile("\\[.+?\\]");
        // Identify all the players and append the special >>IMOKMAN** tag to name
        // else if match the alternative patterns is OK too ...
        if (pattern_speaker_exist.matcher(content).find()
                || pattern_speaker_alt_exist.matcher(content).find()) {
            out.println("Found at least one speaker :) ... replacing >>> ");
            // put here since need to use it first to identify speakers in page ..
            Pattern pattern_mark_speakers;
            pattern_mark_speakers = Pattern.compile("(.+?\\:)");
            Matcher found_speakers = pattern_mark_speakers.matcher(content);
            out.println("SPEAKERS:");
            while (found_speakers.find()) {
                out.println(found_speakers.group(1));
            }
            out.println("<<<<<<<<<<<<<>>>>>>>>>>>>");
            // Do ALT first ... otherwise will have double ..
            Pattern pattern_mark_alt_speakers;
            // Pattern is [ ]+?(.+?\[.+?\])\s+?
            pattern_mark_alt_speakers = Pattern.compile("([ ]+.+\\[.+\\]\\s+?)");
            // /(.+?\:)/g replace with >>IMOKMAN**$1
            // Should check for the special exception of not being person Tuan Speaker ..
            Matcher matched_alt_speakers = pattern_mark_alt_speakers.matcher(content);
            // replace the specific case ..
            String marked_content = matched_alt_speakers.replaceAll(">>IMOKMAN**$1");
            // Start the main replacement .. the general one
            // /(.+?\:)/g replace with >>IMOKMAN**$1
            // Should check for the special exception of not being person Tuan Speaker ..
            Matcher matched_speakers = pattern_mark_speakers.matcher(marked_content);
            String final_marked_content;
            final_marked_content = matched_speakers.replaceAll(">>IMOKMAN**$1");
            // For debugging
            // out.println(final_marked_content);
            HansardParser.prepareSpeechBlock(final_marked_content);

        } // else process and atatch to previous speaker
        else {
            // Skip for now
            out.println("Found no speaker :( ... skipping ..");
            // Next time attch to the last known speaker ..
        }

    }

    private static void identifySpeakersinTopic(Map<Integer, Integer> myHalamanStartEnd, Map<Integer, List<String>> myHalamanHash) throws IOException {
        // NOTE: Assumes: first page is index; and there is offset .. does it apply across the spectrum??
        // Should probably put a safe guard to test actual start/end ...
        // Look out for the DR pattern and its page number ..
        // TODO: Above ..
        for (Integer current_page : myHalamanStartEnd.keySet()) {
            int start_page = current_page + 1;
            int end_page = myHalamanStartEnd.get(current_page) + 1;
            out.println("For current block with title; start page is " + start_page + " and end page is " + end_page);
            for (int i = start_page; i <= end_page; i++) {
                // PdfDictionary pageDict = reader.getPageN(i);
                // use location based strategy
                out.println("Page " + i);
                out.println("===========");
                String content = PdfTextExtractor.getTextFromPage(HansardParser.my_reader, i);
                // out.println(content);
                // Identify people ..
                HansardParser.preparePage(content);
                // ... and what they say??
                // How to regexp detect paragraph ..
            }
            // Rescan needed? no need; just give CMS to tag their speech ..
            // for demo; break out after first cycle ..
            break;
        }
    }

    private static Map<Integer, Integer> splitHalamanbyTopic(Map<Integer, List<String>> myHalamanHash) {
        Map<Integer, Integer> Halamans = new TreeMap<>();
        // Halamans[0] = topic, start_page, end_page (1,1)
        // Halamans[1] = topic, start_page, end_page (1,26)
        // Halamans[2] = topic, start_page, end_page (27,60)
        // Halamans[3] = topic, start_page, end_page (61

        // Actual Start page (Start page + 1)
        // Actual End page (Page Number - 1)
        out.println("Sorted Pages and Title");
        out.println("=======================");
        // Send back ordered HashMap of Start page and mapped End page
        int current_start_page = 0;
        int current_end_page = 0;
        int previous_page = 0;
        // Current index - 1, fill in the end_page based on start_page - 1; if start_page != 1, else fill 1
        for (Integer myStart_Page : myHalamanHash.keySet()) {
            current_start_page = myStart_Page;
            if (previous_page == 0) {
                // do nothing
            } else {
                if (current_start_page == 1) {
                    // special case: remain as 1
                    current_end_page = 1;
                } else {
                    // it will be start_page - 1
                    current_end_page = current_start_page - 1;
                }
                // Attach to previous page index the value
                out.println("Calculated block starting at page " + previous_page + " end at page " + current_end_page);
                Halamans.put(previous_page, current_end_page);
            }
            // Upddate previous_page_index to the current start_page
            previous_page = current_start_page;
            // Below for debugging purposes only ..
            /*
             List<String> myTopicList = myHalamanHash.get(myStart_Page);
             for (String myTopic : myTopicList) {
             out.println("Page: " + myStart_Page + " Topic: " + myTopic);
             }
             */

        }
        // At the end; we need to know that the previous page; the endpage is size of the whole doc - 1
        current_end_page = HansardParser.my_reader.getNumberOfPages() - 1;
        out.println("FINALLY: Block starting at page " + previous_page + " end at page " + current_end_page);
        Halamans.put(previous_page, current_end_page);

        return Halamans;
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
