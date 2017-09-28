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
package marytts.http;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import marytts.Version;
import marytts.exceptions.MaryConfigurationException;
import marytts.http.models.constants.MaryState;
import marytts.modules.MaryModule;
import marytts.modules.ModuleRegistry;
import marytts.config.MaryProperties;
import marytts.util.MaryCache;
import marytts.util.MaryUtils;
import marytts.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * MaryTTS startup / shutdown script
 *
 * @author <a href="mailto:attashah@coli.uni-saarland.de">Atta-Ur-Rehman
 * Shah</a>
 */
public class MaryLauncher extends Thread {

    private static Logger logger;

    private static MaryState currentState = MaryState.STATE_OFF;
    private static boolean jarsAdded = false;

    //meta attributes
    private boolean iCanContinue = true;
    private static MaryLauncher instance = null;
    private final int MAX_SLEEP_TIME = 60 * 60 * 60000; //1 hour

    public MaryLauncher() {
        super("MaryLauncher");
        setDaemon(true);
        instance = this;    //assign current reference to instance
    }

    @Override
    public void run() {
        try {
            this.sleep(MAX_SLEEP_TIME);
            while (iCanContinue) {
                //sleep thread for 1 hour
                this.sleep(MAX_SLEEP_TIME);
            }
        } catch (InterruptedException ex) {
            logger.error("Mary launcher stopped: " + ex);
        }
    }

    /**
     * Startup Mary Launcher
     *
     * @return
     */
    public static synchronized MaryLauncher getInstance() {
        try {
	    if (instance == null) {
		instance = new MaryLauncher();
		instance.start();   //startup marytts
		startup();
	    }

        } catch (Exception ex) {
            logger.error("Error while starting mary launcher startup():" + ex);
        }
        return instance;
    }

    /**
     * Stop Mary Launcher
     */
    public static synchronized void stopProcessor() {
        if (instance == null) {
            return;
        }
        logger.info("Stopping mary launcher.");
        try {
            instance.iCanContinue = false;
            shutdown(); //shutdown marytts
            instance.interrupt();
            instance.join();
        } catch (InterruptedException | NullPointerException e) {
            logger.error("Error stopping mary launcher shutdown():" + e);
        }
    }

    /**
     * Inform about system state.
     *
     * @return an integer representing the current system state.
     * @see #STATE_OFF
     * @see #STATE_STARTING
     * @see #STATE_RUNNING
     * @see #STATE_SHUTTING_DOWN
     */
    public static synchronized MaryState currentState() {
        return currentState;
    }

    private static void startModules()
    throws ClassNotFoundException, InstantiationException, Exception {
        for (String moduleClassName : MaryProperties.moduleInitInfo()) {
            MaryModule m = ModuleRegistry.instantiateModule(moduleClassName);
            // Partially fill module repository here;
            // TODO: voice-specific entries will be added when each voice is loaded.
            ModuleRegistry.registerModule(m, m.getLocale());
        }
        ModuleRegistry.setRegistrationComplete();

        List<Pair<MaryModule, Long>> startupTimes = new ArrayList<Pair<MaryModule, Long>>();

        // Separate loop for startup allows modules to cross-reference to each
        // other via Mary.getModule(Class) even if some have not yet been
        // started.
        for (MaryModule m : ModuleRegistry.getAllModules()) {
            // Only start the modules here if in server mode:
            if ((!MaryProperties.getProperty("server").equals("commandline"))
                    && m.getState() == MaryModule.MODULE_OFFLINE) {
                long before = System.currentTimeMillis();
                try {
                    m.startup();
                } catch (Throwable t) {
                    throw new Exception("Problem starting module " + m.name(), t);
                }
                long after = System.currentTimeMillis();
                startupTimes.add(new Pair<MaryModule, Long>(m, after - before));
            }
        }

        if (startupTimes.size() > 0) {
            Collections.sort(startupTimes, new Comparator<Pair<MaryModule, Long>>() {
                public int compare(Pair<MaryModule, Long> o1, Pair<MaryModule, Long> o2) {
                    return -o1.getSecond().compareTo(o2.getSecond());
                }
            });
            logger.debug("Startup times:");
            for (Pair<MaryModule, Long> p : startupTimes) {
                logger.debug(p.getFirst().name() + ": " + p.getSecond() + " ms");
            }
        }
    }

