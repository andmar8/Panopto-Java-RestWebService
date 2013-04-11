Panopto-Java-RestWebService
===========================

A simple Rest web service to expose most of the Panopto API.

This is a pre-cursor to the [ExternalIdTool](https://github.com/andmar8/Panopto-Java-ExternalIdTool), it allows you to see, via a Java container server (i.e. Tomcat et al), the Panopto API and plain XML (but could be adapted to JSON), the data inside Panopto. This can be more useful than the Panopto GUI itself when developing/testing

Please see the index.jsp for supported resources, most things are GET-able, a couple are POST-able, but the POST code is likely to be nowhere near as mature as the GET code, my intent was only really to get at the raw data more easily then the GUI, so updating stuff wasn't really a huge concern when I was writing this.

Requirements
------------

* Axis (I've used 1.6.2)
* Jersey (I've used 1.17)
* [PanoptoHelper](https://github.com/andmar8/Panopto-Java-RestWebServiceHelper) library
* [PanoptoSerializableObjects](https://github.com/andmar8/Panopto-Java-SerializableObjects) library
* [PanoptoAxis](https://github.com/andmar8/Panopto-Java-Axis) stubs library

How to compile
--------------

Follow the instructions for the respective required libraries to compile up their jars and then add them to the project like so (the names of the jars/libraries are as an example)...

1. Axis2-1.6.2
2. Jersey1.17
3. PanoptoAxis1.6.2.jar
4. PanoptoSerializableObjects.jar
5. PanoptoHelper.jar

All dependancies in the code should now be satisfied, so you can compile to a war and deploy to your tomcat container

How to use
----------

Once compiled and deployed to your java container (i.e. Tomcat), you can find a bit of resource documentation at the [index](https://github.com/andmar8/Panopto-Java-RestWebService/blob/master/web/index.jsp) page, but in general everything is under...

<pre>
	http://yourtomcat.com:&lt;yourport&gt;/panoptoRest/resources/....
</pre>

e.g.

<pre>
	http://localhost:8084/panoptoRest/resources/folders/
</pre>

Note: For testing purposes, you may want to set a default user/password or authcode in the code so you don't have to send that in the headers for each request, see the comment [here](https://github.com/andmar8/Panopto-Java-RestWebService/blob/master/src/java/rest/resources/panopto/BasicResource.java#L43) for a little extra on how to do that

Nuances/deficiencies
--------------------

At the moment the root of resources that lists all of a given entity, e.g. /folders, /users etc..., is unbounded and does not have an interface to change the pagination settings, this could be added fairly quickly using QueryParams or HeaderParams to say something like "I want 300 results, 10 per page, give me page 2"

The unbound root resources, likely, will timeout on your server if you change the pagination beyond a couple of hundred records, this was never particularly a problem for me as I'd usually found the id of the entity initially anyway and worked from there, i.e. instead of looking for a folder by listing /folders, then taking the id and doing a /folders/0000-0000-000, I would usually have the folder Id to begin with from the GUI; you may think this slightly defeats the point, but most of what I was doing was not finding the initial id's of thing but trying to find what relates to it and what their id's are, so for example, what sessions are linked to a given folder, or what is the id of the folder a session is in.

When compiling and deploying, it seems quite normal for the container to memory leak and you may have to fully restart the server to deploy some code you are testing, I've read in forums this is something the Jersey team are working on, but, don't worry too much, if you are not editing the code too much (or at all!) then you will probably never see this problem; apparently tomcat 7 doesn't exhibit this problem, but I've never tried it! For clarity, what I regularly see in the Catalina.log is something akin to...

<pre>
SEVERE: A web application created a ThreadLocal with key of type [null] (value [com.sun.xml.bind.v2.ClassFactory$1@bd61d4]) and a value of type [java.util.WeakHashMap] (value [{class javax.xml.bind.annotation.W3CDomHandler=java.lang.ref.WeakReference@161df1f}]) but failed to remove it when the web application was stopped. To prevent a memory leak, the ThreadLocal has been forcibly removed.
</pre>