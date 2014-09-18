/*
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
 */

package org.apache.airavata.gsi.ssh.impl;

import org.apache.airavata.credential.store.util.GSIAuthenticationInfo;
import org.apache.airavata.gsi.ssh.api.*;
import org.apache.airavata.gsi.ssh.config.ConfigReader;
import org.apache.airavata.gsi.ssh.impl.authentication.MyProxyAuthenticationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DefaultSSHApiTestWithMyProxyAuth {
    private static final Logger log = LoggerFactory.getLogger(PBSCluster.class);



    public void tearDown() throws Exception {
    }


    public static void main(String[]ars){
         String myProxyUserName = "us3";
         String myProxyPassword = "Cme4UScan";
         String certificateLocation = "/Users/smarru/deploy/certificates";


        GSIAuthenticationInfo authenticationInfo
                = new MyProxyAuthenticationInfo(myProxyUserName, myProxyPassword, "myproxy.teragrid.org",
                7512, 17280000, certificateLocation);

        // Create command
        CommandInfo commandInfo = new RawCommandInfo("/bin/ls");

        // Server info
        ServerInfo serverInfo = new ServerInfo("us3", "stampede.tacc.utexas.edu", 2222);

        // Output
        CommandOutput commandOutput = new SystemCommandOutput();

        // Execute command
        try {
            CommandExecutor.executeCommand(commandInfo, serverInfo, authenticationInfo, commandOutput, new ConfigReader());
        } catch (SSHApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
