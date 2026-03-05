
create table if not exists post(
  id int AUTO_INCREMENT primary key,
  title varchar(256) not null,
  text CLOB not null,
  likesCount integer not null);

create table if not exists post_comment(
  id int AUTO_INCREMENT primary key,
  text CLOB not null,
  post_id int,
  CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE);

create table if not exists tag(
  id int AUTO_INCREMENT primary key,
  name varchar(256) not null);


create table if not exists post_tags(
  post_id int,
  tag_id int,
  PRIMARY KEY (post_id, tag_id),
  CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE,
  CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE);


