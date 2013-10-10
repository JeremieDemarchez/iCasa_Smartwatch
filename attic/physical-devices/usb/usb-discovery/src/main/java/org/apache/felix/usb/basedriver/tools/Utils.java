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
package org.apache.felix.usb.basedriver.tools;

/**
 * The Class Utils.
 */
public class Utils {

    /**
     * To complete hex.
     * 
     * @param s the s
     * @return the string
     */
    public static String toCompleteHex(short s) {
        StringBuffer sb = new StringBuffer("0x");
        int et = 0xFFF;
        for (int d = 12; d >= 0; d -= 4) {
            int i = (s >> d) & 0xF;
            if (i < 10) {
                sb.append((char) (i + '0'));
            } else {
                sb.append((char) (i - 10 + 'a'));
            }
            s = (short) (s & et);
            et = et >> 4;
        }
        return sb.toString();
    }
}
