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
class VerifyPersonTest extends GebReportingSpec {

    def "create person"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }

        then://get elements.
        def createPersonButton = $(id: "createPersonButton")
        def table = $(id: "filteredPersonsTable")
        def tab = $(id: "ui-id-3")//Generated id.

        then:
        tab.click()
        $(id: "newPersonNameId").value("Paquito")

        then: //person table is empty.
        createPersonButton.text() == "Create"//Test Create Person Button.
        assert table.children().empty == true

        then: // create device and check table to contain 1
        createPersonButton.click()
        sleep(300)
        assert table.children().empty == false
        assert table.children().size() == 1
    }

    def "Remove All Persons"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }

        then://get elements.
        def createPersonButton = $(id: "createPersonButton")
        def checkAllPersons = $(id: "checkAllPersonCheckboxId")
        def removeAllPersons = $(id: "removeSelectedPersonButtonId")
        def table = $(id: "filteredPersonsTable")
        def tab = $(id: "ui-id-3")//Generated id.

        then:
        tab.click()
        $(id: "newPersonNameId").value("PeterWeber")

        then://create one device.
        createPersonButton.click()
        sleep(300)

        then://check device table.
        sleep(300)
        assert table.children().size() > 0

        then://remove all devices
        checkAllPersons.click()//check it.
        removeAllPersons.click()//remove all devices
        sleep(300)
        assert table.children().empty == true
        table.children().size() == 0
    }

    def "create person with Type"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }

        then://get elements.
        def createPersonButton = $(id: "createPersonButton")
        def table = $(id: "filteredPersonsTable")
        def tab = $(id: "ui-id-3")//Generated id.

        then:
        tab.click()
        $(id: "newPersonNameId").value("Paquito")
        $(id: "selectPersonTypeId").value("Father")

        then: //person table is empty.
        createPersonButton.text() == "Create"//Test Create Person Button.
        assert table.children().empty == true

        then: // create device and check table to contain 1
        createPersonButton.click()
        sleep(300)
        assert table.children().empty == false
        assert table.children().size() == 1
    }



    def "Move Person"() {
        when:
        go(baseUrl+"/map/default")
        def offset = 400
        then:
        waitFor { title.startsWith("iCasa") }
        sleep(300)

        then://get elements.
        def createButton = $(id: "createPersonButton")
        def table = $(id: "filteredPersonsTable")
        def tab = $(id: "ui-id-3")//Generated id.
        def personId = "PeterWeberChatanuga"

        then:
        tab.click()
        $(id: "newPersonNameId").value(personId)

        then://create one person.
        createButton.click()
        sleep(300)

        then://get coordinates
        def person = $(id: personId).firstElement()
        def leftValue = person.getCssValue("left").minus("px").toFloat()
        def topValue = person.getCssValue("top").minus("px").toFloat()

        then://move person
        sleep(300)
        def actions = new Actions(driver)
        actions.dragAndDropBy(person, offset,offset)
        actions.perform()

        then: //test new values

        assert leftValue < person.getCssValue("left").minus("px").toFloat()
        assert topValue < person.getCssValue("top").minus("px").toFloat()
    }


}