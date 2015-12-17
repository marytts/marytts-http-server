/**
 * Copyright 2015 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package marytts.http.controllers;

/* RESTFULL / HTTP part */
import marytts.http.response.MaryListResponse;
import marytts.http.response.MaryResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/* Utils */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

/* IO */
import java.io.ByteArrayInputStream;

/* Audio */
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;

/* MaryTTS */
import marytts.LocalMaryInterface;
import marytts.modules.synthesis.Voice;
import marytts.util.MaryUtils;

/* XML */
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;

/* Data */
import marytts.data.XML2Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *  Mary RESTFUL controller class
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
@RestController
// @Scope("session")
public class MaryController
{
    private String current_language;
    private AudioInputStream ais; /*< Synthesized audio stream saved to be accessed through getSynthesizedSignal */
    @Autowired
    private LocalMaryInterface localMary; /*< Interface to the MaryTTS system */

    /**
     *  Constructor. Just create an interface to MaryTTS 
     *
     *    @throws Exception in case of the creation of the interface failed
     */
    public MaryController() throws Exception
    {
        ais = null;
        current_language = "en";    //set current language english to avoid exception in setRegion
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
    public MaryListResponse listVoices(@RequestParam(value="language", defaultValue="none") String language,
                                       @RequestParam(value="region", defaultValue="none") String region)
        throws Exception
    {
        Locale locale_obj;
        if (language.equals("none")) {
            locale_obj =  localMary.getLocale();
        }
        else
        {
            String locale = language;
            if (!region.isEmpty())
            {
                locale += "_" + region;
            }
            
            locale_obj = MaryUtils.string2locale(locale);
        }
        ArrayList<String> result = new ArrayList<String>();
        
        // List voices and only retrieve the names
        for (Voice v: Voice.getAvailableVoices(locale_obj)) {
            result.add(v.getName());
        }

        return new MaryListResponse(result, null, false);
    }


    /**
     *  Method used to list available languages in the current MaryTTS instance
     *
     *    @return a MaryListResponse object where result field contains the list of languages
     *    @throws Exception in the case of the listing is failing
     */    
    @RequestMapping("/listLanguages")
    public MaryListResponse listLanguages()
        throws Exception
    {
        HashSet<String> result = new HashSet<String>();
        
        // List voices and only retrieve the names
        for (Locale l: localMary.getAvailableLocales()) {
            String[] elts = l.toString().split("_");
            
            result.add(elts[0]);
        }

        return new MaryListResponse(new ArrayList<String>(result), null, false);
    }
    
    /**
     *  Method used to list available regions for a given language in the current MaryTTS instance
     *
     *    @param language the given language shortcut ("en", "de", ...)
     *    @return a MaryListResponse object where result field contains the list of regions
     *    @throws Exception in the case of the listing is failing
     */    
    @RequestMapping("/listRegions")
    public MaryListResponse listRegions(@RequestParam(value="language", defaultValue="en") String language)
        throws Exception
    {
        HashSet<String> result = new HashSet<String>();
        
        // List voices and only retrieve the names
        for (Locale l: localMary.getAvailableLocales()) {
            String[] elts = l.toString().split("_");
            if (elts.length < 2)
            {
                if (elts[0].equals(language))
                {
                    result.add(language.toUpperCase());
                }
            }
            else if (elts[0].equals(language)) {
                result.add(elts[1]);
            }
        }

        return new MaryListResponse(new ArrayList<String>(result), null, false);
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
        for (String t: localMary.getAvailableInputTypes())
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
        for (String t: localMary.getAvailableOutputTypes())
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
        return new MaryResponse(localMary.getLocale(), null, false);
    }

    /**
     *  Method used to get the current language name
     *
     *    @return a MaryResponse object where result field contains the current language name
     */  
    @RequestMapping("/getCurrentLanguage")
    public MaryResponse getCurrentLanguage()
    {
        // Init based on locale !
        if (current_language == null)
        {
            current_language = localMary.getLocale().toString().split("_")[0];
        }
        
        return new MaryResponse(current_language, null, false);
    }

    

    /**
     *  Method used to get the current region name
     *
     *    @return a MaryResponse object where result field contains the current region name
     */  
    @RequestMapping("/getCurrentRegion")
    public MaryResponse getCurrentRegion()
    {
        String[] elts = localMary.getLocale().toString().split("_");
        String current_region = elts[0].toUpperCase();
        if (elts.length > 1)
            current_region = elts[1];
        
        return new MaryResponse(current_region, null, false);
    }

    
    /**
     *  Method used to get the current voice name
     *
     *    @return a MaryResponse object where result field contains the current voice name
     */    
    @RequestMapping("/getCurrentVoice")
    public MaryResponse getCurrentVoice()
    {
        return new MaryResponse(localMary.getVoice(), null, false);
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
        localMary.setLocale(MaryUtils.string2locale(locale));
        String[] elts = localMary.getLocale().toString().split("_");
        current_language = elts[0];
    }

    
    /**
     *  Method used to set the current locale
     *
     *    @param locale the new locale to set (format is the standard one like en_US for example)
     *    @throws Exception in case of unexisting local
     */  
    @RequestMapping("/setLanguage")
    public void setLanguage(@RequestParam(value="language") String language)
        throws Exception
    {
        HashSet<String> result = new HashSet<String>();
        
        // List voices and only retrieve the names
        for (Locale l: localMary.getAvailableLocales())
        {
            String[] elts = l.toString().split("_");
            if (elts.length < 2)
            {
                if (elts[0].equals(language))
                {
                    localMary.setLocale(l);
                    break;
                }
            }
            else if (elts[0].equals(language))
            {
                localMary.setLocale(l);
                break;
            }
        }

        // FIXME: how to find the default region !

        current_language = language;
    }

    
    /**
     *  Method used to set the current locale
     *
     *    @param locale the new locale to set (format is the standard one like en_US for example)
     *    @throws Exception in case of unexisting local
     */  
    @RequestMapping("/setRegion")
    public void setRegion(@RequestParam(value="region") String region)
        throws Exception
    {
        String locale = current_language + "_" + region;
        localMary.setLocale(MaryUtils.string2locale(locale));
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
        localMary.setVoice(voice);
    }


    /**
     *  Method used to set the current input type
     *
     *    @param type the name of the new input type
     *    @throws Exception in case of unexisting type or if type is not an input one
     */  
    @RequestMapping("/setInputType")
    public void setInputType(@RequestParam(value="type") String type)
        throws Exception
    {
        localMary.setInputType(type);
    }

    
    /**
     *  Method used to set the current output type
     *
     *    @param type the name of the new output type
     *    @throws Exception in case of unexisting type or if type is not an output one
     */  
    @RequestMapping("/setOutputType")
    public void setOutputType(@RequestParam(value="type") String type)
        throws Exception
    {
        localMary.setOutputType(type);
    }

    /**
     *  Method used to set the current level of logger
     *
     *    @param level the name of the new log level
     *    @throws Exception in case of unexisting type or if type is not an output one
     */  
    @RequestMapping("/setLoggerLevel")
    public void setLoggerLevel(@RequestParam(value="level") String level)
        throws Exception
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
     *    @param inputType the inputType (can be null)
     *    @param outputType the outputType (can be null)
     *    @return MaryResponse the response where the result field contains the result information
     *    @throws Exception in case of failing (possible failing are invalid types, bad input value, ...)
     */
    @RequestMapping("/process")
    public MaryResponse process(@RequestParam(value="input") String input,
                                @RequestParam(required=false) String inputType,
                                @RequestParam(required=false) String outputType)
        throws Exception
    {

        if (inputType != null)
            localMary.setInputType(inputType);

        if (outputType != null)
            localMary.setOutputType(outputType);
                
        // Deal with output type
        if (localMary.isAudioType(localMary.getOutputType())) // Audio
        {
            // Deal with input type
            if (localMary.isTextType(localMary.getInputType())) // Text 
            {
                ais = localMary.generateAudio(input);
            }
            else if (localMary.isXMLType(localMary.getOutputType())) // XML
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                
                factory.setNamespaceAware(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                
                Document in_xml = builder.parse(new ByteArrayInputStream(input.getBytes()));
                
                ais = localMary.generateAudio(in_xml);
            }
            else
            {
                throw new Exception("Unknown input type");
            }
            
            
            return new MaryResponse(null, null, true);
        }
        else if (localMary.isTextType(localMary.getOutputType())) // Text
        {
            // Deal with input type
            if (localMary.isTextType(localMary.getInputType())) // Text 
            {
                return new MaryResponse(localMary.generateText(input), null, false);
            }
            else if (localMary.isXMLType(localMary.getOutputType())) // XML
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                
                factory.setNamespaceAware(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                
                Document in_xml = builder.parse(new ByteArrayInputStream(input.getBytes()));
                
                return new MaryResponse(localMary.generateText(in_xml), null, false);
            }
            else
            {
                throw new Exception("Unknown input type");
            }
            
        }
        else if (localMary.isXMLType(localMary.getOutputType())) // XML
        {
            // Deal with input type
            if (localMary.isTextType(localMary.getInputType())) // Text 
            {
                
                //DomUtils.document2String());
                return new MaryResponse(XML2Data.convertXML(localMary.generateXML(input)), null, false);
            }
            else if (localMary.isXMLType(localMary.getOutputType())) // XML
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                
                factory.setNamespaceAware(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                
                Document in_xml = builder.parse(new ByteArrayInputStream(input.getBytes()));
               
                // DomUtils.document2String();
                return new MaryResponse(XML2Data.convertXML(localMary.generateXML(in_xml)), null, false);
            }
            else
            {
                throw new Exception("Unknown input type");
            }
        }
        else
        {
            throw new Exception("Unknown output type");
        }
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
        String output_type = localMary.getOutputType();
        localMary.setOutputType("AUDIO");
        ais = localMary.generateAudio(text);
        localMary.setOutputType(output_type);
        return new MaryResponse(null, null, true);
    }

    
    /**
     * Method to retrieve a signal already synthesized using the method {@link synthesize(String)} or the method {@link process(String)}
     *
     *    @param response the response to fill
     *    @throws Exception in case of failing (no synthesis called before, ...)
     */
    @RequestMapping("/getSynthesizedSignal")
    public void getSynthesizedSignal(HttpServletResponse response)
        throws Exception
    {
        if (ais == null)
        {
            throw new RuntimeException("No synthesis achieved => no signal to get !");
        }
        
        response.setContentType("audio/x-wav");
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, response.getOutputStream());
        response.flushBuffer();
    }   
}
