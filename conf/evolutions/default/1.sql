# CallerID schema

# --- !Ups

CREATE SEQUENCE "s_CallRecord_id"
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE "s_CallRecord_id"
  OWNER TO "caller-id";

CREATE SEQUENCE "s_User_id"
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE "s_User_id"
  OWNER TO "caller-id";

CREATE TABLE "CallRecord"
(
  "telephoneNumber" character varying(128) NOT NULL,
  id bigint NOT NULL,
  "callDate" timestamp without time zone NOT NULL,
  CONSTRAINT "CallRecord_pkey" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "CallRecord"
  OWNER TO "caller-id";

CREATE INDEX idx469f0723
  ON "CallRecord"
  USING btree
  ("callDate");

  CREATE TABLE "User"
(
  username character varying(128) NOT NULL,
  "expiryDate" timestamp without time zone NOT NULL,
  id bigint NOT NULL,
  "refreshToken" character varying(128) NOT NULL,
  "accessToken" character varying(128) NOT NULL,
  CONSTRAINT "User_pkey" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "User"
  OWNER TO "caller-id";

CREATE UNIQUE INDEX idx23a1052d
  ON "User"
  USING btree
  (username COLLATE pg_catalog."default");

# --- !Downs

DROP INDEX idx23a1052d;
DROP TABLE "User";

DROP INDEX idx469f0723;
DROP TABLE "CallRecord";

DROP SEQUENCE "s_CallRecord_id";
DROP SEQUENCE "s_User_id";
