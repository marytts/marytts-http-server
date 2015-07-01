package marytts.data;

/**
 * 
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */

public class Phone
{
    private String label;
    private int duration;
    
    public Phone(String label, int duration)
    {
        this.label = label;
        this.duration = duration;
    }

    public String getLabel()
    {
        return label;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }
}
