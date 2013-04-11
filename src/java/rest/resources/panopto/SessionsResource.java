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
import com.panopto.session.SessionManagementStub;
import com.panopto.session.SessionManagementStub.ArrayOfSession;
import com.panopto.session.SessionManagementStub.AuthenticationInfo;
import com.panopto.session.SessionManagementStub.GetSessionsList;
import com.panopto.session.SessionManagementStub.GetSessionsListResponse;
import com.panopto.session.SessionManagementStub.Guid;
import com.panopto.session.SessionManagementStub.ListSessionsRequest;
import com.panopto.session.SessionManagementStub.ListSessionsResponse;
import com.panopto.session.SessionManagementStub.Pagination;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import panopto.resource.Session;
import panopto.resource.collections.impl.Sessions;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Path("sessions")
@Produces("application/xml")
public class SessionsResource extends BasicResource
{
    private String eid;
    
    public SessionsResource() throws org.apache.axis2.AxisFault
    {
        this.SMClient = new SessionManagementStub();
        this.resource = "sessions";
        this.eid = null;
    }

    public SessionsResource(String id) throws org.apache.axis2.AxisFault
    {
        this();
        this.eid = id;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Sessions getSessions(@Context UriInfo uriInfo, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey) throws WebApplicationException
    {
        System.out.println("GET SESSIONS");
        AuthenticationInfo auth = (AuthenticationInfo)doSecurity(WSType.SESSION, authCode, password, userKey);
        Sessions s = new Sessions();
        try
        {
            GetSessionsList gsl = new GetSessionsList();
            gsl.setAuth(null);
            ListSessionsRequest lsreq = new ListSessionsRequest();            
            Pagination pagination = new Pagination();
            pagination.setMaxNumberResults(200);
            if(this.eid!=null)
            {
                Guid id = new Guid();
                id.setGuid(this.eid);
                lsreq.setFolderId(id);
            }
            lsreq.setPagination(pagination);
            gsl.setRequest(lsreq);
            gsl.setSearchQuery("");
            GetSessionsListResponse gslr = this.SMClient.getSessionsList(gsl);
            ListSessionsResponse lsr = gslr.getGetSessionsListResult();
            ArrayOfSession aos = lsr.getResults();
            for(com.panopto.session.SessionManagementStub.Session session : aos.getSession())
            {
                s.addSession((Session)PanoptoObjectFactory.getSerializablePanoptoObject(session, "Session"));
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return s;
    }
    
    @Path("{session}/")
    public SessionResource getSession(@Context UriInfo uriInfo,/*@PathParam("user") String user,*/@HeaderParam("Authorization") String authHeader) throws WebApplicationException, org.apache.axis2.AxisFault
    {
        return new SessionResource();
    }
}
