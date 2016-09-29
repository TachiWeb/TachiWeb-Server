/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.nulldev.ts.sync.http;

import java.io.IOException;
import java.util.Map;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 26/08/16
 *
 * Skeleton for a class that can make HTTP requests
 */
public interface HttpClient {
    /**
     * Make a synchronous HTTP POST request
     * @param url The URL to POST to.
     * @param headers The headers to include in the request.
     * @param postBody The body of the POST request.
     * @return The response from the server
     * @throws IOException If an error occurred while making the request.
     */
    String postRequest(String url, Map<String, String> headers, String postBody) throws IOException;

    /**
     * Make a synchronous HTTP GET request
     * @param url The URL to GET from.
     * @param headers The headers to include in the request.
     * @return The response from the server.
     * @throws IOException If an error occured while making the request.
     */
    String getRequest(String url, Map<String, String> headers) throws IOException;
}
