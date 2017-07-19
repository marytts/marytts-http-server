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
package marytts.http.models.constants;

/**
 * Mary running state
 *
 * @author <a href="mailto:attashah@coli.uni-saarland.de">Atta-Ur-Rehman
 * Shah</a>
 */
public enum MaryState {

    STATE_OFF(0),
    STATE_STARTING(1),
    STATE_RUNNING(2),
    STATE_SHUTTING_DOWN(3);

    private final int state;

    private MaryState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public MaryState findByState(int state) {
        for (MaryState value : values()) {
            if (value.getState() == state) {
                return value;
            }
        }
        return null;
    }
}
