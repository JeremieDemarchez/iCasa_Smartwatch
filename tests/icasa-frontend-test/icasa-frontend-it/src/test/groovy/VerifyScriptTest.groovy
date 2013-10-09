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
import geb.spock.GebReportingSpec
import org.openqa.selenium.interactions.Actions
import spock.lang.*

@Stepwise
class VerifyScriptTest extends GebReportingSpec {

    def "Execute script"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }

        then://get elements.
        def startButton = $(id: "startScriptButtonId")
        def tab = $(id: "ui-id-4")//Generated id.
        def table = $(id: "filteredDevicesTable")


        then://move to tab
        tab.click()
        sleep(300)


        then: // create device and check table to contain more than 1
        startButton.click()
        sleep(3000)

        assert table.children().empty == false
        table.children().size() > 1
    }

}