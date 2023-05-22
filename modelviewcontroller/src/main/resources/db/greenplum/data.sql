CREATE EXTENSION IF NOT EXISTS plpythonu;

CREATE OR REPLACE FUNCTION tweet_sent_vader_check(tweet text)
    RETURNS float
    LANGUAGE plpythonu
AS '
  import nltk.sentiment.vader;
  from nltk.sentiment.vader import SentimentIntensityAnalyzer;
  sid = SentimentIntensityAnalyzer();
  return sid.polarity_scores(tweet)["neg"]
';