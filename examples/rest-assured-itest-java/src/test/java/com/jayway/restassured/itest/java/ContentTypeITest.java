/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jayway.restassured.itest.java;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.itest.java.support.WithJetty;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ContentTypeITest extends WithJetty {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void canValidateResponseContentType() throws Exception {
        exception.expect(AssertionError.class);
        exception.expectMessage("Expected content-type \"something\" doesn't match actual content-type \"application/json; charset=UTF-8\".");

        expect().contentType("something").when().get("/hello");
    }

    @Test
    public void canValidateResponseContentTypeWithHamcrestMatcher() throws Exception {
        expect().contentType(is("application/json; charset=UTF-8")).when().get("/hello");
    }

    @Test
    public void doesntAppendCharsetToContentTypeWhenContentTypeIsNotExplicitlyDefinedAndEncoderConfigIsConfiguredAccordingly() throws Exception {
        given().
                config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToStreamingContentTypeIfUndefined(false))).
                body(new byte[] {42}).
        when().
                post("/returnContentTypeAsBody").
        then().
                body(equalTo("application/octet-stream"));
    }

    @Test
    public void doesntAppendCharsetToContentTypeWhenContentTypeIsExplicitlyDefinedAndEncoderConfigIsConfiguredAccordingly() throws Exception {
        given().
                config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToStreamingContentTypeIfUndefined(false))).
                contentType("application/zip").
                body(new byte[] {42}).
        when().
                post("/returnContentTypeAsBody").
        then().
                body(equalTo("application/zip"));
    }

    @Test
    public void appendsCharsetToContentTypeWhenContentTypeIsExplicitlyDefinedAndEncoderConfigIsConfiguredAccordingly() throws Exception {
        given().
                config(RestAssured.config().encoderConfig(encoderConfig().defaultContentCharset("UTF-8").appendDefaultContentCharsetToStreamingContentTypeIfUndefined(true))).
                contentType("application/zip").
                body(new byte[] {42}).
        when().
                post("/returnContentTypeAsBody").
        then().
                body(equalTo("application/zip; charset=UTF-8"));
    }

    @Test
    public void appendCharsetToContentTypeWhenContentTypeIsNotExplicitlyDefinedAndEncoderConfigIsConfiguredAccordingly() throws Exception {
        given().
                config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToStreamingContentTypeIfUndefined(true))).
                body(new byte[] {42}).
        when().
                post("/returnContentTypeAsBody").
        then().
                body(equalTo("application/octet-stream; charset=ISO-8859-1"));
    }
}
