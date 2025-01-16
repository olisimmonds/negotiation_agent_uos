create database intelligent_agents character set utf8;

use intelligent_agents;

create or replace table tournament_import (
    run_time double(7,3),
    round integer,
    exception varchar(1000),
    deadline varchar(10),
    agreement varchar(3),
    discounted varchar(3),
    agreeing integer,
    min_utility double(7,5),
    max_utility double(7,5),
    distance_pareto double(7,5),
    distance_nash double(7,5),
    social_welfare double(7,5),
    agent_1 varchar(10),
    agent_2 varchar(10),
    utility_1 double(10,9),
    utility_2 double(10,9),
    disc_utility_1 double(10,9),
    disc_utility_2 double(10,9),
    perc_utility_1 double(10,9),
    perc_utility_2 double(10,9),
    user_bother_1 double(7,5),
    user_bother_2 double(7,5),
    user_utility_1 double(10,9),
    user_utility_2 double(10,9),
    profile_1 varchar(30),
    profile_2 varchar(30)
);

load data infile '/Users/martin/Dropbox/University\ Of\ Southampton/COMP6203\ Intelligent\ Agents/Coursework/Results/all_logs_edited.csv'
    into table tournament_import
    fields terminated by ';'
    enclosed by '"'
    lines terminated by '\n'
    ignore 1 rows;
    
create or replace view successful as
select 
  cast(substring_index(substring_index(agent_1, '@', 1), 't', -1) as unsigned) as id_1,
  cast(substring_index(substring_index(agent_2, '@', 1), 't', -1) as unsigned) as id_2,
  true as completed,
  substring_index(agent_1, '@', 1) as agent_1,
  substring_index(agent_2, '@', 1) as agent_2,
  run_time,
  round,
  case when agreement = 'Yes' then true else false end as agreement,
  agreeing,
  utility_1,
  utility_2,
  distance_pareto,
  distance_nash,
  social_welfare,
  user_utility_1,
  user_utility_2,
  perc_utility_1,
  perc_utility_2,
  user_bother_1,
  user_bother_2,
  substring_index(profile_1, '_util', 1) as domain,
  null as failed_agent,
  null as failed_id,
  null as error_message
from 
  tournament_import 
where 
  exception = '0';
  
create view all_failed as select * from tournament_import where exception != '0';

create view exception_analysis as
select 
  trim(substring_index(exception, ':', 1)) as ex1, 
  trim(substring_index(substring_index(exception, ':', 2), ":", -1)) as ex2,
  trim(substring_index(substring_index(exception, ':', 3), ":", -1)) as ex3,
  trim(substring_index(substring_index(exception, ':', 4), ":", -1)) as ex4,
  trim(substring_index(substring_index(exception, ':', 5), ":", -1)) as ex5,
  trim(substring_index(substring_index(exception, ':', 6), ":", -1)) as ex6,
  af.*
from 
  all_failed af;
  
create view failure_messages as 
select 
  cast(substring_index(substring_index(ex3, '@', 1), 'Agent', -1) as unsigned) as failed_id,
  ex4 as error_message,
  ea.*
from
  exception_analysis ea
where 
  ex4 <> 'java.lang.NullPointerException' and ex4 <> 'java.lang.RuntimeException' and ex4 not like '[%'
union all 
select 
  cast(substring_index(substring_index(ex3, '@', 1), 'Agent', -1) as unsigned) as failed_id,
  concat(ex4, ": ", ex5) as error_message,
  ea.*
from
  exception_analysis ea
where 
  ex4 = 'java.lang.NullPointerException'
union all 
select 
  cast(substring_index(substring_index(ex3, '@', 1), 'Agent', -1) as unsigned) as failed_id,
  concat(ex4, ": ", ex5, " ", ex6) as error_message,
  ea.*
from
  exception_analysis ea
where 
  ex4 = 'java.lang.RuntimeException'
union all 
select 
  cast(substring_index(substring_index(ex3, '@', 1), 'Agent', -1) as unsigned) as failed_id,
  ex2 as error_message,
  ea.*
from
  exception_analysis ea
where 
  ex4 like '[%' 
;

create or replace view failed as
select 
  cast(substring_index(substring_index(agent_1, '@', 1), 't', -1) as unsigned) as id_1,
  cast(substring_index(substring_index(agent_2, '@', 1), 't', -1) as unsigned) as id_2,
  false as completed,
  substring_index(agent_1, '@', 1) as agent_1,
  substring_index(agent_2, '@', 1) as agent_2,
  run_time,
  round,
  case when agreement = 'Yes' then true else false end as agreement,
  agreeing,
  utility_1,
  utility_2,
  distance_pareto,
  distance_nash,
  social_welfare,
  user_utility_1,
  user_utility_2,
  perc_utility_1,
  perc_utility_2,
  user_bother_1,
  user_bother_2,
  substring_index(profile_1, '_util', 1) as domain,
  concat('Agent', failed_id) as failed_agent,
  failed_id,
  error_message
