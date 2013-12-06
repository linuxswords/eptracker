This is a Play 2.2 application for tracking tv-episodes
=======================================================

Description
-----------

Keep track which episode from your favorite tv-shows you already have seen. Also see a list of recent episodes that have been aired and how many
days to the next release....



Requirements:
-------------

- scala
- awk installed
- play! framework 2.2.1
- mysql-database although other might work also

start with 'play run' on the commandline, fire up localhost:9000 and start importing and flagging shows

Configuration
-------------

In the [conf/application.conf](/conf/application.conf) file

`torrent.searchstring` -- is the prefix used to create torrent search strings

    # this url will be used to build torrent search urls
    torrent.searchstring="http://kickass.to/torrents/search/?q="

`picture.storage` -- absolute path to the location to store file

    # this is where we will store the pictures
    picture.storage="/opt/eptracker"

Data-Sources
------------

- [allshows.txt](/allshows.txt) -- downloadable from http://epguide.com, will be used for the import text field autocomplete
- episodes and show -- will be imported into configured database, data comes from the http://epguide.com/`showID` and will be parsed by regex
- show description -- freebase.com search
- episode description -- freebase.com search


TODO
----

- get the pictures in another format, other license
- handle when picture location is crappy
- rewrite import to another source as freebase.com or openmoviedatabase.org
- make this application user aware. create accounts etc.


License
--------

Most content comes from pages in the web and freebase.com and available under Creative Commons Attribution Only (or CC-BY) license.

This app itself is available under GPL

