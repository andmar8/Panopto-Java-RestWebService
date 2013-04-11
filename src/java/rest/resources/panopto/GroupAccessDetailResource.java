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
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import com.panopto.access.AccessManagementStub;
import com.panopto.access.AccessManagementStub.AuthenticationInfo;
import com.panopto.access.AccessManagementStub.GetGroupAccessDetails;
import com.panopto.access.AccessManagementStub.GetGroupAccessDetailsResponse;
import com.panopto.access.AccessManagementStub.Guid;
import panopto.resource.GroupAccessDetail;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Produces("application/xml")
public class GroupAccessDetailResource extends BasicResource
{
    private String eid;
    
    public GroupAccessDetailResource() throws org.apache.axis2.AxisFault
    {
        this.AMClient = new AccessManagementStub();
        this.resource = "GroupAccessDetail";
        this.eid = null;
    }

    public GroupAccessDetailResource(String id) throws org.apache.axis2.AxisFault
    {
        this();
        this.eid = id;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public GroupAccessDetail getGroupAccessDetail(@Context UriInfo uriInfo, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey)
    {
        System.out.println("GET GROUP ACCESS DETAILS "+this.eid);
        AuthenticationInfo auth = (AuthenticationInfo)doSecurity(WSType.ACCESS,authCode,password,userKey);
        GroupAccessDetail g = new GroupAccessDetail();
        try
        {
            GetGroupAccessDetails ggad = new GetGroupAccessDetails();
            ggad.setAuth(auth);
            Guid id = new Guid();
            id.setGuid(this.eid);
            ggad.setGroupId(id);
            GetGroupAccessDetailsResponse ggadr = this.AMClient.getGroupAccessDetails(ggad);
            g = (GroupAccessDetail)PanoptoObjectFactory.getSerializablePanoptoObject(ggadr.getGetGroupAccessDetailsResult(), "GroupAccessDetail");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return g;
    }
}

