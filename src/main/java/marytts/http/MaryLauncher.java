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
import marytts.exceptions.MaryConfigurationException;
import marytts.http.models.constants.MaryState;
import marytts.modules.MaryModule;
import marytts.modules.ModuleRegistry;
import marytts.util.MaryUtils;
import marytts.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marytts.runutils.Mary;

/**
 * MaryTTS startup / shutdown script
 *
 *
 */
public class MaryLauncher extends Mary implements Runnable {

    private static Logger logger;

    private static int currentState = Mary.STATE_OFF;

    //meta attributes
    private boolean iCanContinue = true;
    private final int MAX_SLEEP_TIME = 60 * 60 * 60000; //1 hour

    protected MaryLauncher() throws Exception {
        // setDaemon(true);
        configureLogging();
        startup();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(MAX_SLEEP_TIME);
            while (iCanContinue) {
                //sleep thread for 1 hour
                Thread.sleep(MAX_SLEEP_TIME);
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
    public static synchronized void start() {
        try {
            (new Thread(new MaryLauncher())).start();   //startup marytts
        } catch (Exception ex) {
            logger.error("Error while starting mary launcher startup():" + ex);
        }
    }

    /**
     * Stop Mary Launcher
     */
    public static synchronized void stopProcessor() {
        // logger.info("Stopping mary launcher.");
        // try {
        //     shutdown(); //shutdown marytts
        // } catch (InterruptedException | NullPointerException e) {
        //     logger.error("Error stopping mary launcher shutdown():" + e);
        // }
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
}
