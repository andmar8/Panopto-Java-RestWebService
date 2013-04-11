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

import com.panopto.user.UserManagementStub;
import com.panopto.user.UserManagementStub.ArrayOfUser;
import com.panopto.user.UserManagementStub.ArrayOfguid;
import com.panopto.user.UserManagementStub.AuthenticationInfo;
import com.panopto.user.UserManagementStub.GetUsers;
import com.panopto.user.UserManagementStub.GetUsersResponse;
import com.panopto.user.UserManagementStub.Guid;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import panopto.resource.User;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Path("users/{user}")
@Produces("application/xml")
public class UserResource extends BasicResource
{
    String eid;
    
    public UserResource() throws org.apache.axis2.AxisFault
    {
        this.UMClient = new UserManagementStub();
        this.resource = "user";
        this.eid = null;
    }

    public UserResource(String id) throws org.apache.axis2.AxisFault
    {
        this();
        this.eid = id;
    }

    @Path("accessDetails")
    public UserAccessDetailResource getUserAccessDetails(@Context UriInfo uriInfo,@PathParam("user") String userId) throws WebApplicationException, org.apache.axis2.AxisFault
    {
        System.out.println("GET USER ACCESS DETAILS FOR USER "+userId);
        return new UserAccessDetailResource(userId);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public User getUser(@Context UriInfo uriInfo,@PathParam("user") String userId, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey)
    {
        System.out.println("GET USER "+(this.eid!=null?this.eid:userId));
        AuthenticationInfo auth = (AuthenticationInfo)doSecurity(WSType.USER, authCode, password, userKey);
        User u = new User();
        try
        {
            GetUsers gu = new GetUsers();
            gu.setAuth(auth);
            ArrayOfguid aog = new ArrayOfguid();
            Guid id = new Guid();
            id.setGuid(this.eid!=null?this.eid:userId);
            aog.addGuid(id);
            gu.setUserIds(aog);
            GetUsersResponse gur = this.UMClient.getUsers(gu);
            ArrayOfUser aou = gur.getGetUsersResult();
            for(com.panopto.user.UserManagementStub.User user : aou.getUser())
            {
                System.out.println(user.getEmail());
                u = (User)PanoptoObjectFactory.getSerializablePanoptoObject(user, "User");
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return u;
    }
}

