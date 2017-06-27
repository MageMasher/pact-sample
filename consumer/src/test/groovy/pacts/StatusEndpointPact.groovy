package pacts

import org.junit.Test

import au.com.dius.pact.consumer.PactVerificationResult
import au.com.dius.pact.consumer.groovy.PactBuilder
import groovyx.net.http.RESTClient

class StatusEndpointPact {

    @Test
    void "pact for /status"() {
        def statusEndpointPact = new PactBuilder()

        statusEndpointPact {
            serviceConsumer "Status CLI" 	        // Define the service consumer by name
            hasPactWith "Status Endpoint"           // Define the service provider that the consumer has a pact with
            port 1234                               // The port number for the service. It is optional, leave it out to use a random one

            given('status endpoint is up')
            uponReceiving('a status enquiry')
            withAttributes(method: 'get', path: '/status')
            willRespondWith(
                    status: 200,
                    headers: ['Content-Type': 'application/json'],
                    body: '{"status":"OK","currentDateTime":"2017-06-27T13:54:29.214"}'
            )
        }

        // Execute the run method to have the mock server run.
        // It takes a closure to execute your requests and returns a PactVerificationResult.
        PactVerificationResult result = statusEndpointPact.runTest {
            def client = new RESTClient('http://localhost:1234/')
            def response = client.get(path: '/status')

            assert response.status == 200
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
        }

        assert result == PactVerificationResult.Ok.INSTANCE  // This means it is all good
    }
}
