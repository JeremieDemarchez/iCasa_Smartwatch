/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
import geb.spock.GebReportingSpec

import spock.lang.*

@Stepwise
class VerifyIHMTest extends GebReportingSpec {
    
    def "go to Simulator"() {
        when:
        go() // uses base url system property
        
        then:
        waitFor {title.startsWith("iCasa")}
    }


    def "go to the default map"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }

        then:
        def button = $(id: "connection-status-button")

        then:
        button.text() == "Connected"//Test connection.

    }


}