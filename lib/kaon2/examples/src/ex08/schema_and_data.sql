CREATE TABLE persons (
    personID int,
    name varchar (255),
    street varchar (255),
    zip int
);

CREATE TABLE parents (
    personID int,
    parentID int
);

CREATE TABLE equals (
    object1 int,
    object2 int
);

CREATE TABLE notequals (
    object1 int,
    object2 int
);

CREATE TABLE hu (
    objectID int
);

CREATE TABLE labels (
    personID int,
    label varchar(255)
);

INSERT INTO persons VALUES (1,'Peter','Loveland St. 1',1);
INSERT INTO persons VALUES (2,'Paul','Rushcreek Drive 4',15720);
INSERT INTO persons VALUES (3,'Mary','Rushcreek Drive 4',15720);

INSERT INTO parents VALUES (1,2);
INSERT INTO parents VALUES (1,3);

INSERT INTO persons VALUES (4,'Son','Penbrook Avenue 10',-1);
INSERT INTO persons VALUES (5,'Mum','Penbrook Avenue 10',-1);
INSERT INTO persons VALUES (6,'Dad','Homesick St. 10',20000);

INSERT INTO parents VALUES (4,5);
INSERT INTO parents VALUES (4,6);

INSERT INTO equals VALUES (1,7);
INSERT INTO equals VALUES (2,8);

INSERT INTO notequals VALUES (1,2);
INSERT INTO notequals VALUES (2,3);

INSERT INTO hu VALUES (1);
INSERT INTO hu VALUES (2);
INSERT INTO hu VALUES (3);
INSERT INTO hu VALUES (4);
INSERT INTO hu VALUES (5);
INSERT INTO hu VALUES (6);
INSERT INTO hu VALUES (7);
INSERT INTO hu VALUES (8);

INSERT INTO labels VALUES (1,'Label for Peter');
INSERT INTO labels VALUES (2,'Label for Paul');
