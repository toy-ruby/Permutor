/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reduc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Reduc {

    // Holding list
    static SortedSet<String> list = new TreeSet<String>();
    static String[] strs;
    
    public Reduc(String str, int r_val){
        doReduc(str, r_val);
        
    }

    public final void doReduc(String str, int r_val)
            throws NoSuchElementException {
        String new_str;
        new_str = str;

        for (int x = 0; x < str.length(); ++x) {
            new_str = str.substring(0, x) + str.substring(x + 1, str.length());
            // System.err.print(new_str + "\n");
            if (new_str.length() > r_val) {
                doReduc(new_str, r_val);
            }
            try {
                if (!new_str.equals(list.last())) {
                    list.add(new_str);
                }
            } catch (NoSuchElementException e) {
                list.add(new_str);
            }
           
        }
        strs = list.toArray(new String[0]);
    }

    static boolean save() {
        StringBuilder sb = new StringBuilder();
        
        
        for (String s : strs) {

            sb.append(s).append("\n");
        }

        File saveFile = new File("/Users/user0/Desktop/saveFile.csv");

        // Save the file
        try {

            FileWriter fw = new FileWriter(saveFile);
            PrintWriter pw = new PrintWriter(fw);
            String csvString = sb.toString();
            pw.print(csvString);
            fw.close();

        } catch (IOException ioe) {
            System.err.print("Error: " + ioe + "\n");
            return false;
        }
        return true;
    }
    
    public String[] getArray(){
        Arrays.sort(strs, new StringLengthComparator());
        return strs;
    }


}

class StringLengthComparator implements
        Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        if (o1.length() > o2.length()) {
            return -1;
        } else {
            if (o1.length() < o2.length()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
