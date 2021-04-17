package me.dolphago.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import me.dolphago.dto.ResponseDto;
import me.dolphago.service.FollowTrackingService;

@ExtendWith(MockitoExtension.class)
class FollowControllerTest {

    @InjectMocks
    private FollowController followController;

    @Mock
    private FollowTrackingService followTrackingService;

    private String URL = "/check";

    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(followController).build();
    }

    @DisplayName("controller check")
    @Test
    public void check() throws Exception {
        ResponseDto dto = ResponseDto.create();
        given(followTrackingService.checkFollow(anyString())).willReturn(dto);
        ResultActions resultActions = mockMvc.perform(get(URL)
                                                              .queryParam("handle", "DolphaGo"))
                                             .andExpect(status().isOk());
        MvcResult result = resultActions.andReturn();
        System.out.println(result.getRequest().getRequestURI());
        System.out.println(result.getRequest().getRequestURL());
    }
}