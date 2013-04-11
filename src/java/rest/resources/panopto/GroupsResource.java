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

import com.panopto.user.UserManagementStub;
import com.panopto.user.UserManagementStub.AuthenticationInfo;
import com.panopto.user.UserManagementStub.ListGroups;
import com.panopto.user.UserManagementStub.ListGroupsResponse;
import com.panopto.user.UserManagementStub.ListGroupsResponse0;
import com.panopto.user.UserManagementStub.Pagination;
import panopto.resource.Group;
import panopto.resource.collections.impl.Groups;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Path("groups")
@Produces("application/xml")
public class GroupsResource extends BasicResource
{
    public GroupsResource() throws org.apache.axis2.AxisFault
    {
        this.UMClient = new UserManagementStub();
        this.resource = "groups";
    }

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Groups getGroups(@Context UriInfo uriInfo, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey) throws WebApplicationException
    {
        System.out.println("GET GROUPS");
        AuthenticationInfo auth = (AuthenticationInfo)this.doSecurity(WSType.USER, authCode, password, userKey);
        Groups g = new Groups();
        try
        {
            ListGroups lg = new ListGroups();
            Pagination pagination = new Pagination();
            pagination.setMaxNumberResults(200);
            lg.setAuth(auth);
            lg.setPagination(pagination);
            ListGroupsResponse0 lgr0 = this.UMClient.listGroups(lg);
            ListGroupsResponse lgr = lgr0.getListGroupsResult();
            for(com.panopto.user.UserManagementStub.Group group : lgr.getPagedResults().getGroup())
            {
                g.addGroup((Group)PanoptoObjectFactory.getSerializablePanoptoObject(group, "Group"));
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return g;
    }
    
    @Path("{group}/")
    public GroupResource getGroup(@Context UriInfo uriInfo,/*@PathParam("user") String user,*/@HeaderParam("Authorization") String authHeader) throws WebApplicationException, org.apache.axis2.AxisFault
    {
        return new GroupResource();
    }
}
