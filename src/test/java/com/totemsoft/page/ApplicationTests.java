package com.totemsoft.page;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ApplicationTests {

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    //private MockRestServiceServer mockServer;

    @Test
    void contextLoads() {
        //
    }

}
