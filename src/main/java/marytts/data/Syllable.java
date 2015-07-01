package marytts.data;

import java.util.ArrayList;

/**
 * 
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class Syllable
{
    private ArrayList<Phone> phones;
    private boolean is_stressed;
    
    public Syllable(ArrayList<Phone> phones, boolean is_stressed)
    {
        this.phones = phones;
        this.is_stressed = is_stressed;
    }

    public ArrayList<Phone> getPhones()
    {
        return phones;
    }

    public boolean isStressed()
    {
        return is_stressed;
    }
}
