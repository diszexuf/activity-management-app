--liquibase formatted sql

--changeset diszexuf:1
create table if not exists intervals
(
    id         UUID primary key default gen_random_uuid(),
    start      integer    not null check ( start >= 0 and start <= 86400 ),
    "end"      integer    not null check ( "end" >= 0 and "end" <= 86400 ),
    type       varchar(5) not null check ( type in ('WORK', 'BREAK')),
    created_at timestamp        DEFAULT now(),

    constraint valid_interval check ( start < "end" )
);

create index if not exists idx_intervals_range on intervals (start, "end");

comment on table intervals is 'Временные интервалы активностей';
comment on column intervals.id is 'Уникальный идентификатор';
comment on column intervals.start is 'Начало интервала в секундах (0-86400)';
comment on column intervals."end" is 'Конец интервала в секундах (0-86400)';
comment on column intervals.type is 'Тип активности: WORK или BREAK';
comment on column intervals.created_at is 'Время создания записи';
--rollback drop table intervals

--changeset diszexuf:2
create index if not exists idx_intervals_end on intervals ("end");
create index if not exists idx_intervals_type on intervals (type);