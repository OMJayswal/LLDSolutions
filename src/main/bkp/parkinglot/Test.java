package com.test;

import java.util.*;

public class Test {

    public static boolean canBePartitionedInKSubsets(Integer[] nums,int k){
        int n = nums.length;
        int sum = 0;
        for(int num:nums){
            sum = sum+num;
        }

        if(sum%k !=0)
            return false;
        int target = sum/k;
        Arrays.sort(nums,Collections.reverseOrder());
        if(nums[n-1] > target)
            return false;

        Boolean[] dp = new Boolean[n];
        for(int i=0;i<n;i++){
            dp[i] = false;
        }

        int buckets[]= new int[k];

        for(int i=0;i<n;i++){
            for(int j=0;j<k;j++){
                if(buckets[j]+nums[i]<=target) {
                    buckets[j]= buckets[j]+nums[i];
                    break;
                }
            }
        }
        int res = 0;
        for(int num:buckets){
            res = res+num;
        }
        if(res == sum)
        return true;
        return false;
    }

    public static boolean canStringBeFormed(String s, List<String> dict){
        Set<String> dictSet = new HashSet<>(dict);
        boolean[] dp = new boolean[s.length()+1];
        dp[0] = true;
        for(int i=1;i<=s.length();i++){
            for(int j=0;j<i;j++){
                if(dp[j] && dictSet.contains(s.substring(j,i))){
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[s.length()];

    }
    public static void main(String[] args){
        Integer nums[] = {4,3,2,3,5,2,1};
        int k= 4;
        System.out.println(canBePartitionedInKSubsets(nums,k));
        List<String> dict = Arrays.asList("hello","world");
        String s = "helloworld";
        List<String> dict2 = Arrays.asList("apple", "pen", "pine", "pineapple");
        String s2 = "pineapplepenapple";
        String s3 = "catsandog";
        List<String> dict3 = Arrays.asList("cats","dog","sand","and","cat");
        System.out.println(canStringBeFormed(s,dict));
        System.out.println(canStringBeFormed(s2,dict2));
        System.out.println(canStringBeFormed(s3,dict3));
    }

}


