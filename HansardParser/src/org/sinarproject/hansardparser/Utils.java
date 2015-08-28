/*
 * Put the misc Utils that can be used here ...
 */
package org.sinarproject.hansardparser;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author leow
 */
public class Utils {

    // Patterns for the various regexp used
    private static final String more_than_one_space_regexp = "\\s+";
    private static final Pattern pattern_more_than_one_space = Pattern.compile(more_than_one_space_regexp);
    // above try the private static final pattern ..
    private static final String disallow_number_dot_speakers_regexp = "";
    private static final Pattern pattern_illegal_speaker = Pattern.compile(disallow_number_dot_speakers_regexp);
    private static final String not_word_number_topic_regexp = "[^\\w\\d\\s]";
    private static final Pattern pattern_illegal_topic = Pattern.compile(not_word_number_topic_regexp);
    private static final String match_speaker_regexp = "";
    private static final String match_speaker_alt_regexp = "";
    private static final String match_timestamp_regexp = "";
    // Below are the data structures for maintainign the final mapping for use outside ..
    // HansardComplete['Speakers'] --> {  [name:'Speaker1', name:'Speaker2']}
    private static List<String> all_speakers_who_talked;
    // HansardComplete['Topic Title']['Speakers'] --> {  [name:'Speaker1', name:'Speaker2']}
    private static Map<String, List<String>> speakers_per_topic;
    // HansardComplete['Topic Title']['Log'] --> SpeechBlock1 --> SpeechBlock2 --> Timestamp .. --> ..
    private static Map<String, List<String>> speakers_log_per_topic;

    public static String getTopicbyPageNumber(int current_page, Map<Integer, List<String>> myHalamanHash) {
        List<String> myTopicList = myHalamanHash.get(current_page);
        String sanitized_encoded_topic = null;
        for (String myTopic : myTopicList) {
            // For now we assume the last it list; pop??
            sanitized_encoded_topic = Utils.cleanTopicTitle(myTopic);
        }

        return sanitized_encoded_topic;

    }

    public static void getTopicStats(String Topic) {
        // Make sure topic is standardized ...
        // Count number of speakers
        // Utils.speakers_per_topic.size();
    }

    public static List<String> getMergedSpeakerList() {
        return null;

    }

    public static List<String> getMergedSpeakerListbyTopic(String topic) {
        // clean first to standard to make sure we can pull the right index ..
        return null;

    }

    public static void mergeSpeakerList(List<String> speaker_list) {

    }

    public static void mergeSpeakerListbyTopic(List<String> speaker_list, String topic) {
        // clean first to standard to make sure we can put in the right index ..

    }

    public static void mergeLogbyTopic(String speaker_name, String speech_block, String topic) {
        // clean first to standard to make sure we can put in the right index ..

    }
    
    public static boolean hasContentfromPreviousTopic(String current_topic_title) {
        // Ignore the DR pattern; and see after that ..
        // Flatten content; before trying a match ..
        // by default assumes NO content from Previous Topic ..
        return false;
    }

    // pattern matching of speaker
    // apttern matching of alt speaker 
    // Match timestamp ..
    // clean up of title
    private static String cleanTopicTitle(String raw_topic_title) {
        // remove chars not allowed
        Matcher matched_illegal_topic = pattern_illegal_topic.matcher(raw_topic_title);
        // apply a trim
        // remove extra spaec ebecome one; merge with below ..
        // replace all space with underscore so can be used as filename
        Matcher matched_extra_space = pattern_more_than_one_space.matcher(
                matched_illegal_topic.replaceAll("").trim()
        );
        // For topic leave space with _ for use in filename ..
        return matched_extra_space.replaceAll("_");

    }

    // clean up of speakers??
    private static String cleanSpeakersName(String raw_speakers_name) {
        // remove chars not allowed
        Matcher matched_illegal_speaker = pattern_illegal_speaker.matcher(raw_speakers_name);
        // apply a trim
        // remove extra spaec ebecome one
        Matcher matched_extra_space = pattern_more_than_one_space.matcher(
                matched_illegal_speaker.replaceAll("").trim()
        );
        // For speakers replace with space ...
        return matched_extra_space.replaceAll(" ");

    }

    // Keeping track of overall speakers stats
    // Scenarios:
    // a) Who spoke in this session? Semi-correct attendance for those who appeared ..
    // b) How many speech blocks this session?  Does not seem too useful; and not 
    //      very accurate
    // HansardComplete['Speakers'] --> {  [name:'Speaker1', name:'Speaker2']}
    // Keeping track of speech per issue
    // Scenarios:
    // a) Who spoke for this topic?  Possible has interest or is in a commitee for it ..
    // b) How many speechblocks per speaker in this topic??
    // c) Sentiment analysis
    // d) How many BN/Opposition talk; ration?  Might not be too useful in terms 
    //      of raw speech block tho ..
    // HansardComplete['Topic Title']['Speakers'] --> {  [name:'Speaker1', name:'Speaker2']}
    // HansardComplete['Topic Title']['Log'] --> SpeechBlock1 --> SpeechBlock2 --> Timestamp .. --> ..
    // HansardComplete['Topic Title']['Speaker1'] --> "{ [talked:12, asked:1, objected:5 ] }"
    // HansardComplete['Topic Title']['Speaker2'] --> "{ [talked:12, asked:1, objected:5 ] }"
    // HansardComplete['Topic Title']['Incumbent'] --> "{ [talked:19, asked:10, objected:9 ] }"
    // HansardComplete['Topic Title']['Opposition'] --> "{ [talked:120, asked:111, objected:50 ] }"
}
