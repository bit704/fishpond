/*
gsql -d fishpond -p 26000

gsql -d fishpond -U mhn -p 26000 -r
fpDbMhn#
*/

create table fishpond.user(
uid SERIAL primary key,
password CHAR(100) NOT NULL,
question NVARCHAR2(100) NOT NULL,
answer NVARCHAR2(100) NOT NULL
);

select setval('fishpond.user_uid_seq',10000000,true);

create table fishpond.userinfo(
uid INTEGER references fishpond.user(uid),
name NVARCHAR2(20) NOT NULL,
sex CHAR(1),
birthday DATE,
region NVARCHAR2(20),
state BOOLEAN NOT NULL,
create_time DATE,
last_offline DATE,
real BOOLEAN
); 


create table fishpond.groupinfo(
gid SERIAL  primary key,
name NVARCHAR2(20) NOT NULL,
creator INTEGER references fishpond.user(uid),
create_time DATE NOT NULL,
manager INTEGER references fishpond.user(uid),
real BOOLEAN Not Null
);

select setval('fishpond.groupinfo_uid_seq',10000000,true);

create table fishpond.friendship(
uid1 INTEGER,
uid2 INTEGER,
friend_time DATE NOT NULL,
primary key (uid1,uid2)
);


create table fishpond.friendrequest(
requester INTEGER references fishpond.user(uid),
receiver INTEGER references fishpond.user(uid),
request_time DATE NOT NULL,
explanation NVARCHAR2(100),
primary key(requester, receiver)
);

create table fishpond.message(
sender INTEGER references fishpond.user(uid),
receiver INTEGER references fishpond.user(uid),
send_time DATE NOT NULL,
mtype TINYINT,
content TEXT NOT NULL
);

create table fishpond.groupmessage(
sender INTEGER references fishpond.user(uid),
receiver INTEGER references fishpond.groupinfo(gid),
send_time DATE NOT NULL,
mtype TINYINT,
content TEXT NOT NULL
);

create table fishpond.groupmember(
gid INTEGER references fishpond.groupinfo(gid),
memberID INTEGER references fishpond.user(uid),
invitorID INTEGER references fishpond.user(uid),
intime DATE Not Null,
primary key(gid,memberID)
);

create table fishpond.sysmessage(
uid INTEGER references fishpond.user(uid),
send_time DATE NOT NULL,
mtype TINYINT,
content TEXT NOT NULL
);
 
