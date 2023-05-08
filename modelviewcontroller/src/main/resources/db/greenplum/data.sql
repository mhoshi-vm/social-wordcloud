
DROP VIEW IF EXISTS social_message_vader;
CREATE VIEW social_message_vader AS
SELECT message_id,
       create_date_time,
       origin,
       username,
       lang,
       context,
       tweet_sent_vader_check(context) as negative_sentiment
FROM social_message where context is not null;