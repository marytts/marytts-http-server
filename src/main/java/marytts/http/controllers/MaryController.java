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
import marytts.modules.ModuleRegistry;


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

import java.util.List;
import java.util.ArrayList;
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
    @RequestMapping(value="/listAvailableModules")
    public List<String> listAvailableModules()
    throws Exception {
	ArrayList<String> list_modules = new ArrayList<String>();
	for (MaryModule m: ModuleRegistry.listRegisteredModules())
	    list_modules.add(m.getClass().toString());

	return list_modules;
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

    @RequestMapping(value = "/setLoggerLevel", method = RequestMethod.POST)
    public void  setLoggerLevel(@RequestParam(value = "level") String level) throws Exception {
	if (level == "ERROR")
	    current_level = Level.ERROR;
	else if (level == "WARN")
	    current_level = Level.WARN;
	else if (level == "INFO")
	    current_level = Level.INFO;
	else if (level == "DEBUG")
	    current_level = Level.DEBUG;
	else
	    throw new Exception("\"" + level + "\" is an unknown level");
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

        int id = counter.incrementAndGet();

        Exception save_ex = null;
        Object output = null;



        ByteArrayOutputStream baos_logger = new ByteArrayOutputStream();
        ThresholdFilter threshold_filter = ThresholdFilter.createFilter(current_level, null, null);
        LoggerContext context = LoggerContext.getContext(false);
        Configuration config = context.getConfiguration();
        PatternLayout layout = PatternLayout.createDefaultLayout(config);
        Appender appender = OutputStreamAppender.createAppender(layout, threshold_filter, baos_logger,
                            "client " + (new Integer(id)).toString(),
                            false, true);
        appender.start();

        try {
	    InputStream configuration_stream = new ByteArrayInputStream(configuration.getBytes("UTF-8"));
	    MaryConfiguration conf_object = (new JSONMaryConfigLoader()).loadConfiguration(configuration_stream);
            Request request = new Request(appender, conf_object, input_data);
            request.process();
            output = request.serializeFinaleUtterance();
        } catch (Exception ex) {
            save_ex = ex;
        }

        String log_result = baos_logger.toString("UTF-8");
        appender.stop();

        return new MaryResponse(output, log_result, false, save_ex);
    }
}
