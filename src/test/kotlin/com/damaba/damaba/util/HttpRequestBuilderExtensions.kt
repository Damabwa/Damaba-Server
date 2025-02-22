package com.damaba.damaba.util

import com.damaba.damaba.domain.user.User
import com.damaba.damaba.util.fixture.SecurityFixture.createAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

fun MockHttpServletRequestBuilder.withAuthUser(user: User): MockHttpServletRequestBuilder = this.with(authentication(createAuthenticationToken(user)))
