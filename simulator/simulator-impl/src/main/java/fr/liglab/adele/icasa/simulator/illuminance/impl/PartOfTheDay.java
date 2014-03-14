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
package fr.liglab.adele.icasa.simulator.illuminance.impl;

public enum PartOfTheDay {
    MORNING(6), AFTERNOON(12), EVENING(18), NIGHT(22);

    /**
     * Gets the moment of the day corresponding to the hour.
     *
     * @param hour
     *            the given hour
     * @return the corresponding moment of the day
     */
    PartOfTheDay getCorrespondingMoment(int hour) {
        assert ((0 <= hour) && (hour <= 24));
       /* if ((6 <= hour) &&  (hour< 12)){
            return MomentOfTheDay.MORNING;
        }else if ((6 <= hour) && (hour< 12)){
            return MomentOfTheDay.AFTERNOON;
        }else if ((12 <= hour) && (hour < 18)){
            return MomentOfTheDay.EVENING;
        }else {
            return MomentOfTheDay.NIGHT;
        }*/
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

    int getStartHour() {
        assert ((0 <= startHour) && (startHour <= 24));
        return startHour;
    }

    /**
     * The hour when the moment start.
     */
    private final int startHour;

    /**
     * Build a new moment of the day :
     *
     * @param startHour
     *            when the moment start.
     */
    PartOfTheDay(int startHour) {
        assert ((0 <= startHour) && (startHour <= 24));
        this.startHour = startHour;
    }
}