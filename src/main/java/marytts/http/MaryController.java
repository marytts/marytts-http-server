package marytts.http;

/* RESTFULL / HTTP part */
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/* Utils */
import java.util.ArrayList;
import java.util.Locale;

/* IO */
import java.io.InputStream;
import java.io.IOException;

/* Audio */
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;

/* MaryTTS */
import marytts.LocalMaryInterface;
import marytts.modules.synthesis.Voice;
import marytts.util.MaryUtils;

/**
 *  Mary RESTFUL controller class
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
@RestController
public class MaryController
{    
    private AudioInputStream ais;
    private LocalMaryInterface local_mary;

    public MaryController() throws Exception
    {
        local_mary = new LocalMaryInterface();
    }

    /**************************************************************************
     ** Listings
     **************************************************************************/
    @RequestMapping("/listVoices")
    public MaryListResponse listvoices(@RequestParam(value="locale", defaultValue="en_US")
                                       String locale)
        throws Exception
    {
        ArrayList<String> result = new ArrayList<String>();
        Locale locale_obj = MaryUtils.string2locale(locale);
        
        // List voices and only retrieve the names
        for (Voice v: Voice.getAvailableVoices(locale_obj)) {
            result.add(v.getName());
        }

        return new MaryListResponse(result, null, false);
    }

    
    @RequestMapping("/listLocales")
    public MaryListResponse listlocales()
        throws Exception
    {
        ArrayList<String> result = new ArrayList<String>();
        
        // List voices and only retrieve the names
        for (Locale l: local_mary.getAvailableLocales()) {
            result.add(l.toString());
        }

        return new MaryListResponse(result, null, false);
    }

    @RequestMapping("/listInputTypes")
    public MaryListResponse listInputTypes()
        throws Exception
    {
        ArrayList<String> result = new ArrayList<String>();
        
        // List voices and only retrieve the names
        for (String t: local_mary.getAvailableInputTypes())
        {
            result.add(t);
        }

        return new MaryListResponse(result, null, false);
    }

    @RequestMapping("/listOutputTypes")
    public MaryListResponse listOutputTypes()
        throws Exception
    {
        ArrayList<String> result = new ArrayList<String>();
        
        // List voices and only retrieve the names
        for (String t: local_mary.getAvailableOutputTypes())
        {
            result.add(t);
        }

        return new MaryListResponse(result, null, false);
    }
    
    /**************************************************************************
     ** Getters
     **************************************************************************/
    @RequestMapping("/getCurrentLocale")
    public MaryResponse getCurrentLocale()
    {
        return new MaryResponse(local_mary.getLocale().toString(), null, false);
    }
    
    @RequestMapping("/getCurrentVoice")
    public MaryResponse getCurrentVoice()
    {
        return new MaryResponse(local_mary.getVoice(), null, false);
    }
    
    /**************************************************************************
     ** Setters
     **************************************************************************/
    @RequestMapping("/setLocale")
    public void setLocale(@RequestParam(value="locale") String locale) {
        local_mary.setLocale(MaryUtils.string2locale(locale));
    }

    
    @RequestMapping("/setVoice")
    public void setVoice(@RequestParam(value="voice") String voice) {
        local_mary.setVoice(voice);
    }


    @RequestMapping("/setInputType")
    public void setInputType(@RequestParam(value="type") String type) {
        local_mary.setInputType(type);
    }

    
    @RequestMapping("/setOutputType")
    public void setOutputType(@RequestParam(value="type") String type) {
        local_mary.setOutputType(type);
    }

    @RequestMapping("/setOutputLevel")
    public void setOutputLevel(@RequestParam(value="level") String level) {
    }

    
    
    /**************************************************************************
     ** Process (except synthesis)
     **************************************************************************/
    @RequestMapping("/process")
    public MaryResponse process(@RequestParam(value="input") String input)
        throws Exception
    {
        if (local_mary.isAudioType(local_mary.getOutputType()))
        {
            //ais = local_mary.generateAudio(text);
            
            return new MaryResponse(null, null, true);
        }
        else if (local_mary.isTextType(local_mary.getOutputType()))
        {
        }
        else if (local_mary.isXMLType(local_mary.getOutputType()))
        {
        }
        else
        {
            throw new Exception("Unknown kind of type");
        }

        return null;
    }


    
    /**************************************************************************
     ** Synthesis
     **************************************************************************/
    @RequestMapping("/synthesize")
    public MaryResponse synthesize(@RequestParam(value="text") String text)
        throws Exception
    {
        ais = local_mary.generateAudio(text);
        return new MaryResponse(null, null, true);
    }

    @RequestMapping("/getSynthesizedSignal")
    public void getFile(HttpServletResponse response)
    {
        try
        {
            response.setContentType("audio/x-wav");
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, response.getOutputStream());
            response.flushBuffer();
        }
        catch (IOException ex)
        {
            throw new RuntimeException("IOError writing file to output stream");
        }

    }   
}
