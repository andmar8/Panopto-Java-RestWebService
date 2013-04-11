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
import com.panopto.user.UserManagementStub.AuthenticationInfo;
import com.panopto.user.UserManagementStub.ListUsers;
import com.panopto.user.UserManagementStub.ListUsersRequest;
import com.panopto.user.UserManagementStub.ListUsersResponse;
import com.panopto.user.UserManagementStub.ListUsersResponse1;
import com.panopto.user.UserManagementStub.Pagination;
import com.panopto.user.UserManagementStub.UserSortField;

//jax-rs
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import panopto.resource.User;
import panopto.resource.collections.impl.Users;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Path("users")
@Produces("application/xml")
public class UsersResource extends BasicResource
{
    public UsersResource() throws org.apache.axis2.AxisFault
    {
        this.UMClient = new UserManagementStub();
        this.resource = "users";
    }

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Users getUsers(@Context UriInfo uriInfo, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey) throws WebApplicationException
    {
        System.out.println("GET USERS");
        AuthenticationInfo auth = (AuthenticationInfo)doSecurity(WSType.USER, authCode, password, userKey);
        Users u = new Users();
        try
        {
            ListUsers lu = new ListUsers();
            lu.setAuth(auth);
            ListUsersRequest luReq = new ListUsersRequest();
            Pagination pagination = new Pagination();
            pagination.setMaxNumberResults(10);
            System.out.println("cheese");
            //pagination.setPageNumber(1);
            luReq.setPagination(pagination);
            luReq.setSortBy(UserSortField.UserKey);
            lu.setParameters(luReq);
            lu.setSearchQuery("*");
            System.out.println("clamp");            
            ListUsersResponse1 lur1 = this.UMClient.listUsers(lu);
            ListUsersResponse lur = lur1.getListUsersResult();
            ArrayOfUser aou = lur.getPagedResults();
            System.out.println("master");
            for(com.panopto.user.UserManagementStub.User user : aou.getUser())
            {
                u.addUser((User)PanoptoObjectFactory.getSerializablePanoptoObject(user, "User"));
            }
            System.out.println("henry");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return u;
    }
    
    @Path("{user}/")
    public UserResource getUser(@Context UriInfo uriInfo,/*@PathParam("user") String user,*/@HeaderParam("Authorization") String authHeader) throws WebApplicationException, org.apache.axis2.AxisFault
    {
        return new UserResource();
    }
}
