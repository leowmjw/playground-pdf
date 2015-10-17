/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sinarproject;

import static java.lang.System.out;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author leow
 */
public class Utils {

    // Patterns for the various regexp used
    // No need to use Pattern/Matcher style; just direct with regexp adjustable here ..
    private static final String more_than_one_space_regexp = "\\s+";
    private static final String first_schedule = "JADUAL PERTAMA";
    private static final Pattern first_schedule_pattern = Pattern.compile(first_schedule);
    private static final String second_schedule = "JADUAL KEDUA";
    private static final Pattern second_schedule_pattern = Pattern.compile(second_schedule);
    private static final String third_schedule = "JADUAL KETIGA";
    private static final Pattern third_schedule_pattern = Pattern.compile(third_schedule);
    private static final String PAR_regexp = "PERSEKUTUAN.*?P.*?(\\d+).*?([\\w\\s]+).*?";
    private static final Pattern PAR_regexp_pattern = Pattern.compile(PAR_regexp);
    private static final String DUN_regexp = "N\\s*?\\.\\s*?(\\d+).*?([\\w’'\\s]+?)\\s*?(\\d+\\..*)";
    private static final Pattern DUN_regexp_pattern = Pattern.compile(DUN_regexp);
    private static final String DUN_regexp_loose = "N\\..*?(\\d+).*";
    private static final Pattern DUN_regexp_loose_pattern = Pattern.compile(DUN_regexp_loose);
    private static final String DM_regexp = "(\\d+?)\\s*?\\.\\s*?([\\w’'\\s]+?)\\s*?(\\d[,\\d]+).*?";
    private static final Pattern DM_regexp_pattern = Pattern.compile(DM_regexp);
    private static final String DM_regexp_loose = "";
    private static final String population_total_regexp = "\\s+\\d+";

    public static boolean isStartOfSchedule(String raw_content) {

        if (first_schedule_pattern.matcher(raw_content).find()) {
            out.println("Found FIRST SCHEDULE!!" + raw_content.matches(first_schedule));
            ECRedelineation.currentScheduleBlock = 1;
            return true;
        }
        if (second_schedule_pattern.matcher(raw_content).find()) {
            out.println("Found SECOND SCHEDULE!!");
            ECRedelineation.currentScheduleBlock = 2;
            return true;
        }
        if (third_schedule_pattern.matcher(raw_content).find()) {
            out.println("Found THIRD SCHEDULE!!");
            ECRedelineation.currentScheduleBlock = 3;
            return true;
        }
        // Nothing matched; is just an ordinary page ..
        return false;
    }

    public static boolean isStartOfPAR(String single_line_of_content) {
        Matcher PAR_regexp_pattern_matched = PAR_regexp_pattern.matcher(single_line_of_content);
        if (PAR_regexp_pattern_matched.find()) {
            out.println("CODE:" + PAR_regexp_pattern_matched.group(1)
                    + " NAME:" + PAR_regexp_pattern_matched.group(2));
            return true;
        }
        // Extract out PAR Code 
        // Update the current PAR Code label
        return false;

    }

    public static boolean isStartOfDUN(String single_line_of_content) {
        // Init the Matchers
        Matcher DUN_regexp_pattern_matched = DUN_regexp_pattern.matcher(single_line_of_content);
        Matcher DUN_regexp_loose_pattern_matched = DUN_regexp_loose_pattern.matcher(single_line_of_content);

        // Matching rules below
        if (DUN_regexp_pattern_matched.find()) {
            // DEBUG:
            /*
             out.println("CODE:" + DUN_regexp_pattern_matched.group(1)
             + " NAME:" + DUN_regexp_pattern_matched.group(2)
             + " LEFTOVER:" + DUN_regexp_pattern_matched.group(3));
             */

            extractDataOfDM(DUN_regexp_pattern_matched.group(3));
            return true;
        } else if (DUN_regexp_loose_pattern_matched.find()) {
            // Abnormalities; note it down for future correction
            out.println("ERROR: Needed a loose match for CODE: " + DUN_regexp_loose_pattern_matched.group(1));
            out.println("PROB_LINE:" + single_line_of_content);
            // Leave in Map for further analysis
            ECRedelineation.error_while_parsing.put(
                    "N" + DUN_regexp_loose_pattern_matched.group(1), 
                    DUN_regexp_loose_pattern_matched.group(3));
            // If leftover  is number; add as population; can assume 01 as DM code 
            //      leave name as UNKNOWN
            // else if words; then can assume 01 as DM code
            //      leave population as 0
        }
        // Need for a more flexible match as well ..
        // Extract out DUN Code and also first line of DM for downstream processing
        // Update the current DUN Code label
        return false;

    }

    public static boolean containsDMData(String single_line_of_content) {
        // Use a strict focus ..
        // Need for a more flexible match as well ..
        return false;

    }

    public static boolean extractDataOfDM(String single_line_of_content) {
        Matcher DM_regexp_pattern_matched = DM_regexp_pattern.matcher(single_line_of_content);
        if (DM_regexp_pattern_matched.find()) {
            out.println("CODE:" + DM_regexp_pattern_matched.group(1)
                    + " NAME:" + DM_regexp_pattern_matched.group(2)
                    + " POPULATION:" + DM_regexp_pattern_matched.group(3));
            return true;
        }
        // Detect and attach to last found DUN ..
        return false;

    }

    private static void normalizeCode(String raw_code_number) {
        // Fixed to double digit ..
    }

}
