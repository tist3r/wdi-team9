package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing;

public class StringPreprocessing {

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
        String removeRegex = "[\\p{Punct}&&[^"+exclude+"]]";
        return token.replaceAll(removeRegex, "");
    }

    public static String removeWhitespaces(String token){
        String removeRegex = "[\\s]";
        return token.replaceAll(removeRegex, "");
    }

    public static String removePunctuationWhitespaces(String token, String exclude){
        String removeRegex = "[\\s\\p{Punct}&&[^"+exclude+"]]";
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
        url = url.replaceAll("((http|https)://)?(www.)?", "");
        return url.replaceAll("(\\.[a-z]*/?)", "").toLowerCase();
    }

    
    // public static void main(String[] args) {
    //     String token = "www.mydays.com";
    //     System.out.println(normalizeURL(token));
    // }
    
}
