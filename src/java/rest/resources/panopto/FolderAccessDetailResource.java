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
import com.panopto.access.AccessManagementStub.GetFolderAccessDetails;
import com.panopto.access.AccessManagementStub.GetFolderAccessDetailsResponse;
import com.panopto.access.AccessManagementStub.Guid;
import panopto.resource.FolderAccessDetail;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Produces("application/xml")
public class FolderAccessDetailResource extends BasicResource
{
    private String eid;
    
    public FolderAccessDetailResource() throws org.apache.axis2.AxisFault
    {
        this.AMClient = new AccessManagementStub();
        this.resource = "FolderAccessDetail";
        this.eid = null;
    }

    public FolderAccessDetailResource(String id) throws org.apache.axis2.AxisFault
    {
        this();
        this.eid = id;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public FolderAccessDetail getFolderAccessDetail(@Context UriInfo uriInfo, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey)
    {
        System.out.println("GET FOLDER ACCESS DETAILS "+this.eid);
        AuthenticationInfo auth = (AuthenticationInfo)doSecurity(WSType.ACCESS, authCode, password, userKey);
        FolderAccessDetail f = new FolderAccessDetail();
        try
        {
            GetFolderAccessDetails gfad = new GetFolderAccessDetails();
            gfad.setAuth(auth);
            Guid id = new Guid();
            id.setGuid(this.eid);
            gfad.setFolderId(id);
            GetFolderAccessDetailsResponse gfadr = this.AMClient.getFolderAccessDetails(gfad);
            f = (FolderAccessDetail)PanoptoObjectFactory.getSerializablePanoptoObject(gfadr.getGetFolderAccessDetailsResult(), "FolderAccessDetail");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return f;
    }
}

