package marytts.data;

import java.util.ArrayList;

/**
 * 
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */

public class Phrase
{
    private ArrayList<Token> tokens;

    public Phrase(ArrayList<Token> tokens)
    {
        this.tokens = tokens;
    }

    public ArrayList<Token> getTokens()
    {
        return tokens;
    }
    
}
