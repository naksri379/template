package com.mytest.nack;

import com.mytest.nack.util.EndpointHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloSuccessIntegrationTest {
    @Autowired
    private EndpointHelper sendRequestService;

    @Test
    public void success() {
        ResponseEntity<String> response = sendRequestService.callHelloWorld();
        // Assertion
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Hello, World");
    }
}
