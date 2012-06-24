# Media schema

# --- !Ups

create table Media(
  id integer primary key auto_increment,
  showid varchar(255),
  publishingDate timestamp,
  author varchar(255),
  title varchar(255),
  subtitle varchar(255),
  identifier varchar(255),
  description varchar(255),
  consumed bit,
  media_parent integer
--   constraint u_show UNIQUE (title,identifier,subtitle)
);
 ;
create view TVShow as
select title, count(*) as 'count'
from Media
group by title
order by title asc;


insert into Media(consumed,title,subtitle,identifier,publishingDate, showid) values(1, 'Game of Thrones', 'Winter is Coming', 's01e01','2011-04-17', 'GameOfThrones');

insert into Media(consumed,title,subtitle,identifier,publishingDate, showid) select 1, 'Game of Thrones', 'Winter is Coming', 's01e01','2011-04-18', 'GameOfThrones'
from Media
where not exists (select * from Media where title = 'Game of Thrones' and identifier = 's01e01');


insert into Media(consumed,title,subtitle,identifier,publishingDate, showid) values(1, 'Game of Thrones', 'The Kingsroad', 's01e02','2011-04-24', 'GameOfThrones');
insert into Media(consumed,title,subtitle,identifier,publishingDate, showid) values(1, 'Game of Thrones', 'Lord Snow', 's01e03','2011-05-01', 'GameOfThrones');
insert into Media(consumed,title,subtitle,identifier,publishingDate, showid) values(1, 'Game of Thrones', 'Cripples, Bastards, and Broken Things', 's01e04','2011-05-08', 'GameOfThrones');
insert into Media(consumed,title,subtitle,identifier,publishingDate, showid) values(1, 'Game of Thrones', 'The Wolf and the Lion', 's01e05','2011-05-15', 'GameOfThrones');
insert into Media(consumed,title,subtitle,identifier,publishingDate, showid) values(1, 'Game of Thrones', 'A Golden Crown', 's01e06','2011-05-22', 'GameOfThrones');
insert into Media(consumed,title,subtitle,identifier,publishingDate, showid) values(1, 'Game of Thrones', 'You Win or You Die', 's01e07','2011-05-29', 'GameOfThrones');


# --- !Downs

Drop table Media;
Drop view TVShow;
