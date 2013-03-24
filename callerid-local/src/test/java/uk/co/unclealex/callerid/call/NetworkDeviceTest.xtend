/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 *
 * @author alex
 *
 */
package uk.co.unclealex.callerid.call

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import org.junit.Test
import java.io.Closeable
import uk.co.unclealex.callerid.device.NetworkDevice
import com.google.common.base.Charsets

import static org.junit.Assert.*

class NetworkDeviceTest {
  
    @Test(timeout=1000)
    def void testNetworkDevice() {
        val echoServer = new EchoServer;
        val networkDevice = new NetworkDevice(echoServer.port, Charsets::UTF_8);
        new Thread(echoServer).start;
        networkDevice.writeLine("Hello");
        val String response = networkDevice.readLine;
        assertEquals("The wrong response was echoed back.", "Hello", response);
        networkDevice.close       
    }
}

/**
 * A small server that echoes whatever it is sent to it.
 */
@Data class EchoServer implements Runnable {
    
    val ServerSocket serverSocket = new ServerSocket;
    
    override run() {
        val Socket socket = serverSocket.accept;
        val PrintWriter out = new PrintWriter(socket.outputStream);
        val BufferedReader in = new BufferedReader(new InputStreamReader(socket.inputStream));
        var String line;
        while ((line = in.readLine) != null) {
            out.println(line);
        }
        #[in, out, socket].forEach[Closeable c | c.close]
    }
    
    def int getPort() {
        serverSocket.localPort;
    }
}