from 
  failure_messages; 
  
create table tournament_results as
select * from successful
union all
select * from failed;

update tournament_results
set distance_nash = 1
where completed = false;

drop table domain_scores;

create table domain_scores as
-- 2 --
select 2 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 2 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 2 or id_2 = 2
group by domain
-- 3 --
union
select 3 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 3 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 3 or id_2 = 3
group by domain
union
-- 4 -- 
select 4 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 4 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 4 or id_2 = 4
group by domain
union
-- 5 --
select 5 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 5 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 5 or id_2 = 5
group by domain
-- 6 --
union
select 6 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 6 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 6 or id_2 = 6
group by domain
union
-- 7 -- 
select 7 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 7 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 7 or id_2 = 7
group by domain
union
-- 9 --
select 9 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 9 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 9 or id_2 = 9
group by domain
-- 10 --
union
select 10 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 10 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 10 or id_2 = 10
group by domain
union
-- 12 -- 
select 12 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 12 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 12 or id_2 = 12
group by domain
union
-- 13 --
select 13 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 13 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 13 or id_2 = 13
group by domain
-- 14 --
union
select 14 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 14 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 14 or id_2 = 14
group by domain
union
-- 15 -- 
select 15 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 15 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 15 or id_2 = 15
group by domain
union
-- 17 --
select 17 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 17 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 17 or id_2 = 17
group by domain
union
-- 18 --
select 18 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 18 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 18 or id_2 = 18
group by domain
union
-- 19 --
select 19 as 
  agent, domain, 
  count(*) negotiations, 
  sum(completed) completed, 
  count(*) - sum(completed) failed, 
  avg(distance_nash) as nash_average,
  avg(case
  	when id_1 = 19 then utility_1
  	else utility_2
  end) utility_average
from tournament_results
where id_1 = 19 or id_2 = 19
group by domain
;

drop table domains;

create table domains (
  domain varchar(20) primary key,
  id char(4),
  main varchar(8),
  elicitation_cost varchar(4),
  domain_size integer,
  known_offers integer
);

load data infile '/Users/martin/Dropbox/University\ Of\ Southampton/COMP6203\ Intelligent\ Agents/Coursework/Results/domains.csv'
    into table domains
    fields terminated by ','
    enclosed by '"'
    lines terminated by '\n'
    ignore 1 rows;
 
drop table domain_ranking;
   
create table domain_ranking (
  agent integer,
  domain varchar(20),
  rank_utility integer,
  rank_nash integer,
  primary key (agent, domain)
);

load data infile '/Users/martin/Dropbox/University\ Of\ Southampton/COMP6203\ Intelligent\ Agents/Coursework/Results/domain_ranks.csv'
    into table domain_ranking
    fields terminated by ','
    enclosed by '"'
    lines terminated by '\n'
    ignore 1 rows;

alter table domain_scores 
add primary key (agent, domain);

alter table tournament_results
add index tr_id_1 (id_1);

alter table tournament_results
add index tr_id_2 (id_2);

alter table tournament_results
add index tr_domain (domain);

alter table tournament_results
add index tr_domain_id_1 (domain, id_1);

alter table tournament_results
add index tr_domain_id_2 (domain, id_2);

alter table tournament_results
add index tr_domain_ids (domain, id_1, id_2);

create or replace view bother_perc_utility as
select id_1, domain, user_bother_1, perc_utility_1
from tournament_results
union all
select id_2, domain, user_bother_2, perc_utility_2
from tournament_results
;

create table domain_bother_perc_utility as
select 
  id_1 as id, 
  domain, 
  avg(user_bother_1) as bother,
  avg(perc_utility_1) as perc_utility
from bother_perc_utility
group by id_1, domain;

alter table domain_bother_perc_utility
add primary key (id, domain);

create or replace view domain_score_ranking as
select 
  ds.agent,
  ds.domain,
  d.domain_size,
  d.known_offers,
  dr.rank_utility as utility_ranking, 
  ds.utility_average,
  dbpu.perc_utility as perc_utility_average,
  dr.rank_nash as nash_ranking,  
  ds.nash_average,
  dbpu.bother as bother_average,
  d.elicitation_cost, 
  ds.negotiations,
  ds.completed,
  ds.failed
from 
  domain_scores ds,
  domain_ranking dr,
  domains d,
  domain_bother_perc_utility dbpu 
where ds.agent = dr.agent 
  and ds.domain = dr.domain
  and dbpu.id = dr.agent 
  and dbpu.domain = dr.domain
  and d.domain = ds.domain;
  
select 
  domain, 
  concat(known_offers,'/',domain_size) offers_size,
  utility_ranking,
  utility_average,
  elicitation_cost 
from domain_score_ranking
where agent = 17
order by elicitation_cost, domain_size, known_offers;

