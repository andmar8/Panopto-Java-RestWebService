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
import com.panopto.remoterecorder.RemoteRecorderManagementStub;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.ArrayOfRemoteRecorder;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.ArrayOfguid;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.AuthenticationInfo;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.GetRemoteRecordersById;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.GetRemoteRecordersByIdResponse;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.Guid;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.ListRecorders;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.ListRecordersResponse;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.ListRecordersResponse0;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.Pagination;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import javax.ws.rs.core.MediaType;
import panopto.resource.RemoteRecorder;
import panopto.resource.Session;
import panopto.resource.collections.impl.Ids;
import panopto.resource.collections.impl.RemoteRecorders;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Path("remoteRecorders")
@Produces("application/xml")
public class RemoteRecordersResource extends BasicResource
{
    private String eid;
    
    public RemoteRecordersResource() throws org.apache.axis2.AxisFault
    {
        this.RRMClient = new RemoteRecorderManagementStub();
        this.resource = "remoteRecorders";
        this.eid = null;
    }

    public RemoteRecordersResource(String id) throws org.apache.axis2.AxisFault
    {
        this();
        this.eid = id;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public RemoteRecorders getRemoteRecorders(@Context UriInfo uriInfo, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey) throws WebApplicationException
    {
        System.out.println("GET REMOTE RECORDERS");
        AuthenticationInfo auth = (AuthenticationInfo)this.doSecurity(WSType.REMOTERECORDER, authCode, password, userKey);
        RemoteRecorders rR = new RemoteRecorders();
        try
        {
            com.panopto.remoterecorder.RemoteRecorderManagementStub.RemoteRecorder[] pRR;
            if(this.eid!=null)
            {
                GetRemoteRecordersById grrbi = new GetRemoteRecordersById();
                grrbi.setAuth(auth);
                //eid==session
                Session s = new SessionResource().getSession(null, eid, authCode, password, userKey);
                Ids ids = s.getRemoteRecorderIds();
                ArrayOfguid aog = new ArrayOfguid();
                for(String id : ids.getStringArray())
                {
                    Guid guid = new Guid();
                    guid.setGuid(id);
                    aog.addGuid(guid);
                }
                grrbi.setRemoteRecorderIds(aog);
                Pagination pagination = new Pagination();
                pagination.setMaxNumberResults(200);
                grrbi.setRemoteRecorderIds(aog);
                GetRemoteRecordersByIdResponse grrbir = this.RRMClient.getRemoteRecordersById(grrbi);
                ArrayOfRemoteRecorder aorr = grrbir.getGetRemoteRecordersByIdResult();
                pRR = aorr.getRemoteRecorder();
            }
            else
            {
                ListRecorders lr = new ListRecorders();
                lr.setAuth(auth);
                Pagination pagination = new Pagination();
                pagination.setMaxNumberResults(200);
                lr.setPagination(pagination);
                lr.setSortBy(RemoteRecorderManagementStub.RecorderSortField.Name);
                ListRecordersResponse0 lrr0 = this.RRMClient.listRecorders(lr);
                ListRecordersResponse lrr = lrr0.getListRecordersResult();
                ArrayOfRemoteRecorder aorr = lrr.getPagedResults();
                pRR = aorr.getRemoteRecorder();
            }
            for(com.panopto.remoterecorder.RemoteRecorderManagementStub.RemoteRecorder remoteRecorder : pRR)
            {
                rR.addRemoteRecorder((RemoteRecorder)PanoptoObjectFactory.getSerializablePanoptoObject(remoteRecorder, "RemoteRecorder"));
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return rR;
    }
    
    @Path("{remoteRecorder}/")
    public RemoteRecorderResource getRemoteRecorder(@Context UriInfo uriInfo,/*@PathParam("user") String user,*/@HeaderParam("Authorization") String authHeader) throws WebApplicationException, org.apache.axis2.AxisFault
    {
        return new RemoteRecorderResource();
    }
}
