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
    private AudioInputStream ais; /*< Synthesized audio stream saved to be accessed through getSynthesizedSignal */
    private LocalMaryInterface local_mary; /*< Interface to the MaryTTS system */

    /**
     *  Constructor. Just create an interface to MaryTTS 
     *
     *    @throws Exception in case of the creation of the interface failed
     */
    public MaryController() throws Exception
    {
        ais = null;
        local_mary = new LocalMaryInterface();
    }

    /**************************************************************************
     ** Listings
     **************************************************************************/

    /**
     *  Method used to list available voices for a given locale in the current MaryTTS instance
     *
     *    @param locale the locale (if not given, the default locale is en_US)
     *    @return a MaryListResponse object where result field contains the list of voices
     *    @throws Exception in the case of the listing is failing
     */
    @RequestMapping("/listVoices")
    public MaryListResponse listVoices(@RequestParam(value="locale", defaultValue="en_US")
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

    /**
     *  Method used to list available locales in the current MaryTTS instance
     *
     *    @return a MaryListResponse object where result field contains the list of locales
     *    @throws Exception in the case of the listing is failing
     */    
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

    /**
     *  Method used to list available input types in the current MaryTTS instance
     *
     *    @return a MaryListResponse object where result field contains the list of input types
     *    @throws Exception in the case of the listing is failing
     */  
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

    /**
     *  Method used to list available output types in the current MaryTTS instance
     *
     *    @return a MaryListResponse object where result field contains the list of output types
     *    @throws Exception in the case of the listing is failing
     */  
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
    /**
     *  Method used to get the current locale name
     *
     *    @return a MaryResponse object where result field contains the current locale name
     */  
    @RequestMapping("/getCurrentLocale")
    public MaryResponse getCurrentLocale()
    {
        return new MaryResponse(local_mary.getLocale().toString(), null, false);
    }
    
    /**
     *  Method used to get the current voice name
     *
     *    @return a MaryResponse object where result field contains the current voice name
     */    
    @RequestMapping("/getCurrentVoice")
    public MaryResponse getCurrentVoice()
    {
        return new MaryResponse(local_mary.getVoice(), null, false);
    }
    
    /**************************************************************************
     ** Setters
     **************************************************************************/
    /**
     *  Method used to set the current locale
     *
     *    @param locale the new locale to set (format is the standard one like en_US for example)
     *    @throws Exception in case of unexisting local
     */  
    @RequestMapping("/setLocale")
    public void setLocale(@RequestParam(value="locale") String locale)
        throws Exception
    {
        local_mary.setLocale(MaryUtils.string2locale(locale));
    }

    
    /**
     *  Method used to set the current voice
     *
     *    @param voice the name of the new voice
     *    @throws Exception in case of unexisting voice
     */  
    @RequestMapping("/setVoice")
    public void setVoice(@RequestParam(value="voice") String voice)
        throws Exception
    {
        local_mary.setVoice(voice);
    }


    /**
     *  Method used to set the current input type
     *
     *    @param type the name of the new input type
     *    @throws Exception in case of unexisting type or if type is not an input one
     */  
    @RequestMapping("/setInputType")
    public void setInputType(@RequestParam(value="type") String type) {
        local_mary.setInputType(type);
    }

    
    /**
     *  Method used to set the current output type
     *
     *    @param type the name of the new output type
     *    @throws Exception in case of unexisting type or if type is not an output one
     */  
    @RequestMapping("/setOutputType")
    public void setOutputType(@RequestParam(value="type") String type) {
        local_mary.setOutputType(type);
    }

    /**
     *  Method used to set the current level of logger
     *
     *    @param level the name of the new log level
     *    @throws Exception in case of unexisting type or if type is not an output one
     */  
    @RequestMapping("/setLoggerLevel")
    public void setLoggerLevel(@RequestParam(value="level") String level)
    {
        throw new UnsupportedOperationException("Not implemented yet !");
    }

    
    
    /**************************************************************************
     ** Process (except synthesis)
     **************************************************************************/
    /**
     *  Method used to process a text-based input considering the current configuration of MaryTTS
     *
     *    @param input the input in a text-based format (XML is detected otherwise everything is considered as a text)
     *    @return MaryResponse the response where the result field contains the result information
     *    @throws Exception in case of failing (possible failing are invalid types, bad input value, ...)
     */
    @RequestMapping("/process")
    public MaryResponse process(@RequestParam(value="input") String input)
        throws Exception
    {

        // Deal with input type

        // Deal with output type
        
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
    /**
     *  Main entry point method : synthesis of a given text. It doesn't provide the signal but needs
     *  to be called first. If the call succeed, the method {@link
     *  getSynthesizedSignal(HttpServletResponse)} should be call to retrieve the signal
     *
     *    @param text : the text to synthesize
     *    @return MaryResponse the response where the result field contains null and synthDone
     *    contains true to indicate that the synthesis as succeded
     *    @throws Exception in case of failing (text is empty, ...)
     */
    @RequestMapping("/synthesize")
    public MaryResponse synthesize(@RequestParam(value="text") String text)
        throws Exception
    {
        ais = local_mary.generateAudio(text);
        return new MaryResponse(null, null, true);
    }

    
    /**
     * Method to retrieve a signal already synthesized using the method {@link synthesize(String)} or the method {@link process(String)}
     *
     *    @param response the response to fill
     *    @throws Exception in case of failing (no synthesis called before, ...)
     */
    @RequestMapping("/getSynthesizedSignal")
    public void getFile(HttpServletResponse response)
        throws Exception
    {
        if (ais == null)
        {
            throw new RuntimeException("No synthesis achieved => no signal to get !")
        }
        
        response.setContentType("audio/x-wav");
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, response.getOutputStream());
        response.flushBuffer();
    }   
}
