package marytts.http;

import java.util.List;
import java.io.IOException;


public class MaryListResponse
{
    private final List<String> result;
    private final String log;
    private final boolean synth_done;

    public MaryListResponse(List<String> result, String log, boolean synth_done)
    {
        this.result = result;
        this.log = log;
        this.synth_done = synth_done;
    }

    public List<String> getResult()
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
