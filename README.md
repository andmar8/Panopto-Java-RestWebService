Panopto-Java-RestWebService
===========================

A simple Rest web service to expose most of the Panopto API.

This is a pre-cursor to the ExternalIdTool, it allows you to see, via a Java container server (i.e. Tomcat et al), the Panopto API and plain XML (but could be adapted to JSON), the data inside Panopto. This can be more useful than the Panopto GUI itself when developing/testing

Please see the index.jsp for supported resources, most things are GET-able, a couple are POST-able, but the POST code is likely to be nowhere near as mature as the GET code, my intent was only really to get at the raw data more easily then the GUI, so updating stuff wasn't really a huge concern when I was writing this.

Requirements
------------

* Axis
* Jersey
* [PanoptoHelper](https://github.com/andmar8/Panopto-Java-RestWebServiceHelper) library
* [PanoptoSerializableObjects](https://github.com/andmar8/Panopto-Java-SerializableObjects) library
* [PanoptoAxis](https://github.com/andmar8/Panopto-Java-Axis) stubs library

How to compile
--------------



How to use
----------

Once compiled and deployed to your java container (i.e. Tomcat), you can find a bit of resource documenation at the index page, but in general everything is under...

<pre>
	http://yourtomcat.com:<yourport>/panoptoRest/resources/....
</pre>

e.g.

<pre>
	http://localhost:8084/panoptoRest/resources/folders/
</pre>

Nuances/deficiencies
--------------------

At the moment the root of resources that lists all of a given entity, e.g. /folders, /users etc..., is unbounded and does not have an interface to change the pagination settings, this could be added fairly quickly using QueryParams or HeaderParams to say something like "I want 300 results, 10 per page, give me page 2"

The unbound root resources, likely, will timeout on your server if you change the pagination beyond a couple of hundred records, this was never particularly a problem for me as I'd usually found the id of the entity initially anyway and worked from there, i.e. instead of looking for a folder by listing /folders, then taking the id and doing a /folders/0000-0000-000, I would usually have the folder Id to begin with from the GUI; you may think this slightly defeats the point, but most of what I was doing was not finding the initial id's of thing but trying to find what relates to it and what their id's are, so for example, what sessions are linked to a given folder, or what is the id of the folder a session is in.
