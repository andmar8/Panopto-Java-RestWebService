Panopto-Java-RestWebService
===========================

Nuances/deficiencies
--------------------

At the moment the root of resources that lists all of a given entity, e.g. /folders, /users etc..., is unbounded and does not have an interface to change the pagination settings, this could be added fairly quickly using QueryParams or HeaderParams to say something like "I want 300 results, 10 per page, give me page 2"

The unbound root resources, likely, will timeout on your server if you change the pagination beyond a couple of hundred records, this was never particularly a problem for me as I'd usually found the id of the entity initially anyway and worked from there, i.e. instead of looking for a folder by listing /folders, then taking the id and doing a /folders/0000-0000-000, I would usually have the folder Id to begin with from the GUI; you may think this slightly defeats the point, but most of what I was doing was not finding the initial id's of thing but trying to find what relates to it and what their id's are, so for example, what sessions are linked to a given folder, or what is the id of the folder a session is in.
