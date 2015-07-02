package marytts.data;

import java.util.ArrayList;

/**
 * 
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */

public class Phrase
{
    private int break_index;
    private int end_pause_duration;
    private String tone;
    private ArrayList<Token> tokens;

    public Phrase(ArrayList<Token> tokens)
    {
        this.tokens = tokens;
        this.break_index = -1;
        this.end_pause_duration = 0;
        this.tone = "";
    }

    public Phrase(ArrayList<Token> tokens, int break_index, int end_pause_duration, String tone)
    {
        this.tokens = tokens;
        this.break_index = break_index;
        this.end_pause_duration = end_pause_duration;
        this.tone = tone;
    }

    public int getBreakIndex()
    {
        return break_index;
    }

    public int getEndPauseDuration()
    {
        return end_pause_duration;
    }

    public String getTone()
    {
        return tone;
    }
    
    public ArrayList<Token> getTokens()
    {
        return tokens;
    }
    
}
