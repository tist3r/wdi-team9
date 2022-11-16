package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import java.util.Set;
import java.util.stream.Collectors;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;

public class LongestCommonSubsequenceSimilarity extends SimilarityMeasure<String> {

    public enum NormalizationFlag {JACCARD, MIN, MAX, DICE, AVG, NONE};

    private NormalizationFlag normalizationFlag = NormalizationFlag.DICE;

    public LongestCommonSubsequenceSimilarity(NormalizationFlag normalizationFlag) {
        this.normalizationFlag = normalizationFlag;
    }



  /*Credit to: https://www.geeksforgeeks.org/longest-common-subsequence-dp-4/ */
    /**
   * Preprocessing is handeled by the calculate function
   */
    @Override
    public double calculate(String first, String second) {
        first = StringPreprocessing.tokenBasicNormalization(first, "", false);
        second = StringPreprocessing.tokenBasicNormalization(second, "", false);

        int m = first.length();
        int n = second.length();


        //System.out.println(String.format("calculating for %s %s", first, second));
        int[][] dp = new int[m + 1][n + 1];
        for(int i=0;i<m + 1;i++){
            for(int j=0;j<n + 1;j++){
                dp[i][j] = -1;
            }
        }


        return this.normalizeSim(lcs(first, second, m, n, dp), first, second);
    }

    /*Credit to: https://www.geeksforgeeks.org/longest-common-subsequence-dp-4/ */
  // A Top-Down DP implementation of LCS problem
  
  // Returns length of LCS for X[0..m-1], Y[0..n-1]
    static int lcs(String X,String Y,int m,int n,int[][] dp){
    
        if (m == 0 || n == 0)
        return 0;
    
        if (dp[m][n] != -1)
        return dp[m][n];
    
        if(X.charAt(m - 1) == Y.charAt(n - 1)){
        dp[m][n] = 1 + lcs(X, Y, m - 1, n - 1, dp);
        return dp[m][n];
        }
    
        dp[m][n] = Math.max(lcs(X, Y, m, n - 1, dp),lcs(X, Y, m - 1, n, dp));
        return dp[m][n];
    }


    private double normalizeSim(int lcs, String s1, String s2){
        switch(this.normalizationFlag){
            case DICE: return this.normalizeSimDice(lcs, s1, s2);

            case JACCARD: return this.normalizeSimJaccard(lcs, s1, s2);

            case MIN: return this.normalizeSimMin(lcs, s1, s2);

            case MAX: return this.normalizeSimMax(lcs, s1, s2);
            case AVG: return this.normalizeSimMax(lcs, s1, s2)/2 + this.normalizeSimMin(lcs, s1, s2)/2;

            case NONE: return lcs;
            
            default:
                return this.normalizeSimDice(lcs, s1, s2);
        }
    }

    private double normalizeSimDice(int lcs, String s1, String s2){
        return (2.0*lcs)/(s1.length() + s2.length());
    }

    private double normalizeSimJaccard(int lcs, String s1, String s2){
        //get individual characters
        Set<Character> chars1 = s1.chars().mapToObj(e->(char)e).collect(Collectors.toSet());
        Set<Character> chars2 = s2.chars().mapToObj(e->(char)e).collect(Collectors.toSet());

        chars1.addAll(chars2);


        return (1.0*lcs)/(chars1.size());
    }

    private double normalizeSimMin(int lcs, String s1, String s2){

        return (1.0*lcs)/(Math.min(s1.length(), s2.length()));
    }

    private double normalizeSimMax(int lcs, String s1, String s2){

        return (1.0*lcs)/(Math.max(s1.length(), s2.length()));
    }


    public static void main(String[] args) {
        LongestCommonSubsequenceSimilarity lcss1 = new LongestCommonSubsequenceSimilarity(LongestCommonSubsequenceSimilarity.NormalizationFlag.AVG);
        
        System.out.println(lcss1.calculate("Commercial National Financial", "National Commercial Bank"));
        System.out.println(lcss1.calculate("Royal Dutch Shell", "Shell"));
        System.out.println(lcss1.calculate("CAE Inc.","cae technology services inc"));

    }


    
}
