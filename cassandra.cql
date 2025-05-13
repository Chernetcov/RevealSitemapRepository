CREATE KEYSPACE IF NOT EXISTS reveal WITH REPLICATION =
{ 'class' : 'SimpleStrategy',
'replication_factor' : '1'
};

CREATE TABLE IF NOT EXISTS reveal.authors (
    user_id uuid PRIMARY KEY,
    name text
);

INSERT INTO reveal.authors
    (user_id, name)
    VALUES (uuid(), 'Alexander');
INSERT INTO reveal.authors
    (user_id, name)
    VALUES (uuid(), 'Peter');

CREATE TABLE IF NOT EXISTS reveal.interests (
    name text PRIMARY KEY
);

INSERT INTO reveal.interests
    (name)
    VALUES ('Spy stories');
INSERT INTO reveal.interests
    (name)
    VALUES ('City legends');
INSERT INTO reveal.interests
    (name)
    VALUES ('Architecture and design');

CREATE TABLE IF NOT EXISTS reveal.story (
    story_id uuid,
    language text,
    name text,
    slug_name text,
    update_time timestamp,
    PRIMARY KEY ((story_id), language, slug_name)
);

INSERT INTO reveal.story
    (story_id, language, name, slug_name, update_time)
    VALUES (uuid(), 'en', 'Abandoned film set in a desert far, far away',
    'abandoned-film-set-in-a-desert-far-far-away', toTimestamp(now()));
INSERT INTO reveal.story
    (story_id, language, name, slug_name, update_time)
    VALUES (uuid(), 'en', 'The Secret Garden where the Force Awakens',
    'the-secret-garden-where-the-force-awakens', toTimestamp(now()));
INSERT INTO reveal.story
    (story_id, language, name, slug_name, update_time)
    VALUES (uuid(), 'ru', 'Дворец Дмитрия Кантемира (Дом Громова)',
    'dvorets-dmitriya-kantemira-dom-gromova', toTimestamp(now()));
INSERT INTO reveal.story
    (story_id, language, name, slug_name, update_time)
    VALUES (uuid(), 'ru', 'Елагин дворец',
    'elagin-dvorets', toTimestamp(now()));

CREATE TABLE IF NOT EXISTS reveal.collections (
    collection_id uuid,
    language text,
    name text,
    slug_name text,
    edited timestamp,
    stories set<text>,
    PRIMARY KEY ((collection_id), language, slug_name)
);

INSERT INTO reveal.collections
    (collection_id, language, name, slug_name, edited, stories)
    VALUES (uuid(), 'en', 'Star Wars Set Journey', 'star-wars-set-journey', toTimestamp(now()),
    {'abandoned-film-set-in-a-desert-far-far-away', 'the-secret-garden-where-the-force-awakens'});
INSERT INTO reveal.collections
    (collection_id, language, name, slug_name, edited, stories)
    VALUES (uuid(), 'ru', 'Петербург: Дворцы и их судьбы', 'peterburg-dvortsy-i-ih-sud-by', toTimestamp(now()),
    {'the-secret-garden-where-the-force-awakens', 'dvorets-dmitriya-kantemira-dom-gromova'});