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
import com.panopto.access.AccessManagementStub;
import com.panopto.access.AccessManagementStub.AuthenticationInfo;
import com.panopto.access.AccessManagementStub.GetSessionAccessDetails;
import com.panopto.access.AccessManagementStub.GetSessionAccessDetailsResponse;
import com.panopto.access.AccessManagementStub.Guid;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import panopto.resource.SessionAccessDetail;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Produces("application/xml")
public class SessionAccessDetailResource extends BasicResource
{
    private String eid;
    
    public SessionAccessDetailResource() throws org.apache.axis2.AxisFault
    {
        this.AMClient = new AccessManagementStub();
        this.resource = "SessionAccessDetail";
        this.eid = null;
    }

    public SessionAccessDetailResource(String id) throws org.apache.axis2.AxisFault
    {
        this();
        this.eid = id;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public SessionAccessDetail getSessionAccessDetail(@Context UriInfo uriInfo, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey)
    {
        System.out.println("GET SESSION ACCESS DETAILS "+this.eid);
        AuthenticationInfo auth = (AuthenticationInfo)doSecurity(WSType.ACCESS, authCode, password, userKey);
        SessionAccessDetail s = new SessionAccessDetail();
        try
        {
            GetSessionAccessDetails gsad = new GetSessionAccessDetails();
            gsad.setAuth(auth);
            Guid id = new Guid();
            id.setGuid(this.eid);
            gsad.setSessionId(id);
            GetSessionAccessDetailsResponse gsadr = this.AMClient.getSessionAccessDetails(gsad);
            s = (SessionAccessDetail)PanoptoObjectFactory.getSerializablePanoptoObject(gsadr.getGetSessionAccessDetailsResult(), "SessionAccessDetail");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return s;
    }
}

