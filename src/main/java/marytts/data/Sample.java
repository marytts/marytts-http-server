package marytts.data;

import java.util.ArrayList;

/**
 * 
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */

public class Sample
{
    private ArrayList<Phrase> phrases;

    public Sample(ArrayList<Phrase> phrases)
    {
        this.phrases = phrases;
    }

    public ArrayList<Phrase> getPhrases()
    {
        return phrases;
    }
}
