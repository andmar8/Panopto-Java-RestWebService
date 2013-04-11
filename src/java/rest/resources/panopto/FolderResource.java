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
import com.panopto.session.SessionManagementStub;
import com.panopto.session.SessionManagementStub.ArrayOfFolder;
import com.panopto.session.SessionManagementStub.AuthenticationInfo;
import com.panopto.session.SessionManagementStub.GetFoldersList;
import com.panopto.session.SessionManagementStub.GetFoldersListResponse;
import com.panopto.session.SessionManagementStub.ListFoldersRequest;
import com.panopto.session.SessionManagementStub.ListFoldersResponse;
import com.panopto.session.SessionManagementStub.Pagination;
import org.apache.axis2.AxisFault;
import panopto.resource.Folder;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Path("folders/{folder}")
@Produces("application/xml")
public class FolderResource extends BasicResource
{
    private String eid;
    
    public FolderResource() throws org.apache.axis2.AxisFault
    {
        this.SMClient = new SessionManagementStub();
        this.resource = "Folder";
        this.eid = null;
    }

    public FolderResource(String id) throws org.apache.axis2.AxisFault
    {
        this();
        this.eid = id;
    }

    @Path("accessDetails")
    public FolderAccessDetailResource getFolderAccessDetails(@Context UriInfo uriInfo,@PathParam("folder") String folderId,@HeaderParam("Authorization") String authHeader) throws WebApplicationException, org.apache.axis2.AxisFault
    {
        System.out.println("GET FOLDER ACCESS DETAILS FOR FOLDER "+folderId);
        return new FolderAccessDetailResource(folderId);
    }
    
    @Path("sessions")
    public SessionsResource getSessions(@Context UriInfo uriInfo,@PathParam("folder") String folderId,@HeaderParam("Authorization") String authHeader) throws WebApplicationException, AxisFault//, ServiceException
    {
        return new SessionsResource(folderId);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Folder getFolder(@Context UriInfo uriInfo,@PathParam("folder") String folderId, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey)
    {
        System.out.println("GET FOLDER "+(this.eid!=null?this.eid:folderId));
        AuthenticationInfo auth = (AuthenticationInfo)doSecurity(WSType.SESSION,authCode,password,userKey);
        Folder f = new Folder();
        try
        {
            GetFoldersList gfl = new GetFoldersList();
            gfl.setAuth(auth);
            ListFoldersRequest lfreq = new ListFoldersRequest();
            Pagination pagination = new Pagination();
            pagination.setMaxNumberResults(200);
            lfreq.setPagination(pagination);
            lfreq.setPublicOnly(false);
            gfl.setRequest(lfreq);
            gfl.setSearchQuery((this.eid!=null?this.eid:folderId));
            GetFoldersListResponse gflr = this.SMClient.getFoldersList(gfl);
            ListFoldersResponse lfr = gflr.getGetFoldersListResult();
            ArrayOfFolder aof = lfr.getResults();
            com.panopto.session.SessionManagementStub.Folder[] fs = aof.getFolder();
            for(com.panopto.session.SessionManagementStub.Folder folder : fs)
            {
                f = (Folder)PanoptoObjectFactory.getSerializablePanoptoObject(folder, "Folder");
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return f;
    }
}
