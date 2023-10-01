CREATE EXTENSION plpython3u;

CREATE OR REPLACE FUNCTION tweet_sent_vader_check(tweet text)
    RETURNS float
    LANGUAGE plpython3u
AS '
  import nltk.sentiment.vader;
  from nltk.sentiment.vader import SentimentIntensityAnalyzer;
  sid = SentimentIntensityAnalyzer();
  return sid.polarity_scores(tweet)["neg"]
';