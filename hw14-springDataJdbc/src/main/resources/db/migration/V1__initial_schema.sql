drop table if exists address CASCADE;
drop table if exists client CASCADE;
drop table if exists phone CASCADE;
create table phone (id bigserial not null primary key, number varchar(255), client_id bigint not null );
create table address (id bigserial not null primary key, street varchar(255));
create table client (id bigserial not null primary key, name varchar(255), address_id bigint);
alter table client add constraint fk_client_address_id foreign key (address_id) references address;
alter table phone add constraint fk_phone_client_id foreign key (client_id) references client;
create index idx_client_address_id on client(address_id);