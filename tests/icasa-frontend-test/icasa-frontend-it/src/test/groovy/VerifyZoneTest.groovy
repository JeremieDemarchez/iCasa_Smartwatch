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
import org.openqa.selenium.internal.Locatable
import spock.lang.*
import org.openqa.selenium.interactions.*

@Stepwise
class VerifyZoneTest extends GebReportingSpec {
    def "Remove All Zones"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }
        sleep(300)
        def zoneTab = $(id: "ui-id-2")  //Generated id for tab
        zoneTab.click()
        $(id: "newZoneNameId").value("newZone2")

        then://get elements.
        def createZoneButton = $(id: "createZoneButton")
        def checkAllZones = $(id: "checkAllZoneCheckboxId")
        def removeAllZones = $(id: "removeSelectedZoneButtonId")
        def table = $(id: "filteredZoneTable")

        then://create one zones.
        createZoneButton.click()
        sleep(300)

        then://check zones table.
        sleep(300)
        assert table.children().size() > 0

        then://remove all zones
        checkAllZones.click()//check it.
        removeAllZones.click()//remove all zones
        sleep(3000)
        assert table.children().empty == true
        table.children().size() == 0
    }

    def "create zone"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }
        sleep(300)

        then://get elements.
        def createZoneButton = $(id: "createZoneButton")
        def table = $(id: "filteredZoneTable")
        def zoneTab = $(id: "ui-id-2")//Generated id.

        then:
        zoneTab.click()
        $(id: "newZoneNameId").value("newZone1")


        then: //zone table is empty.
        assert table.children().empty == true

        then: // create zone and check table to contain 1
        createZoneButton.click()
        sleep(300)
        assert table.children().empty == false
        assert table.children().size() == 1
    }



    def "Move Zone"() {
        when:
        go(baseUrl+"/map/default")
        def zoneId = "newZone3"
        def offset = 400
        then:
        waitFor { title.startsWith("iCasa") }
        sleep(300)
        def zoneTab = $(id: "ui-id-2")  //Generated id for tab
        zoneTab.click()
        $(id: "newZoneNameId").value(zoneId)

        then://get elements.
        def createZoneButton = $(id: "createZoneButton")

        then://create one zones.
        createZoneButton.click()
        sleep(300)

        then://get coordinates
        def zone = $(id: zoneId).firstElement()
        def leftValue = zone.getCssValue("left").minus("px").toInteger()
        def topValue = zone.getCssValue("top").minus("px").toInteger()

        then://move Zone
        sleep(300)
        def actions = new Actions(driver)
        actions.dragAndDropBy(zone, offset,offset)
        actions.perform()

        then: //test new values
        assert leftValue + offset == zone.getCssValue("left").minus("px").toInteger()
        assert topValue + offset == zone.getCssValue("top").minus("px").toInteger()
    }

}