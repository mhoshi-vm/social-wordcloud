
DROP FUNCTION IF EXISTS create_social_message_text(bigint, text);
CREATE FUNCTION create_social_message_text(id bigint, full_text text)
    RETURNS TABLE ( message_id text, text text) AS
$$
  import nltk
  tokens = nltk.word_tokenize(full_text)
  d = len(tokens)
  return ( ( str(id), tokens[i]) for i in range(0,d) )
$$ LANGUAGE 'plpythonu';


INSERT INTO social_message_text (message_id, text)
SELECT (f).message_id, (f).text
FROM (SELECT create_social_message_text(id::bigint, full_text) AS f
      FROM tweets
      where full_text is not null
        AND lang = 'en')a ;


INSERT INTO social_message (context, create_date_time, lang, origin, username, negative_sentiment, loc)
SELECT
    tweets.full_text AS context,
    tweets.created_at AS create_date_time,
    tweets.lang AS lang,
    'twitter_fake'AS origin,
    tweets.user_name AS username,
    tweet_sent_vader_check(tweets.full_text) AS negative_sentiment,
    CONCAT(usstates.name, ' ', coordinates::text) AS loc
FROM tweets LEFT JOIN usstates
ON json_typeof(coordinates) <> 'null' AND ST_Contains(usstates.geom,
        ST_Transform(ST_SetSRID( ST_GeomFromGeoJSON(coordinates::text), 4326), 4269))
WHERE full_text IS NOT NULL
  AND lang = 'en';
