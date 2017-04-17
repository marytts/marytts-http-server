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

/**
 *  Standard mary response model wrapper
 *
 */
public class MaryResponse
{
    private final Object result; /*< The result value */
    private final ResponseType type;
    private final Exception ex;
    private final String log; /*< The server log */
    private final boolean synth_done; /*< Status to indicate in the synthesis is achieved or not */

    /**
     *  Constructor which initialise the data structure
     *
     *     @param result the result value
     *     @param log the server log
     *     @param synth_done the status of the synthesis
     *     @param ex the exception if any
     */
    public MaryResponse(Object result, String log, boolean synth_done, Exception ex)
    {
        this.result = result;
        this.log = log;
        this.synth_done = synth_done;
        this.ex = ex;
        this.type = ex == null ? ResponseType.OK : ResponseType.EXCEPTION;
    }
    
    public MaryResponse(Object result, String log, boolean synth_done, Exception ex, ResponseType type)
    {
        this.result = result;
        this.log = log;
        this.synth_done = synth_done;
        this.ex = ex;
        this.type = type;
    }

    /**
     *  Accessor to get the result value
     *
     *     @return the result json object
     */
    public Object getResult()
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
     *     @return true if a synthesis is achieved
     */
    public boolean isSynthDone()
    {
        return synth_done;
    }

    public Exception getException()
    {
        return this.ex;
    }
    
    public ResponseType getResponseType(){
        return type;
    }
}
