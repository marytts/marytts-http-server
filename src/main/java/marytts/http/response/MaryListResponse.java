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
package marytts.http.response;

import java.util.List;


public class MaryListResponse
{
    private final List<String> result; /*< The result list */
    private final String log; /*< The server log */
    private final boolean synth_done; /*< Status to indicate in the synthesis is achieved or not */

    /**
     *  Constructor which initialise the data structure
     *
     *     @param result the result list
     *     @param log the server log
     *     @param synth_done the status of the synthesis
     */
    public MaryListResponse(List<String> result, String log, boolean synth_done)
    {
        this.result = result;
        this.log = log;
        this.synth_done = synth_done;
    }

    /**
     *  Accessor to get the result list
     *
     *     @return the result list
     */
    public List<String> getResult()
    {
        return result;
    }

    /**
     *  Accessor to get the server log
     *
     *     @return the log string
     */
    public String getLog()
    {
        return log;
    }

    /**
     *  Accessor to get the synthesis status
     *
     *     @return the log string
     */
    public boolean isSynthDone()
    {
        return synth_done;
    }

}
