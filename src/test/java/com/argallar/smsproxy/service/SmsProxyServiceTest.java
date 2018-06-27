package com.argallar.smsproxy.service;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.argallar.smsproxy.SmsProxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SmsProxy.class)
@SpringBootTest
public class SmsProxyServiceTest {

    private MockMvc mockMvc;
        
    @Autowired
    private WebApplicationContext wac;
    
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    
	@Test
	public void createRequest() throws Exception {
	    
	    mockMvc.perform(MockMvcRequestBuilders.post("/request-sms")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content("{ \"recipient\": \"888999333\", \"originator\" : \"test\", " +
	                    "\"message\" : \"The test message.\" }")
	            .accept(MediaType.APPLICATION_JSON))
        	    .andExpect(jsonPath("$.recipient").value("888999333"))
                .andExpect(jsonPath("$.originator").value("test"))
                .andExpect(jsonPath("$.message").value("The test message."))
        	    .andDo(print());
	}

	@Test
	public void getReport() throws Exception {
	    
	    mockMvc.perform(MockMvcRequestBuilders.get("/report")
	            .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].recipient").exists())
                .andExpect(jsonPath("$.[0].originator").exists())
                .andExpect(jsonPath("$.[0].message").exists())
                .andExpect(jsonPath("$.[0].createDate").exists())
                .andExpect(jsonPath("$.[0].chunksLeft").exists())
                .andExpect(jsonPath("$.[0].failedChunks").exists())
                .andDo(print());
	}
    
    @Test
    public void exceptionBRHandler() throws Exception {
        
        mockMvc.perform(MockMvcRequestBuilders.post("/request-sms")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"recipient\": \"a\", \"originator\" : \"a\", " +
                        "\"message\" : \"\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());
    }
    
    @Test
    public void exceptionNFHandler() throws Exception {
        
        mockMvc.perform(MockMvcRequestBuilders.post("/report")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode")
                        .value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Url not found or method not supported"))
                .andDo(print());
    }
}
