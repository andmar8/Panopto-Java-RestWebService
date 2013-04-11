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
import javax.ws.rs.core.MediaType;

import com.panopto.remoterecorder.RemoteRecorderManagementStub;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.ArrayOfRemoteRecorder;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.ArrayOfguid;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.AuthenticationInfo;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.GetRemoteRecordersById;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.GetRemoteRecordersByIdResponse;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.Guid;
import com.panopto.remoterecorder.RemoteRecorderManagementStub.Pagination;
import panopto.resource.RemoteRecorder;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Path("remoteRecorders/{remoteRecorder}")
@Produces("application/xml")
public class RemoteRecorderResource extends BasicResource
{
    public RemoteRecorderResource() throws org.apache.axis2.AxisFault
    {
        this.RRMClient = new RemoteRecorderManagementStub();
        this.resource = "RemoteRecorder";
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public RemoteRecorder getRemoteRecorder(@Context UriInfo uriInfo,@PathParam("remoteRecorder") String remoteRecorderId, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey)
    {
        System.out.println("GET REMOTE RECORDER "+remoteRecorderId);
        AuthenticationInfo auth = (AuthenticationInfo)doSecurity(WSType.REMOTERECORDER, authCode, password, userKey);
        RemoteRecorder rR = new RemoteRecorder();
        try
        {
            GetRemoteRecordersById grrbi = new GetRemoteRecordersById();
            grrbi.setAuth(auth);
            ArrayOfguid aog = new ArrayOfguid();
            Guid id = new Guid();
            id.setGuid(remoteRecorderId);
            aog.addGuid(id);
            grrbi.setRemoteRecorderIds(aog);
            Pagination pagination = new Pagination();
            pagination.setMaxNumberResults(200);
            grrbi.setRemoteRecorderIds(aog);
            GetRemoteRecordersByIdResponse grrbir = this.RRMClient.getRemoteRecordersById(grrbi);
            ArrayOfRemoteRecorder aorr = grrbir.getGetRemoteRecordersByIdResult();
            for(com.panopto.remoterecorder.RemoteRecorderManagementStub.RemoteRecorder remoteRecorder : aorr.getRemoteRecorder())
            {
                rR = (RemoteRecorder)PanoptoObjectFactory.getSerializablePanoptoObject(remoteRecorder, "RemoteRecorder");
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return rR;
    }
}

