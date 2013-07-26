import geb.spock.GebReportingSpec

import spock.lang.*

@Stepwise
class VerifyDeviceCreationTest extends GebReportingSpec {

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

        then:
        def button = $(id: "connection-status-button")

        then:
        button.text() == "Connected"//Test connection.

    }

}