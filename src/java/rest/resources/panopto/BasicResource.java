/*
 * This file is part of Panopto-Java-RestWebService.
 * 
 * Panopto-Java-RestWebService is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Panopto-Java-RestWebService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Panopto-Java-RestWebService.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright: Andrew Martin, Newcastle University
 * 
 */
package rest.resources.panopto;

import com.panopto.access.AccessManagementStub;
import com.panopto.auth.AuthStub;
import com.panopto.remoterecorder.RemoteRecorderManagementStub;
import com.panopto.session.SessionManagementStub;
import com.panopto.user.UserManagementStub;
import rest.resources.panopto.enums.WSType;

public abstract class BasicResource
{
    protected String resource = "";

    protected AccessManagementStub AMClient;
    protected AuthStub AuthClient;
    protected RemoteRecorderManagementStub RRMClient;
    protected SessionManagementStub SMClient;
    protected UserManagementStub UMClient;
    
    protected Object doSecurity(WSType type, String authCode, String password, String userKey)
    {
        /**
         * 
         * Uncomment the 3 lines below and fill in as necessary if you want to run this
         * service with default credentials, this is useful in testing as it allows you
         * to access everything through a browser without having to pass credentials. It does
         * also give ANYONE who can see the service access to the API with the privileges
         * of the user you put here, so BE CAREFUL! You may want to consider locking down IP's
         * if you were to run this on anything but your own localhost tomcat.
         */
        //authCode = "";
        //password = "";
        //userKey = "";

        switch(type)
        {
            case ACCESS:
                AccessManagementStub.AuthenticationInfo accessAI = new AccessManagementStub.AuthenticationInfo();
                accessAI.setAuthCode(authCode);
                accessAI.setPassword(password);
                accessAI.setUserKey(userKey);
                return accessAI;
            case AUTH:
                AuthStub.AuthenticationInfo authAI = new AuthStub.AuthenticationInfo();
                authAI.setAuthCode(authCode);
                authAI.setPassword(password);
                authAI.setUserKey(userKey);
                return authAI;
            case REMOTERECORDER:
                RemoteRecorderManagementStub.AuthenticationInfo remoteRecorderAI = new RemoteRecorderManagementStub.AuthenticationInfo();
                remoteRecorderAI.setAuthCode(authCode);
                remoteRecorderAI.setPassword(password);
                remoteRecorderAI.setUserKey(userKey);
                return remoteRecorderAI;
            case SESSION:
                SessionManagementStub.AuthenticationInfo sessionAI = new SessionManagementStub.AuthenticationInfo();
                sessionAI.setAuthCode(authCode);
                sessionAI.setPassword(password);
                sessionAI.setUserKey(userKey);
                return sessionAI;
            case USER:
                UserManagementStub.AuthenticationInfo userAI = new UserManagementStub.AuthenticationInfo();
                userAI.setAuthCode(authCode);
                userAI.setPassword(password);
                userAI.setUserKey(userKey);
                return userAI;
            default:
                return null;
        }
    }
}
