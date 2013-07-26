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