    /**
     * Start the MARY system and all modules. This method must be called once
     * before any calls to
     * {@link #process(String input, String inputTypeName, String outputTypeName, String localeString, String audioTypeName, String voiceName, String style, String effects, String outputTypeParams, OutputStream output)}
     * are possible. The method will dynamically extend the classpath to all jar
     * files in MARY_BASE/java/*.jar. Use <code>startup(false)</code> if you do
     * not want to automatically extend the classpath in this way.
     *
     * @throws IllegalStateException if the system is not offline.
     * @throws Exception Exception
     */
    public static void startup()
    throws Exception {

        if (currentState != MaryState.STATE_OFF) {
            throw new IllegalStateException("Cannot start system: it is not offline");
        }
        currentState = MaryState.STATE_STARTING;

        configureLogging();

        logger.info("Mary starting up...");
        logger.info("Specification version " + Version.specificationVersion());
        logger.info("Implementation version " + Version.implementationVersion());
        logger.info("Running on a Java " + System.getProperty("java.version") + " implementation by "
                    + System.getProperty("java.vendor") + ", on a " + System.getProperty("os.name") + " platform ("
                    + System.getProperty("os.arch") + ", " + System.getProperty("os.version") + ")");
        logger.debug("MARY_BASE: " + MaryProperties.maryBase());
        String[] installedFilenames = new File(MaryProperties.maryBase() + "/installed").list();
        if (installedFilenames == null) {
            logger.debug("The installed/ folder does not exist.");
        } else {
            StringBuilder installedMsg = new StringBuilder();
            for (String filename : installedFilenames) {
                if (installedMsg.length() > 0) {
                    installedMsg.append(", ");
                }
                installedMsg.append(filename);
            }
            logger.debug("Content of installed/ folder: " + installedMsg);
        }
        String[] confFilenames = new File(MaryProperties.maryBase() + "/conf").list();
        if (confFilenames == null) {
            logger.debug("The conf/ folder does not exist.");
        } else {
            StringBuilder confMsg = new StringBuilder();
            for (String filename : confFilenames) {
                if (confMsg.length() > 0) {
                    confMsg.append(", ");
                }
                confMsg.append(filename);
            }
            logger.debug("Content of conf/ folder: " + confMsg);
        }
        logger.debug("Full dump of system properties:");
        for (Object key : new TreeSet<Object>(System.getProperties().keySet())) {
            logger.debug(key + " = " + System.getProperties().get(key));
        }

        try {
            // Nov 2009, Marc: This causes "[Deprecated] Xalan: org.apache.xalan.Version" to be written to the console.
            // Class xalanVersion = Class.forName("org.apache.xalan.Version");
            // logger.debug(xalanVersion.getMethod("getVersion").invoke(null));
        } catch (Exception e) {
            // Not xalan, no version number
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutdown();
            }
        });

        // Instantiate module classes and startup modules:
        startModules();

        logger.info("Startup complete.");
        currentState = MaryState.STATE_RUNNING;
    }

    /**
     * Log4j initialisation, called from {@link #startup(boolean)}.
     *
     * @throws NoSuchPropertyException NoSuchPropertyException
     * @throws IOException IOException
     */
    private static void configureLogging()
    throws MaryConfigurationException, IOException {
        //        logger = MaryUtils.getLogger("main");
        logger = LogManager.getLogger("mary.http.log");
    }

    /**
     * Orderly shut down the MARY system.
     *
     * @throws IllegalStateException if the MARY system is not running.
     */
    public static void shutdown() {
        if (currentState != MaryState.STATE_RUNNING) {
            throw new IllegalStateException("MARY system is not running");
        }
        currentState = MaryState.STATE_SHUTTING_DOWN;
        logger.info("Shutting down modules...");
        // Shut down modules:
        for (MaryModule m : ModuleRegistry.getAllModules()) {
            if (m.getState() == MaryModule.MODULE_RUNNING) {
                m.shutdown();
            }
        }

        if (MaryCache.haveCache()) {
            MaryCache cache = MaryCache.getCache();
            try {
                cache.shutdown();
            } catch (SQLException e) {
                logger.warn("Cannot shutdown cache: ", e);
            }
        }
        logger.info("Shutdown complete.");
        currentState = MaryState.STATE_OFF;
    }
}
