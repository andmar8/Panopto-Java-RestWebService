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
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

//rest
import com.panopto.session.SessionManagementStub;
import com.panopto.session.SessionManagementStub.AuthenticationInfo;
import com.panopto.session.SessionManagementStub.AddFolder;
import com.panopto.session.SessionManagementStub.AddFolderResponse;
import com.panopto.session.SessionManagementStub.GetFoldersList;
import com.panopto.session.SessionManagementStub.GetFoldersListResponse;
import com.panopto.session.SessionManagementStub.Guid;
import com.panopto.session.SessionManagementStub.ListFoldersRequest;
import com.panopto.session.SessionManagementStub.ListFoldersResponse;
import com.panopto.session.SessionManagementStub.Pagination;
import com.panopto.session.SessionManagementStub.UpdateFolderAllowPublicNotes;
import com.panopto.session.SessionManagementStub.UpdateFolderAllowSessionDownload;
import com.panopto.session.SessionManagementStub.UpdateFolderDescription;
import com.panopto.session.SessionManagementStub.UpdateFolderEnablePodcast;
import panopto.resource.Folder;
import panopto.resource.collections.impl.Folders;
import panopto.util.factory.object.PanoptoObjectFactory;
import rest.resources.panopto.enums.WSType;

@Path("folders")
@Produces("application/xml")
public class FoldersResource extends BasicResource
{
    public FoldersResource() throws org.apache.axis2.AxisFault
    {
        this.SMClient = new SessionManagementStub();
        this.resource = "Folders";
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Folders getFolders(@Context UriInfo uriInfo, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey) throws WebApplicationException
    {
        System.out.println("GET FOLDERS");
        AuthenticationInfo auth = (AuthenticationInfo)doSecurity(WSType.SESSION,authCode,password,userKey);
        Folders f = new Folders();
        try
        {
            GetFoldersList gfl = new GetFoldersList();
            gfl.setAuth(auth);
            ListFoldersRequest lfreq = new ListFoldersRequest();
            Pagination pagination = new Pagination();
            pagination.setMaxNumberResults(200);
            lfreq.setPagination(pagination);
//            lfreq.setParentFolderId(); -> Not setting this returns root of all folders
            lfreq.setPublicOnly(false);
            gfl.setRequest(lfreq);
            gfl.setSearchQuery("");
            GetFoldersListResponse gflr = this.SMClient.getFoldersList(gfl);
            ListFoldersResponse lfr = gflr.getGetFoldersListResult();
            System.out.println(lfr.getResults().getFolder().length);
            for(com.panopto.session.SessionManagementStub.Folder folder : lfr.getResults().getFolder())
            {
                f.addFolder((Folder)PanoptoObjectFactory.getSerializablePanoptoObject(folder, "Folder"));
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new javax.ws.rs.WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return f;
    }
    
    @POST
    @Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response setFolder(@Context UriInfo uriInfo, Folder folder, @HeaderParam("AuthCode") String authCode, @HeaderParam("Password") String password, @HeaderParam("UserKey") String userKey) throws WebApplicationException, URISyntaxException, RemoteException
    {
        System.out.println("POST FOLDER");
//        this.doSecurity(authCode, password, userKey);

        com.panopto.session.SessionManagementStub.Folder f = null;
        AuthenticationInfo auth = (AuthenticationInfo)doSecurity(WSType.SESSION,authCode,password,userKey);
        try
        {
            AddFolder af = new AddFolder();
            af.setAuth(auth);
            af.setIsPublic(folder.getIsPublic());
            af.setName(folder.getName());
            Guid id = new Guid();
            id.setGuid(folder.getParentFolder());
            af.setParentFolder(id);
            AddFolderResponse afr = this.SMClient.addFolder(af);
            f = afr.getAddFolderResult();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            //e.printStackTrace();
            if(e.getMessage().contains("already exists"))
            {
                return Response.notModified().build();
            }
            else
            {
                return Response.serverError().build();
            }
        }
        try
        {
            UpdateFolderAllowPublicNotes ufapn = new UpdateFolderAllowPublicNotes();
            ufapn.setAuth(auth);
            ufapn.setAllowPublicNotes(folder.getAllowPublicNotes());
            ufapn.setFolderId(f.getId());
            this.SMClient.updateFolderAllowPublicNotes(ufapn);
            UpdateFolderAllowSessionDownload ufasd = new UpdateFolderAllowSessionDownload();
            ufasd.setAuth(auth);
            ufasd.setFolderId(f.getId());
            ufasd.setAllowSessionDownload(folder.getAllowSessionDownload());
            this.SMClient.updateFolderAllowSessionDownload(ufasd);
            UpdateFolderDescription ufd = new UpdateFolderDescription();
            ufd.setAuth(auth);
            ufd.setDescription(folder.getDescription());
            ufd.setFolderId(f.getId());
            this.SMClient.updateFolderDescription(ufd);
            UpdateFolderEnablePodcast ufep = new UpdateFolderEnablePodcast();
            ufep.setAuth(auth);
            ufep.setEnablePodcast(folder.getEnablePodcast());
            ufep.setFolderId(f.getId());
            this.SMClient.updateFolderEnablePodcast(ufep);
        }
        catch(Exception e)
        {
            System.out.println(e);
            //e.printStackTrace();
            return Response.serverError().build();
        }
        return Response.created(new URI("/folders/"+f.getId())).build();
    }
}