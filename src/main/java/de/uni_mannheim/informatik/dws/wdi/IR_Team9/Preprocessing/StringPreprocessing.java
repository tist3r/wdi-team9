package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing;

import java.util.StringTokenizer;

public class StringPreprocessing {


    static String[] FREQUENT_TOKENS = {
        "bank", "group", "of", "and",
        "holding", "energy", "financial",
        "international", "industries", "inc", "incorporated",
        "corporation", "corp", "communications", "technologies",
        "holdings", "chemical", "motor", "motors", "the", "co",
        "pharmaceutical", "pharmaceuticals", "ltd", "limited", 
        "sa", "ag"
    };

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

    public static String removePunctuation(String token, String exclude){
        String removeRegex = "[\\p{Punct}]";

        if (!exclude.matches("")){
            //TODO improve performance
            removeRegex = "[\\p{Punct}&&[^"+exclude+"]]";
        }

        return token.replaceAll(removeRegex, "");
    }

    public static String removeFrequentToken(String s, boolean removePunctuation){
        if(removePunctuation){
            s = removePunctuation(s, "");
        }

        return removeFrequentToken(s);
    }

    
    public static String removeFrequentToken(String s){
        StringTokenizer tokenizer = new StringTokenizer(s);
        String token;
        StringBuffer sb = new StringBuffer();
        boolean isFrequentToken;
        
        while(tokenizer.hasMoreTokens()){
            isFrequentToken = false;
            token = tokenizer.nextToken();

            for(String ft : FREQUENT_TOKENS){
                if(ft.equalsIgnoreCase(token)){
                    isFrequentToken = true;
                    break;
                }
            }

            if(!isFrequentToken){
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
