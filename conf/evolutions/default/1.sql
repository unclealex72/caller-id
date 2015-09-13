# Users schema

# --- !Ups

create table "users" ("id" SERIAL NOT NULL PRIMARY KEY,"email" VARCHAR NOT NULL);
create unique index "unique_email_index" on "users" ("email");
create table "phonenumbers" ("id" SERIAL NOT NULL PRIMARY KEY,"number" VARCHAR NOT NULL,"type" VARCHAR,"contact_id" INTEGER NOT NULL);
create index "number_index" on "phonenumbers" ("number");
create table "contacts" ("id" SERIAL NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"user_id" INTEGER NOT NULL);
alter table "phonenumbers" add constraint "CONTACT_FK" foreign key("contact_id") references "contacts"("id") on update NO ACTION on delete CASCADE;
alter table "contacts" add constraint "USER_FK" foreign key("user_id") references "users"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

drop table "phonenumbers";
drop table "contacts";
drop table "users";
