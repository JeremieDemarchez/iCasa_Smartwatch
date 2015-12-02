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
package fr.liglab.adele.icasa.service.scheduler.impl;

import java.util.concurrent.TimeUnit;

/**
 * Created by aygalinc on 02/12/15.
 */
public class TimeUtils {

    private static long m_dayFactor = 24* 60*60*1000;

    private static long m_hourFactor = 60*60*1000;

    private static long m_minuteFactor = 60*1000;

    private static long m_secondFactor = 1*1000;

    private static long m_millisecondFactor = 1;



    public static long getTimeFactorToConvertInMillisecond(TimeUnit time){
        if (time == TimeUnit.DAYS){
            return m_dayFactor;
        } else if (time == TimeUnit.HOURS){
            return m_hourFactor;

        } else if (time == TimeUnit.MINUTES){
            return m_minuteFactor;

        } else if (time == TimeUnit.SECONDS){
            return m_secondFactor;

        }
        return m_millisecondFactor;
    }


}
