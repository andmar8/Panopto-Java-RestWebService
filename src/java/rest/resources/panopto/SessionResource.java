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
import com.panopto.session.SessionManagementStub;
import com.panopto.session.SessionManagementStub.ArrayOfSession;
import com.panopto.session.SessionManagementStub.AuthenticationInfo;
import com.panopto.session.SessionManagementStub.GetSessionsList;
import com.panopto.session.SessionManagementStub.GetSessionsListResponse;
import com.panopto.session.SessionManagementStub.ListSessionsRequest;
import com.panopto.session.SessionManagementStub.ListSessionsResponse;
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
import panopto.resource.Session;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Path("sessions/{session}")
@Produces("application/xml")
public class SessionResource extends BasicResource
{
    public SessionResource() throws org.apache.axis2.AxisFault
    {
        this.SMClient = new SessionManagementStub();
        this.resource = "Session";
    }

    @Path("accessDetails")
    public SessionAccessDetailResource getSessionAccessDetails(@Context UriInfo uriInfo,@PathParam("session") String sessionId) throws WebApplicationException, org.apache.axis2.AxisFault
    {
        System.out.println("GET SESSION ACCESS DETAILS FOR SESSION "+sessionId);
        return new SessionAccessDetailResource(sessionId);
    }

    @Path("creator")
    public UserResource getCreatorOfSession(@Context UriInfo uriInfo,@PathParam("session") String sessionId, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey) throws WebApplicationException, org.apache.axis2.AxisFault
    {
        System.out.println("Creator for Session: "+sessionId);
        Session s = this.getSession(null, sessionId, authCode, password, userKey);
        return new UserResource(s.getCreatorId());
    }

    @Path("folder")
    public FolderResource getFolderForSession(@Context UriInfo uriInfo,@PathParam("session") String sessionId, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey) throws WebApplicationException, org.apache.axis2.AxisFault
    {
        System.out.println("Folder for Session: "+sessionId);
        Session s = this.getSession(null, sessionId, authCode, password, userKey);
        return new FolderResource(s.getFolderId());
    }

    @Path("remoteRecorders")
    public RemoteRecordersResource getRemoteRecorders(@Context UriInfo uriInfo,@PathParam("session") String sessionId, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey) throws WebApplicationException, org.apache.axis2.AxisFault
    {
        System.out.println("Remote Recorders for Session: "+sessionId);
        return new RemoteRecordersResource(sessionId);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Session getSession(@Context UriInfo uriInfo,@PathParam("session") String sessionId, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey)
    {
        System.out.println("GET SESSION "+sessionId);
        AuthenticationInfo auth = (AuthenticationInfo)doSecurity(WSType.SESSION, resource, sessionId, resource);
        Session s = new Session();
        try
        {
            GetSessionsList gsl = new GetSessionsList();
            gsl.setAuth(auth);
            gsl.setRequest(new ListSessionsRequest());
            gsl.setSearchQuery(sessionId);
            GetSessionsListResponse gslr = this.SMClient.getSessionsList(gsl);
            ListSessionsResponse lsr = gslr.getGetSessionsListResult();
            ArrayOfSession aor = lsr.getResults();
            for(com.panopto.session.SessionManagementStub.Session session : aor.getSession())
            {
                s = (Session)PanoptoObjectFactory.getSerializablePanoptoObject(session, "Session");
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return s;
    }
}

