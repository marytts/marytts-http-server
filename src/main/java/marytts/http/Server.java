/**
 * Copyright 2015 DFKI GmbH. All Rights Reserved. Use is subject to license
 * terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package marytts.http;

import groovy.util.logging.Log4j;
import java.io.FileInputStream;
import java.util.Properties;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@Log4j
@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})
public class Server {

    public static void main(String[] args)
            throws Exception {

        //start the application
        SpringApplication.run(Server.class, args);

        //load log4j properties at runtime
        if (args.length > 0) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(args[0]));
                LogManager.resetConfiguration();
                PropertyConfigurator.configure(properties);
                System.out.printf("Properties file loaded from %s...", args[0]);
            } catch (Exception e) {
                System.out.printf("Cloudn't load properties file from %s: %s", args[0], e.getMessage());
            }
        }

        //startup mary launcher
        MaryLauncher.getInstance();     //it calls startup() method too.
    }
}
