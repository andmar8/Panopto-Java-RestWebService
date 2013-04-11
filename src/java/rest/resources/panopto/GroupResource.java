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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import com.panopto.user.UserManagementStub;
import com.panopto.user.UserManagementStub.AuthenticationInfo;
import com.panopto.user.UserManagementStub.GetGroup;
import com.panopto.user.UserManagementStub.GetGroupResponse;
import com.panopto.user.UserManagementStub.Guid;
import panopto.resource.Group;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Path("groups/{group}")
@Produces("application/xml")
public class GroupResource extends BasicResource
{
    public GroupResource() throws org.apache.axis2.AxisFault
    {
        this.UMClient = new UserManagementStub();
        this.resource = "group";
    }

    @Path("accessDetails")
    public GroupAccessDetailResource getGroupAccessDetails(@Context UriInfo uriInfo,@PathParam("group") String groupId,@HeaderParam("Authorization") String authHeader) throws WebApplicationException, org.apache.axis2.AxisFault
    {
        System.out.println("GET GROUP ACCESS DETAILS FOR GROUP "+groupId);
        return new GroupAccessDetailResource(groupId);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Group getGroup(@Context UriInfo uriInfo,@PathParam("group") String groupId, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey)
    {
        System.out.println("GET GROUP "+groupId);
        AuthenticationInfo auth = (AuthenticationInfo)this.doSecurity(WSType.USER, authCode, password, userKey);
        Group g = new Group();
        try
        {
            GetGroup gg = new GetGroup();
            gg.setAuth(auth);
            Guid id = new Guid();
            id.setGuid(groupId);
            gg.setGroupId(id);
            GetGroupResponse ggr = this.UMClient.getGroup(gg);
            g = (Group)PanoptoObjectFactory.getSerializablePanoptoObject(ggr.getGetGroupResult(), "Group");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return g;
    }
}

