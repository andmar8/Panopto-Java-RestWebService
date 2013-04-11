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

//javax
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.panopto.auth.AuthStub;
import com.panopto.auth.AuthStub.LogOnWithPassword;
import com.panopto.auth.AuthStub.LogOnWithPasswordResponse;
import javax.ws.rs.core.MediaType;
import panopto.resource.Folder;

@Path("auth")
@Produces("application/xml")
public class AuthResource extends BasicResource
{
    private String eid;
    
    public AuthResource() throws org.apache.axis2.AxisFault
    {
        this.AuthClient = new AuthStub();
        this.resource = "Auth";
        this.eid = null;
    }

    public AuthResource(String id) throws org.apache.axis2.AxisFault
    {
        this();
        this.eid = id;
    }

    /**
     * 
     * This is really just a code example
     */
    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Folder getFolder(@Context UriInfo uriInfo,@PathParam("folder") String folderId, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey)
    {
        System.out.println("GET AUTH "+(this.eid!=null?this.eid:folderId));
        //this.doSecurity(authCode, password, userKey);

        LogOnWithPassword lowp = new LogOnWithPassword();
        lowp.setPassword(password);
        lowp.setUserKey(userKey);
        Folder f = new Folder();
        try
        {
            LogOnWithPasswordResponse lowpr = this.AuthClient.logOnWithPassword(lowp);
            f.setAllowPublicNotes(lowpr.getLogOnWithPasswordResult());
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return f;
    }

}

