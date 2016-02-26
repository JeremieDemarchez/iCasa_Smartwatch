/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.physical.abstraction;

import fr.liglab.adele.icasa.context.model.annotations.ContextService;
import fr.liglab.adele.icasa.context.model.annotations.State;

/**
 * Created by aygalinc on 10/02/16.
 */
public @ContextService interface MomentOfTheDay {

    public static final @State String CURRENT_MOMENT_OF_THE_DAY = "current.moment.of.the.day";

    public PartOfTheDay getCurrentPartOfTheDay();

    public enum PartOfTheDay {
        MORNING(6), AFTERNOON(12), EVENING(18), NIGHT(22);
        /**
         * The hour when the moment start.
         */
        private final int startHour;

        /**
         * Gets the moment of the day corresponding to the hour.
         *
         * @param hour
         *            the given hour
         * @return the corresponding moment of the day
         */
        static public PartOfTheDay getCorrespondingMoment(int hour) {
            assert ((0 <= hour) && (hour <= 24));
            if (PartOfTheDay.NIGHT.getStartHour() <=hour ){
                return PartOfTheDay.NIGHT;
            }
            else if (PartOfTheDay.EVENING.getStartHour() <=hour ){
                return PartOfTheDay.EVENING;
            }
            else if (PartOfTheDay.AFTERNOON.getStartHour() <=hour ){
                return PartOfTheDay.AFTERNOON;
            }
            else if (PartOfTheDay.MORNING.getStartHour() <=hour ){
                return PartOfTheDay.MORNING;
            }else{
                return PartOfTheDay.NIGHT;
            }

        }

        private int getStartHour() {
            return startHour;
        }

        /**
         * Build a new moment of the day :
         *
         * @param startHour
         *            when the moment start.
         */
        private PartOfTheDay(int startHour) {
            assert ((0 <= startHour) && (startHour <= 24));
            this.startHour = startHour;
        }
    }
}
