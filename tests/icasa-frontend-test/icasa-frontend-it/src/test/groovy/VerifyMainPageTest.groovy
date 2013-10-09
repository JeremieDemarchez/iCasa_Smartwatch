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
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import spock.lang.*

@Stepwise
class VerifyMainPageTest extends GebReportingSpec {

    def "connected status"() {
        when:
        go(baseUrl+"/map/default")

        then:
        waitFor { title.startsWith("iCasa") }

        then:
        def button = $(id: "connection-status-button")

        then:
        button.text() == "Connected"//Test connection.

    }

    def "add Map"() {
        when:
        go()

        then:
        waitFor { title.startsWith("iCasa") }
        def mapId = "superMap"

        then://test that are two entries in map, the default map, and the Add Map element
        assert $("ul",class:"thumbnails").find("li").size() == 2


        then://Click in the add new map link
        def link = $(id: "addFormLink")
        link.click()
        sleep(2000)

        then://fill the form. They are obtained this way 'cause they are not visible.
        setValue("mapId","value", mapId)
        setValue("mapName","value", mapId)
        setValue("gatewayURL","value", "http://localhost:8080")
        String workingDir = System.getProperty("user.dir");
        WebElement fileInput = driver.findElement(By.id("picture"));
        fileInput.sendKeys(workingDir+ File.separator +"src" + File.separator + "test" + File.separator + "resources" + File.separator + "house.png")

        then://Click the upload file.
        driver.findElement(By.id("addMapFormSubmitButton")).click()
        sleep(2000)

        then://test the new map
        assert $("ul",class:"thumbnails").find("li").size()  == 3
    }

    def "go to new map"(){
        when:
        go(baseUrl + "/map/superMap")

        then:
        waitFor { title.startsWith("iCasa") }

        then:
        def button = $(id: "connection-status-button")

        then:
        button.text() == "Connected"//Test connection.
    }

    def setValue(String id, String attribute, String value){
        JavascriptExecutor js = (JavascriptExecutor) driver
        js.executeScript("document.getElementById('"+id+"').setAttribute('"+attribute+"','"+value+"')")
        return value
    }


}