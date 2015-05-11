package marytts.http;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;


public class MaryResponse
{
    private final String result;
    private final String log;
    private final boolean synth_done;

    public MaryResponse(String result, String log, boolean synth_done)
    {
        this.result = result;
        this.log = log;
        this.synth_done = synth_done;
    }

    public String getResult()
    {
        return result;
    }

    public String getLog()
    {
        return log;
    }

    public boolean isSynthDone()
    {
        return synth_done;
    }
}
