package marytts.data;

import java.util.ArrayList;

/**
 * 
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */

public class Phrase
{
    private ArrayList<Word> tokens;

    public Phrase(ArrayList<Word> tokens)
    {
        this.tokens = tokens;
    }

    public ArrayList<Word> getWords()
    {
        return tokens;
    }
    
}
