import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// you can use System.out.println for debugging purposes, e.g.
// System.out.println("this is a debug message");

class Solution {
  
        public int solution(int[] A) {
            int sum = 0;
            List<Integer> difflist=new ArrayList();
            int sumleft=0;
            for (int i = 0; i < A.length - 1; i++) {sum+=A[i];}
            for (int i = 0; i < A.length - 1; i++) {
            int sumright = sum - sumleft;
            int difference = Math.abs(sumleft-sumright);
                difflist.add(difference);
            sumleft += A[i];  
         
            }
            int minIndex = difflist.indexOf(Collections.min(difflist));
            return minIndex-1;
        }

}