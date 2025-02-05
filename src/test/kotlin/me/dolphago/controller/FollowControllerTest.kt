package me.dolphago.controller

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import me.dolphago.dto.ResponseDto.Companion.create
import me.dolphago.service.FollowTrackingService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
internal class FollowControllerTest {
    @InjectMockKs
    lateinit var followController: FollowController

    @MockK
    lateinit var followTrackingService: FollowTrackingService


    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(followController).build()
    }

    private val URL = "/check"

    @DisplayName("controller check")
    @Test
    @Throws(Exception::class)
    fun check() {
        val dto = create()

        every { followTrackingService.checkFollow(any()) } returns dto

        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get(URL)
                .queryParam("handle", "DolphaGo")
        )
            .andExpect(MockMvcResultMatchers.status().isOk())

        val result = resultActions.andReturn()
        println(result.request.requestURI)
        println(result.request.requestURL)
    }
}