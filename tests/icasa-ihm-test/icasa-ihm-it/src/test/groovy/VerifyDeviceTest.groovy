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
class VerifyDeviceTest extends GebReportingSpec {

    def "go to Simulator"() {
        when:
        go() // uses base url system property

        then:
        waitFor {title.startsWith("iCasa")}
    }

    def "create device"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }

        then://get elements.
        def createDeviceButton = $(id: "createDeviceButton")
        def table = $(id: "filteredDevicesTable")

        then: //device table is empty.
        createDeviceButton.text() == "Create"//Test Create Device Button.
        assert table.children().empty == true

        then: // create device and check table to contain 1
        createDeviceButton.click()
        sleep(300)
        assert table.children().empty == false
        assert table.children().size() == 1
    }

    def "Remove All Devices"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }

        then://get elements.
        def createDeviceButton = $(id: "createDeviceButton")
        def checkAllDevice = $(id: "checkAllDeviceCheckboxId")
        def removeAllDevice = $(id: "removeSelectedDeviceButtonId")
        def table = $(id: "filteredDevicesTable")

        then://create one device.
        createDeviceButton.click()
        sleep(300)

        then://check device table.
        sleep(300)
        assert table.children().size() > 0

        then://remove all devices
        checkAllDevice.click()//check it.
        removeAllDevice.click()//remove all devices
        sleep(300)
        assert table.children().empty == true
        table.children().size() == 0
   }

}