package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class StringPreprocessing {

    private static String[] _FREQUENT_TOKENS_1 = {
        "bank", "group", "of", "and",
        "holding", "energy", "financial",
        "international", "industries", "inc", "incorporated",
        "corporation", "corp", "communications", "technologies",
        "holdings", "chemical", "motor", "motors", "the", "co",
        "pharmaceutical", "pharmaceuticals", "ltd", "limited", 
        "sa", "ag"
    };

    private static String[] _FREQUENT_TOKENS_2 = {
        "entertainment", "services", "company",
        "technology", "insurance", "management",
        "engineering", "construction", "solution", "solutions",
        "manufacturing", "investment", "investments", "development",
        "plc", "bv", "llc", "telecommunications", "telecommunication",
        "national", "capital", "china", "de", "consulting", "management"
    };

    //set that keeps frequent tokens for better performance
    static final Set<String> FREQUENT_TOKENS = makeFrequentTokenSet(_FREQUENT_TOKENS_1, _FREQUENT_TOKENS_2);


    private static Set<String> makeFrequentTokenSet(String[] tokens1, String[] tokens2){
        Set<String> freqTokens = new HashSet<>();

        for(String ft : tokens1){
            freqTokens.add(ft);
        }

        for(String ft : tokens2){
            freqTokens.add(ft);
        }

        return freqTokens;
    }

    /** 
     * @param token the token that is to be nomralized
     * @param upper indicated if token should be converted to uppercase; default lower
     * 
     * @return normalized token
    */
    public static String normalizeCase(String token, boolean upper){
        if(upper){
            return token.toUpperCase();
        }

        return token.toLowerCase();
    }

    /**
     * Removes punctuation from a String.
     * @param token the string to be processed
     * @param exclude symbols that should not be removed. If all punctuation should be removed put "".
     * @return the preprocessed string
     */
    public static String removePunctuation(String token, String exclude){
        String removeRegex = "[\\p{Punct}]";

        if (exclude.matches(removeRegex)){
            removeRegex = "[\\p{Punct}&&[^"+exclude+"]]";
        }else if(exclude.length() == 0){}
        else{
            System.out.println(String.format("exclude String %s not valid - contains other things than punctuations. Removing all punctuation ...", exclude));
        }

        return token.replaceAll(removeRegex, "");
    }

    /**
     * Removes punctuation from a String.
     * @param token the string to be processed
     * @return the preprocessed string
     */
    public static String removePunctuation(String token){
        return removePunctuation(token, "");
    }

    /**
     * Removes frequent tokens from a give String. Before Punctuation can be removed.
     * @param s
     * @param removePunctuation
     * @return
     */
    public static String removeFrequentTokens(String s, boolean removePunctuation){
        if(removePunctuation){
            s = removePunctuation(s, "");
        }

        return removeFrequentTokens(s);
    }

    
    /**
     * Removes frequent tokens from a string (split by whitespace)
     * Lowercasing is done by the function
     * @param s name (does not have to be lowercased)
     * @return
     */
    public static String removeFrequentTokens(String s){
        /*
         * Starts from an empty Stringbuffer and appends all tokens to it (split by whitespace) that do not match a frequent token.
         */

        StringTokenizer tokenizer = new StringTokenizer(s);
        String token;
        StringBuffer sb = new StringBuffer();
        
        while(tokenizer.hasMoreTokens()){
            // isFrequentToken = false;
            token = tokenizer.nextToken();
            token = token.toLowerCase();

            if(!FREQUENT_TOKENS.contains(token)){ //token is not a frequent token
                sb.append(token);
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    public static String removeWhitespaces(String token){
        String removeRegex = "[\\s]";
        return token.replaceAll(removeRegex, "");
    }

    public static String removePunctuationWhitespaces(String token, String exclude){
        String removeRegex = "[\\s\\p{Punct}]";

        if (!exclude.matches("")){
            //TODO improve performance
            removeRegex = "[\\s\\p{Punct}&&[^"+exclude+"]]";
        }
        
        return token.replaceAll(removeRegex, "");
    }

    public static String removeRegex(String token, String removeRegex){
        return token.replaceAll(removeRegex, "");
    }

    public static String tokenBasicNormalization(String token, String exclude, boolean upper){
        return normalizeCase(removePunctuationWhitespaces(token, exclude), upper);
    }

    /*
     * Taken from: https://www.educative.io/answers/how-to-generate-an-n-gram-in-java
     */
    public static List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList<String>();
        for (int i = 0; i < str.length() - n + 1; i++)
            // Add the substring or size n
            ngrams.add(str.substring(i, i + n));
            // In each iteration, the window moves one step forward
            // Hence, each n-gram is added to the list
    
        return ngrams;
    }


    //Url methods
    public static String normalizeURL(String url){
        if (url == null){return "";} //cater for empty urls

        url = url.replaceAll("((http|https)://)?(www.)?", "");
        return url.replaceAll("(\\.[a-z]*/?)", "").toLowerCase();
    }

    
    // public static void main(String[] args) {
    //     String token = "www.mydays.com";
    //     System.out.println(normalizeURL(token));
    // }
    
}
