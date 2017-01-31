/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package digital.srp;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;

public class TestCredentials {

    public static final String BOT_USERNAME = "tstephen";
    public static final String BOT_PWD = "tstephen";
    public static final String CUST_MGMT_URL = "http://localhost:8083";

    public static void initBot(IdentityService idSvc, String tenantId) {
        User botUser = idSvc.newUser(BOT_USERNAME);
        botUser.setFirstName(tenantId.toLowerCase());
        botUser.setLastName("Bot");
        idSvc.saveUser(botUser);
        idSvc.setUserInfo(BOT_USERNAME, "cust-mgmt-secret", BOT_PWD);
        idSvc.setUserInfo(BOT_USERNAME, "cust-mgmt-url", CUST_MGMT_URL);
    }

    public static void removeBot(IdentityService idSvc, String tenantId) {
        idSvc.deleteUser(BOT_USERNAME);
    }
}
