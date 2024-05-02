-- Users table fulfillment
delete from public.Users;
insert into public.Users(login, password)
values
    ('admin@mail.ru', 'admin_password'),
    ('developer@mail.ru', 'developer_password'),
    ('user@mail.ru', 'user_password');