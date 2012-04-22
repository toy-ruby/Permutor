// File: Permute.java

package permute;

import java.util.ArrayList;
import java.util.List;

public class Permute {

    private String orig = "";
    private String perm = "";
    private List<String> holdList = new ArrayList<String>();

    public Permute( String str ) {
        orig = str;
        this.generate( holdList, orig, perm );
    }

    private void generate( List<String> list, String orig, String perm ) {

        if (orig.isEmpty()) {

            list.add( perm );
            return;
        }

        for (int i = 0; i < orig.length(); ++i) {
            String orig2 = orig;
            String perm2 = perm;
            orig2 = orig2.substring( 0, i ) +
                    orig2.substring( i + 1, orig2.length() );
            perm2 += orig.charAt( i );

            this.generate( list, orig2, perm2 );
        }
    }

    public List getList() {

        return holdList;
    }
}

