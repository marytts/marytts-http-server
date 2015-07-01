package marytts.data;

import java.util.ArrayList;

/**
 * 
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class Token
{
    private ArrayList<Syllable> syllables;
    private String part_of_speech;

    public Token(ArrayList<Syllable> syllables, String part_of_speech)
    {
        this.syllables = syllables;
        this.part_of_speech = part_of_speech;
    }

    public ArrayList<Syllable> getSyllables()
    {
        return syllables;
    }

    public String getPartOfSpeech()
    {
        return part_of_speech;
    }
}
