-- Users table creation
drop table if exists public.Users cascade;
create table if not exists public.Users(
    id serial primary key,
    login varchar(40) not null unique,
    password varchar(40) not null
);

-- Files table creation
drop table if exists public.Files cascade;
create table if not exists public.Files(
    id serial primary key,
    name varchar(255) not null,
    content bytea not null,
    user_id integer not null references public.Users (id)
);