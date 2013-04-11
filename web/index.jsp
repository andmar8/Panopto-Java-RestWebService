<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
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
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Panopto REST</h1>
        https://panopto.example.com:8084/panoptoRest/resource/.....<br/>
        <br/>
        /folders/<br/>
        /folders/{folder}<br/>
        /folders/{folder}/accessDetails<br/>
        /folders/{folder}/sessions/<br/>
        /folders/{folder}/sessions/{session}<br/>
        /folders/{folder}/sessions/{session}/creator<br/>
        /folders/{folder}/sessions/{session}/folder<br/>
        /folders/{folder}/sessions/{session}/remoteRecorders/<br/>
        /folders/{folder}/sessions/{session}/remoteRecorders/{remoteRecorder}<br/>
        /groups/<br/>
        /groups/{groups}<br/>
        /groups/{groups}/accessDetails<br/>
        /remoteRecorders/<br/>
        /remoteRecorders/{remoteRecorder}<br/>
        /sessions/<br/>
        /sessions/{session}<br/>
        /sessions/{session}/accessDetails<br/>
        /sessions/{session}/creator<br/>
        /sessions/{session}/folder<br/>
        /sessions/{session}/remoteRecorders/<br/>
        /sessions/{session}/remoteRecorders/{remoteRecorder}<br/>
        /users/<br/>
        /users/{users}<br/>
        /users/{users}/accessDetails<br/>
    </body>
</html>
