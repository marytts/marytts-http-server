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
import marytts.http.response.MaryResponse;
import marytts.MaryException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;


// Reflection
import org.reflections.Reflections;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;


/* Utils */
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import marytts.http.MaryLauncher;
import marytts.http.models.constants.MaryState;
import marytts.runutils.Request;
import marytts.runutils.Mary;
import marytts.config.MaryConfiguration;
import marytts.config.MaryConfigurationFactory;
import marytts.config.JSONMaryConfigLoader;

import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.OutputStreamAppender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.concurrent.atomic.AtomicInteger;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import marytts.io.serializer.Serializer;
import marytts.modules.ModuleRegistry;
import marytts.modules.MaryModule;



/**
 * Mary RESTFUL controller class
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le
 * Maguer</a>
 */
@RestController
// @Scope("session")
public class MaryController {
    private static final AtomicInteger counter = new AtomicInteger();

    protected Level current_level;
    // @Autowired
    /**
     * Constructor. Just create an interface to MaryTTS
     *
     * @throws Exception in case of the creation of the interface failed
     */
    public MaryController() throws Exception {
        current_level = Level.WARN;
    }


    /**
     * ************************************************************************
     ** Listings
     *************************************************************************
     */

    /**
     * ************************************************************************
     ** Getters
     *************************************************************************
     */
    /**
     * Method used to get the current configuration
     *
     * @param set the set identifying the configuration
     * @throws Exception in case of unexisting local
     */
    @RequestMapping(value="/getDefaultConfiguration")
    public String getDefaultConfiguration()
    throws Exception {
        return MaryConfigurationFactory.getDefaultConfiguration().toString();
    }


    /**
     * Method used to get the current configuration
     *
     * @param set the set identifying the configuration
     * @throws Exception in case of unexisting local
     */
    @RequestMapping(value="/getConfiguration", method = RequestMethod.POST)
    public String getConfiguration(@RequestParam(value = "set") String set)
    throws Exception {
        return MaryConfigurationFactory.getConfiguration(set).toString();
    }


    /**
     * Method used to get the current configuration
     *
     * @param set the set identifying the configuration
     * @throws Exception in case of unexisting local
     */
    @RequestMapping(value="/listAvailableModulesByCategories", method = RequestMethod.POST)
    public Map< String, Map<String, List<String>> > listAvailableModulesByCategories()
	throws Exception
    {
    	Map< String, Map<String, List<String>> > res_map_modules_by_cat = new HashMap< String, Map<String, List<String>> >();
	Map< String, Map<String, List<MaryModule>> > map_modules_by_cat = ModuleRegistry.listModulesByCategories();
    	for (String cat: map_modules_by_cat.keySet())
    	{
	    Map<String, List<MaryModule>> cat_subpart = map_modules_by_cat.get(cat);
	    res_map_modules_by_cat.put(cat, new HashMap<String, List<String>>());

	    for (String conf: cat_subpart.keySet()) {
		ArrayList<String> list_modules = new ArrayList<String>();

		for (MaryModule m: cat_subpart.get(conf))
		    list_modules.add(m.getClass().getName());
		res_map_modules_by_cat.get(cat).put(conf, list_modules);
	    }
    	}

    	return res_map_modules_by_cat;
    }


    /**
     * Method used to get the current configuration
     *
     * @param set the set identifying the configuration
     * @throws Exception in case of unexisting local
     */
    @RequestMapping(value="/listAvailableSerializers", method = RequestMethod.POST)
    public List<String> listAvailableSerializers()
	throws Exception
    {
	List<String> list_serializers = new ArrayList<String>();
	Reflections reflections = new Reflections("marytts");
        for (Class<? extends Serializer>  s: reflections.getSubTypesOf(Serializer.class)) {
	    if (! Modifier.isAbstract(s.getModifiers())) {
		list_serializers.add(s.getName());
	    }
        }
	return list_serializers;
    }


    /**
     * Method used to get the module description
     *
     * @param set the set identifying the configuration
     * @throws Exception in case of unexisting local
     */
    @RequestMapping(value="/getDescription", method = RequestMethod.POST)
    public String getDescription(@RequestParam(value = "module") String module)
	throws Exception {
	return ModuleRegistry.getDefaultModule(module).getDescription();
    }

    /**
     * ************************************************************************
     ** Setters
     *************************************************************************
     */
    /**
     * Method used to set the current configuration
     *
     * @param configuration the new configuration (a string containing java
     * properties)
     * @throws Exception TODO when ?
     */
    @RequestMapping(value = "/setConfiguration", method = RequestMethod.POST)
    public void setConfiguration(@RequestParam(value = "configuration") String configuration)
    throws Exception {

    }

    /**
     * ************************************************************************
     ** Process (except synthesis)
     *************************************************************************
     */
    /**
     * Method used to process a text-based input considering the current
     * configuration of MaryTTS
     *
     * @param input the input in a text-based format (XML is detected otherwise
     * everything is considered as a text)
     * @param inputType the inputType (can be null)
     * @param outputType the outputType (can be null)
     * @return MaryResponse the response where the result field contains the
     * result information
     * @throws Exception in case of failing (possible failing are invalid types,
     * bad input value, ...)
     */
    @RequestMapping(value = "/process", method = RequestMethod.POST)
    public MaryResponse process(@RequestParam(value = "input") String input_data,
                                @RequestParam(required = false) String configuration) throws Exception {

        if (MaryLauncher.getCurrentState() != Mary.STATE_RUNNING) {
            throw new IllegalStateException("MARY system is not running");
        }


        Exception save_ex = null;
        Object output = null;
	Request request = null;

        try {
	    InputStream configuration_stream = new ByteArrayInputStream(configuration.getBytes("UTF-8"));
	    MaryConfiguration conf_object = (new JSONMaryConfigLoader()).loadConfiguration(configuration_stream);
            request = new Request(conf_object, input_data);
            request.process();
            output = request.serializeFinaleUtterance();
        } catch (Exception ex) {
            save_ex = ex;
        }


	// Retrieve logger if necessary
        String log_result = "";
	if (request != null) {
	    ByteArrayOutputStream baos_logger = request.getBaosLogger();
	    if (baos_logger != null)
		log_result = baos_logger.toString("UTF-8");
	}

        return new MaryResponse(output, log_result, false, save_ex);
    }
}
