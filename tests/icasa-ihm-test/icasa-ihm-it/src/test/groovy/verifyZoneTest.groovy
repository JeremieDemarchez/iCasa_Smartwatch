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
class VerifyZoneTest extends GebReportingSpec {

    def "create zone"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }
        sleep(3000)

        then://get elements.
        def createZoneButton = $(id: "createZoneButton")
        def table = $(id: "filteredZoneTable")
        def zoneTab = $(id: "ui-id-2")//Generated id.

        then:
        println(zoneTab)
        zoneTab.click()
        $(id: "newZoneNameId").value("newZone1")


        then: //zone table is empty.
        //createZoneButton.text() == "Create"//Test Create Zone Button.
        assert table.children().empty == true

        then: // create zone and check table to contain 1
        createZoneButton.click()
        sleep(300)
        assert table.children().empty == false
        assert table.children().size() == 1
    }

    def "Remove All Zones"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }
        sleep(3000)
        def zoneTab = $(id: "ui-id-2")  //Generated id for tab
        zoneTab.click()

        then://get elements.
        def createZoneButton = $(id: "createZoneButton")
        def checkAllZones = $(id: "checkAllZoneCheckboxId")
        def removeAllZones = $(id: "removeSelectedZoneButtonId")
        def table = $(id: "filteredZoneTable")
        $(id: "zonesTab").click()

        then://create one zones.
        createZoneButton.click()
        sleep(300)

        then://check zones table.
        sleep(300)
        assert table.children().size() > 0

        then://remove all zones
        checkAllZones.click()//check it.
        removeAllZones.click()//remove all zones
        sleep(300)
        assert table.children().empty == true
        table.children().size() == 0
    }

}