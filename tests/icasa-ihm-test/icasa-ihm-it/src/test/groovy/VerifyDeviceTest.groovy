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
import org.openqa.selenium.interactions.Actions
import spock.lang.*

@Stepwise
class VerifyDeviceTest extends GebReportingSpec {

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

    def "Move Device"() {
        when:
        go(baseUrl+"/map/default")
        def offset = 400
        then:
        waitFor { title.startsWith("iCasa") }
        sleep(300)

        then://get elements.
        def createDeviceButton = $(id: "createDeviceButton")
        def table = $(id: "filteredDevicesTable")

        then://create one device.
        createDeviceButton.click()
        sleep(300)

        then://Get device ID from the device table
        def deviceId = table.find('a').text()
        println (deviceId)

        then://get coordinates
        def device = $('#'+deviceId).firstElement()
        def leftValue = device.getCssValue("left").minus("px").toFloat()
        def topValue = device.getCssValue("top").minus("px").toFloat()

        then://move device
        sleep(300)
        def actions = new Actions(driver)
        actions.dragAndDropBy(device, offset,offset)
        actions.perform()

        then: //test new values
        println("old left value " + leftValue)
        println("new left value " + device.getCssValue("left").minus("px").toFloat())
        assert leftValue < device.getCssValue("left").minus("px").toFloat()
        assert topValue < device.getCssValue("top").minus("px").toFloat()
    }


}